#include "Queries/query4.h"

#include "Data/Managers/history_manager.h"       // set_func_history_listener
#include "Queries/query_manager.h"               // set_ Query_funcs
#include "Data/Managers/music_manager.h"         // 
#include "Data/Managers/artist_manager.h"

#include "error_handler.h"                       // HANDLE_ERROR

#include <stdio.h>  // sscanf 
#include <time.h>   // mktime
#include <glib.h>   // GHashTable GTree

GHashTable *Tops_N = NULL; 
GHashTable *times_tops_N = NULL;
char *result_id = NULL;

void query4_manager_init(void) {
    Tops_N = g_hash_table_new_full   (g_str_hash, g_str_equal, free,(GDestroyNotify)g_tree_destroy);
    if (!Tops_N) HANDLE_ERROR ("Failed to create Tops N table");
   // set_func_history_listener (add_Tops_N,4);
    set_Query_funcs(query_4,4);
}

//Struct Used 
struct artist_time {
    const char *id;
    double time;
};


static Artist_time create_artist_time(const char *id, double time) {
    Artist_time artist = malloc(sizeof(struct artist_time));
    if (artist == NULL) {
        HANDLE_ERROR("Erro ao alocar memória para Artist_time.\n");
        return NULL;
    }

    artist->id = strdup(id);
    if (artist->id == NULL) {
        HANDLE_ERROR("Erro ao alocar memória para o ID.\n");
        free(artist);
        return NULL;
    }

    artist->time = time;

    return artist;
}


static void free_artist_time(Artist_time artist) {
    if(artist) {
        free((char *)artist->id); 
        free(artist);   
    }
}

/////// 

//Funcoes uteis 


static int dia_da_semana(int ano, int mes, int dia) {
    struct tm data = {0};
    data.tm_year = ano - 1900;
    data.tm_mon = mes - 1;
    data.tm_mday = dia;
    mktime(&data);
    return data.tm_wday;
}

static int tem_53_semanas(int ano) {
    return (dia_da_semana(ano, 1, 1) == 0 || dia_da_semana(ano, 12, 31) == 0);
}



static char * calcular_semana(const char *data_str) {
    int ano, mes, dia;
    sscanf(data_str, "%d/%d/%d", &ano, &mes, &dia);

    int ano_resultado, numero_semana;

    int primeiro_dia_semana = dia_da_semana(ano, 1, 1);

    int dias_para_primeiro_domingo = (7 - primeiro_dia_semana) % 7;
    int primeiro_domingo_ano = 1 + dias_para_primeiro_domingo;

    if (mes == 1 && dia <primeiro_domingo_ano) {
        ano_resultado = ano -1;
        numero_semana = tem_53_semanas(ano_resultado) ? 53 : 52;
        char *key = malloc(15);
        if (!key) {
            fprintf(stderr, "Erro ao alocar memória para key.\n");
            exit(EXIT_FAILURE);
        }
        sprintf(key, "%d%03d", ano_resultado, numero_semana); 
        return key;
    }
    struct tm data = {0};
    data.tm_year = ano - 1900;
    data.tm_mon = mes - 1;
    data.tm_mday = dia;
    time_t timestamp = mktime(&data);

    struct tm primeiro_domingo = {0};
    primeiro_domingo.tm_year = ano - 1900;
    primeiro_domingo.tm_mon = 0;
    primeiro_domingo.tm_mday = primeiro_domingo_ano;
    time_t timestamp_primeiro_domingo = mktime(&primeiro_domingo);

    int delta_dias = (timestamp - timestamp_primeiro_domingo) / (60 * 60 * 24);
    numero_semana = delta_dias / 7+1;
    if (mes == 12 && dia >= 25 && numero_semana >= 52 && tem_53_semanas(ano)) {
        numero_semana = 53;
    }
    char *key = malloc(15); 
    if (!key) {
        fprintf(stderr, "Erro ao alocar memória para key.\n");
        exit(EXIT_FAILURE);
    }
    sprintf(key, "%d%03d", ano, numero_semana); 
    return key;
}


static double minutos(const char *time) {
    int hours = 0, minutes = 0, seconds = 0;

    //  divide o tempo no formato HH:MM:SS
    sscanf(time, "%d:%d:%d", &hours, &minutes, &seconds);
 
    // Converte para minutos totais (incluindo fração de segundos)
    return hours * 60.0 + minutes + seconds / 60.0;
}

static void separate_date_hour(const char *input, char *date, char *hour) {
    char temp[20];
    strncpy(temp, input, sizeof(temp));
    temp[sizeof(temp) - 1] = '\0'; 

    char *token = strtok(temp, " ");
    if (token) {
        strcpy(date, token); 
    }

    token = strtok(NULL, " "); 
    if (token) {
        strcpy(hour, token);
    }
}

////////// 
// Guardar dados related 





