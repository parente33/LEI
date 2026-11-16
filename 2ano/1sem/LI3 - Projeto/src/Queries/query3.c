#include "Queries/query3.h"

#include "Queries/query_manager.h"
#include "Data/Managers/user_manager.h"
#include "Data/Managers/music_manager.h"
#include "Data/Entities/music.h"
#include "Data/Entities/user.h"
#include "error_handler.h"
#include "utils.h"

#include <glib.h>
#include <stdlib.h>
#include <stdio.h>

// Data structure to hold genre counts for Query 3
// Holds the genre name and the total likes
typedef struct genrecount {
    char * genre;
    int total_likes;
} GenreCount;

GHashTable * genre_likes_table = NULL;

void query3_managar_init (void) {
    genre_likes_table = g_hash_table_new(g_str_hash, g_str_equal);
    if (!genre_likes_table)     HANDLE_ERROR ("Failed to create genre_likes table");
    //set_func_user_listener      (updates_genre_likes_table, 3);
    set_Query_funcs             (query_3, 3);
}

void query3_destroy (void) {
    if (genre_likes_table) {
        g_hash_table_destroy(genre_likes_table);
        genre_likes_table = NULL;
    }
}

void updates_genre_likes_table (User user, int min_age, int max_age) {
    if (!user) return;

    int age = get_user_age(user);
    if (age < min_age || age > max_age) return;

    const char ** liked_songs = get_user_liked_songs(user);
    if (!liked_songs) return;

    for (int i = 0; liked_songs[i] != NULL; i++) {
        const char *song_id = liked_songs[i];
        Music music = music_manager_get(song_id);
        if (!music) return;

        const char *genre = get_music_genre(music);
        if (!genre) {
            return;
        }
        
        // Updates the genre_likes table
        int *likes = g_hash_table_lookup(genre_likes_table, genre);
        if (!likes) {
            likes = g_malloc(sizeof(int));
            *likes = 0;
            g_hash_table_insert(genre_likes_table, (gpointer)genre, likes);
        }
        (*likes)++;
    }
}

// Comparator function for sorting genres: Descending by total_likes, ascending alphabetically
static gint genre_count_compare(gconstpointer a, gconstpointer b, gpointer user_data) {
    const GenreCount * gc1 = a;
    const GenreCount * gc2 = b;

    if (gc1->total_likes != gc2->total_likes) return gc2->total_likes - gc1->total_likes; // Descending order

    return strcmp(gc1->genre, gc2->genre); // Alphabetical order
}

// Query 3 - Most Popular Genres Within Age Range
Query_results query_3 (Query_info query_info) {
    if (!query_info) {
        HANDLE_ERROR ("Query_info is NULL for Query 3");
        return NULL;
    }

    if (get_n_arguments(query_info) < 2) {
        HANDLE_ERROR ("Insufficient arguments for Query 3");
        return NULL;
    }

    // Extracts arguments from query_info
    int min_age = atoi((get_arguments(query_info))[0]);
    int max_age = atoi((get_arguments(query_info))[1]);
    if (!validate_age_range(min_age, max_age)) {
        HANDLE_ERROR ("Invalid age range");
        return NULL;
    }

    if (genre_likes_table) {
        g_hash_table_destroy(genre_likes_table);
        genre_likes_table = NULL;
    }
    genre_likes_table = g_hash_table_new_full(g_str_hash, g_str_equal,NULL,g_free);
    if (!genre_likes_table) {
        HANDLE_ERROR("Failed to reset genre_likes_table");
        return NULL;
    }

    user_manager_foreach_user_in_age_range(updates_genre_likes_table, min_age, max_age);

    // GList to store the ordered genres
    GList *genre_list = NULL;

    GHashTableIter iter;
    gpointer key, value;
    g_hash_table_iter_init (&iter, genre_likes_table);
    while (g_hash_table_iter_next(&iter, &key, &value)) {
        GenreCount *gco = g_new(GenreCount, 1);
        gco->genre = g_strdup((const char *)key);
        gco->total_likes = *((int *)value);
        genre_list = g_list_prepend(genre_list, gco);
    }

    genre_list = g_list_sort_with_data(genre_list, genre_count_compare, NULL);

    // Number of genres
    size_t count = g_list_length(genre_list);

    // Stores results in an array of strings (in the format "genre;total_likes")
    char **results = g_malloc(sizeof(char *) * count * 2);
    size_t i = 0;
    for (GList *l = genre_list; l != NULL; l = l->next) {
        GenreCount *gco = l->data;

        // Formats the result as intended
        results[i]   = g_strdup_printf("%s", gco->genre);
        results[i+1] = g_strdup_printf("%d\n",gco->total_likes);
        g_free(gco->genre);
        g_free(gco);
        i += 2;
    }
    g_list_free(genre_list);

    Query_results query_results = create_query_results((const char **)results, count * 2);

    for (size_t j = 0; j < count * 2; j++) {
        g_free(results[j]);
    }
    g_free(results);

    return query_results;
}