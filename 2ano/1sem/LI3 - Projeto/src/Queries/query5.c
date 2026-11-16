#include "Queries/recomendador.h"

#include "Data/Entities/history.h"
#include "Data/Entities/music.h"
#include "Data/Managers/history_manager.h"
#include "Data/Managers/music_manager.h"
#include "Queries/query_manager.h"
#include "error_handler.h"

#include <glib.h>

// Used to efficiently map userIDs and genre names to their indices in the classification matrix
static GHashTable *userIndices = NULL;
static GHashTable *genreIndices = NULL;
//-----//
static int **musicClassificatonMatrix = NULL;
static char **genreNames = NULL;
static char **userIDs = NULL;

void query5_manager_init (void) {
    userIndices = g_hash_table_new_full(g_str_hash, g_str_equal, free, NULL);
    if (!userIndices)           HANDLE_ERROR ("Failed to create userIndices table");

    genreIndices = g_hash_table_new_full(g_str_hash, g_str_equal, free, NULL);
    if (!genreIndices)          HANDLE_ERROR ("Failed to create genreIndices table");
    
    set_func_history_listener   (update_classification_matrix, 5);
    set_Query_funcs             (query_5, 5);
}

void update_classification_matrix(History history) {
    if (!history) return;

    const char *userID = get_history_user_id(history);
    const char *musicID = get_history_music_id(history);
    if (!userID || !musicID) return;

    Music music = music_manager_get(musicID); // Recupera o ponteiro diretamente
    if (!music) return; // Verifica se o ponteiro é válido
    
    const char *musicGenre = get_music_genre(music); // Passa o ponteiro diretamente
    if (!musicGenre) return;

    // Obtém ou adiciona o índice do usuário
    int userRow;
    if (!g_hash_table_contains(userIndices, userID)) {
        userRow = g_hash_table_size(userIndices);
        userIDs = realloc(userIDs, (userRow + 1) * sizeof(char *));
        userIDs[userRow] = strdup(userID);

        // Expande as linhas da matriz
        musicClassificatonMatrix = realloc(musicClassificatonMatrix, (userRow + 1) * sizeof(int *));
        musicClassificatonMatrix[userRow] = calloc(g_hash_table_size(genreIndices), sizeof(int));

        g_hash_table_insert(userIndices, userIDs[userRow], GINT_TO_POINTER(userRow));
    } else {
        userRow = GPOINTER_TO_INT(g_hash_table_lookup(userIndices, userID));
    }

    // Obtém ou adiciona o índice do gênero
    int genreCol;
    if (!g_hash_table_contains(genreIndices, musicGenre)) {
        genreCol = g_hash_table_size(genreIndices);
        genreNames = realloc(genreNames, (genreCol + 1) * sizeof(char *));
        genreNames[genreCol] = strdup(musicGenre);

        // Expande as colunas para todos os usuários
        for (int i = 0; i < (int)g_hash_table_size(userIndices); i++) {
            musicClassificatonMatrix[i] = realloc(musicClassificatonMatrix[i], (genreCol + 1) * sizeof(int));
            musicClassificatonMatrix[i][genreCol] = 0;
        }

        g_hash_table_insert(genreIndices, genreNames[genreCol], GINT_TO_POINTER(genreCol));
    } else {
        genreCol = GPOINTER_TO_INT(g_hash_table_lookup(genreIndices, musicGenre));
    }

    // Incrementa o contador para o usuário e o gênero
    musicClassificatonMatrix[userRow][genreCol]++;
}


void query5_destroy (void) {
    if(userIDs)free(userIDs);
    if(genreNames)free(genreNames);
    if(musicClassificatonMatrix )free(musicClassificatonMatrix);

    if (userIndices!=NULL) {
        g_hash_table_destroy(userIndices);
        userIndices = NULL;
    }
    if (genreIndices!=NULL){
         g_hash_table_destroy(genreIndices);
         genreIndices= NULL;
    }
}