static gint compare_artist_time(gconstpointer a, gconstpointer b, gpointer data) {
    const Artist_time artist_a = (const Artist_time)a;
    const Artist_time artist_b = (const Artist_time)b;

    // Se a comparação for por ID (apenas no contexto de busca)
    if (artist_a->id && artist_b->id && strcmp(artist_a->id, artist_b->id) == 0) {
        return 0; // IDs são iguais
    }

    // Comparar por time (max-heap behavior)
    if (artist_a->time != artist_b->time) {
        return (artist_b->time > artist_a->time) - (artist_b->time < artist_a->time);
    }

    // Desempate lexicográfico por ID
    return strcmp(artist_a->id, artist_b->id);
}

static void insert_or_update_artist(GTree *tree, const char *id, double new_time) {
    Artist_time temp = malloc(sizeof(struct artist_time));
    temp->id = id;  
    temp->time = 0; 

    Artist_time existing_artist = g_tree_lookup(tree, temp);

    if (existing_artist) {
        existing_artist->time += new_time;
        free(temp);
    } else {
        Artist_time new_artist = create_artist_time(id,new_time);
        g_tree_insert(tree, new_artist, new_artist);
        free(temp);
    }
}


void add_Tops_N(History history) {
    Music music = music_manager_get(get_history_music_id(history));
    char date[11];
    char hour[9];
    const char *timestamp = get_history_timestamp(history); 
    const char **ids = get_music_artist_ids (music); 
    const char *time = get_history_duration (history);
    separate_date_hour(timestamp, date,hour);
    char *key = calcular_semana(date);
    double new_time = minutos(time);
    GTree *tree = g_hash_table_lookup(Tops_N, key);
    if (!tree) {
        tree = g_tree_new_full(compare_artist_time, NULL, NULL, (GDestroyNotify)free_artist_time);
        g_hash_table_insert(Tops_N, key, tree);  
    }
    else free(key);

    for (size_t i = 0;ids[i]!=NULL; i++) {
        insert_or_update_artist(tree,ids[i],new_time);
    }
}

/////////// CALLBACK func
static gboolean add_most_tops(gpointer key, gpointer value, gpointer user_data){
    int valor = 1;
    int *N = user_data ; 
    if(*N<=0) return 1;
    Artist_time artist = (Artist_time)value; 
    int *exists = g_hash_table_lookup(times_tops_N,(gpointer)artist->id); 
    
    if(exists){
        valor = GPOINTER_TO_INT(exists) +1;
    }
    g_hash_table_insert(times_tops_N,(gpointer)artist->id, GUINT_TO_POINTER(valor));
    (*N)--;
    return 0;
}



static void helper (gpointer key, gpointer value, gpointer user_data) {
    GTree *tree =   value;
    int *dates = user_data;
    char *k = key;
    int x = atoi(k);
    int valor = 10;
    int *N =&valor;   
    if(!user_data || (x>=dates[0] && x<=dates[1])) {
        g_tree_foreach(tree,add_most_tops, N);
    }

}


static void func(gpointer key, gpointer value, gpointer user_data) {
    char *id = key;
    int *maior = user_data;
    int valor = GPOINTER_TO_INT(value);
    if(valor>*maior || (valor==*maior && strcmp(id,result_id))) {
        result_id = key;
        *maior = valor;
    }
}





Query_results query_4 (Query_info query_info) {
    //size_t n_arg = get_n_arguments(query_info);
    //if(n_arg<1) return NULL; 
    //const char **args = get_arguments(query_info);
    //times_tops_N = g_hash_table_new_full (g_str_hash, g_str_equal, NULL,NULL);
    //if(n_arg == 1) g_hash_table_foreach(Tops_N, helper,NULL);
    //else {
    //    char *inicio1= calcular_semana(args[0]);
    //    int inicio = atoi(inicio1); 
    //    char *fim1 = calcular_semana(args[1]);
    //    int fim = atoi (fim1);
    //    free(inicio1);
    //    free(fim1);
    //    int dates[2] = {inicio, fim};
    //    g_hash_table_foreach(Tops_N, helper,dates);
    //}
    //int value = 0;
    //int *maior = &value;
    //g_hash_table_foreach(times_tops_N, func,maior);
    //g_hash_table_destroy(times_tops_N);
    //if(!result_id) {
    //    times_tops_N = NULL;
    //    return NULL; 
    //}
    //Artist artist = artist_manager_get(result_id);
    //const char *type = get_artist_type(artist) ? "group" : "individual"; 
    //char **results = malloc (5*sizeof(char *));
    //results[0] = strdup(result_id); 
    //results[1] = strdup(type); 
    //results[2] = malloc(12); 
    //sprintf(results[2],"%d",*maior);
    //Query_results query_results = create_query_results((const char **)results,3);
    //result_id  = NULL;
    //times_tops_N = NULL;
    //return query_results;
    return NULL;
}

void query4_destroy(void) {
    if(Tops_N)g_hash_table_destroy(Tops_N);
    Tops_N = NULL;
}