char **recomendaUtilizadores(char *idUtilizadorAlvo,
                             int **matrizClassificacaoMusicas,
                             char **idsUtilizadores, char **nomesGeneros,
                             int numUtilizadores, int numGeneros,
                             int numRecomendacoes) {
                                                       
    int alvoIndex = -1;
    // Find target's index
    for (int i = 0; i < numUtilizadores; i++) {
        if (strcmp(idsUtilizadores[i], idUtilizadorAlvo) == 0) {
            alvoIndex = i;
            break;
        }
    }
    if (alvoIndex == -1) HANDLE_ERROR ("Target user not found in matrix.");

    // Identify target's most listened-to genre
    int maxGenreCount = 0;
    int targetGenre = -1;
    for (int genre = 0; genre < numGeneros; genre++) {
        if (matrizClassificacaoMusicas[alvoIndex][genre] > maxGenreCount) {
            maxGenreCount = matrizClassificacaoMusicas[alvoIndex][genre];
            targetGenre = genre;
        }
    }
    if (targetGenre == -1) HANDLE_ERROR ("Target user doesn't listen to music.");

    // Allocate memory for the top recommendations array
    char **recommendations = malloc(numRecomendacoes * sizeof(char *));
    int *topCounts = calloc(numRecomendacoes, sizeof(int)); // For tie-breaking purposes
    for (int i = 0; i < numRecomendacoes; i++) {
        recommendations[i] = NULL;
        topCounts[i] = -1;
    }

    // Find top N users for the targeted genre
    for (int user = 0; user < numUtilizadores; user++) {
        if (user == alvoIndex) continue;

        int count = matrizClassificacaoMusicas[user][targetGenre];
        if (count == 0) continue;

        // Check if the user found should be included in the top N
        for (int i = 0; i < numRecomendacoes; i++) {
            if (recommendations[i] == NULL || count > topCounts[i] ||
                (count == topCounts[i] && strcmp(idsUtilizadores[user], recommendations[i]) < 0)) {
                    // Shifts "lower-ranked recommendations down"
                    for (int j = numRecomendacoes - 1; j > i; j--) {
                        recommendations[j] = recommendations[j - 1];
                        topCounts[j] = topCounts[j - 1];
                    }

                    // Inserts new recommendation
                    recommendations[i] = idsUtilizadores[user];
                    topCounts[i] = count;
                    break;
            }
        }
    }

    free(topCounts);
    return recommendations;
}

Query_results query_5 (Query_info query_info) {
    if (!query_info || get_n_arguments(query_info) != 2) {
        HANDLE_ERROR ("Query 5 requirements are off");
        return NULL;
    }

    // Get the arguments of the query
    const char *userID = get_arguments(query_info)[0];
    int numRecomendacoes = atoi(get_arguments(query_info)[1]);
    if (numRecomendacoes < 0) {
        HANDLE_ERROR ("The number of recommendations has to be atleast 0");
        return NULL;
    }

    // Get the sizes of the classification matrix
    int numUtilizadores = (int)g_hash_table_size(userIndices);
    int numGeneros = (int)g_hash_table_size(genreIndices);

    if (numUtilizadores == 0 || numGeneros == 0) {
        return NULL;
    }

    // Calls the recomendaUtilizadores function
    // Also transforms the result into the Query_results structure
    char **resultados = recomendaUtilizadores(
        (char *)userID,
        musicClassificatonMatrix,
        userIDs,
        genreNames,
        numUtilizadores,
        numGeneros,
        numRecomendacoes
    );

    if (!resultados) {
        HANDLE_ERROR ("Failed to generate recommendations");
        return NULL;
    }

    Query_results query_results = create_query_results((const char **)resultados, numRecomendacoes);

    for (int i = 0; i < numRecomendacoes; i++) {
        if (resultados[i]) free(resultados[i]);
    }
    free(resultados);
    
    return query_results;
}