#include "Queries/query1.h"

#include "Queries/query_manager.h"                // get_arguments set_Query_funcs
#include "Data/Managers/album_manager.h"          // set_func_album_listener getters
#include "Data/Managers/history_manager.h"        // set_func_history_listener getters
#include "Data/Managers/music_manager.h"          // music_manager_get
#include "Data/Managers/artist_manager.h"         // artist_manager_get
#include "Data/Managers/user_manager.h"           // user_manager_get
#include "error_handler.h"                        // HANDLE_ERROR

#include <glib.h>                                 // GHashTable functions g_strv_length()
#include <stdio.h>                                // sprintf, snprintf
#include <stdlib.h>                               // For malloc, free
#include <string.h>                               // strdup 

// Query1 Manager 

GHashTable *album_num_table = NULL; 
GHashTable *revenue_table   = NULL;
static char *results[5];

void query1_manager_init (void) { 
    album_num_table = g_hash_table_new_full (g_str_hash, g_str_equal, NULL, NULL); 
    revenue_table   = g_hash_table_new_full (g_str_hash, g_str_equal,NULL, NULL); 
    if (!album_num_table)      HANDLE_ERROR ("Failed to create album_num table"); 
    if (!revenue_table)        HANDLE_ERROR ("Failed to create artists_revenue table");
    set_func_album_listener   (album_num_add,1);
    set_func_history_listener (artists_revenue_add,1);
    set_Query_funcs           (query_1, 1);

}

void album_num_add (Album album) {
    const char **ids = get_artists_album_id(album);
    for(size_t i=0;i<g_strv_length((char **)ids);i++) {
        int r=1;
        if( g_hash_table_contains(album_num_table, ids[i])) r = GPOINTER_TO_INT(g_hash_table_lookup(album_num_table, ids[i])) + 1;
            g_hash_table_insert  (album_num_table, (gpointer)ids[i], GINT_TO_POINTER(r));
    }
}

void artists_revenue_add (History history) {
    const char *music_id =get_history_music_id (history);
    Music music = music_manager_get (music_id);
    if(music == NULL) return;
    const char **ids = get_music_artist_ids(music);
    int size = g_strv_length((char **)ids); 
    for (int i=0;i<size;i++) {
        Artist artist = artist_manager_get(ids[i]);
        float r =get_artist_recipe_per_stream(artist);
        if( g_hash_table_contains(revenue_table, ids[i])) r += GPOINTER_TO_INT(g_hash_table_lookup(revenue_table, ids[i]));
        g_hash_table_insert  (revenue_table, (gpointer)ids[i], GINT_TO_POINTER(r));
        if(get_artist_type(artist)) {
            const char **constituents = get_id_constituents(artist); 
            for(size_t j=0;j<g_strv_length((char**)constituents);j++) {
                float g = get_artist_recipe_per_stream(artist);
                float t = g/ g_strv_length((char**)constituents); 
                if( g_hash_table_contains(revenue_table, constituents[j])) t += GPOINTER_TO_INT(g_hash_table_lookup(revenue_table, constituents[j]));
                g_hash_table_insert  (revenue_table, (gpointer)constituents[j], GINT_TO_POINTER(t));
            }
        }

    }
}

void query1_destroy (void) {
    if(album_num_table) {
        g_hash_table_destroy(album_num_table); 
        album_num_table = NULL; 
    }
    if(revenue_table) {
        g_hash_table_destroy(revenue_table); 
        revenue_table = NULL; 
    }
}

static void *query1_manager_get(const char *id, int p) {
    GHashTable *target_table = p ? revenue_table : album_num_table;
    if (p) {
        float *value_ptr = (float *)g_hash_table_lookup(target_table, (gpointer)id);
        return value_ptr ? value_ptr : NULL;  // Return pointer to float or NULL
    } else {
        int *value_ptr = (int *)g_hash_table_lookup(target_table, (gpointer)id);
        return value_ptr ? value_ptr : NULL;  // Return pointer to int or NULL
    }
}

static gboolean query1_manager_contains (const char * id,int p) {
    GHashTable *target_table = p ? revenue_table: album_num_table;
    return g_hash_table_contains (target_table, (gpointer)id);
}


//Query 1 solver

Query_results query_1(Query_info query_info) {
    if(!query_info) return NULL;
    const char **arguments = get_arguments(query_info);
    const char *id = arguments[0];
    if (user_manager_contains(id)) {
        User user = user_manager_get(id);
        results[0] = strdup(get_user_email(user));
        results[1] = strdup(get_user_first_name(user));
        results[2] = strdup(get_user_last_name(user));
        results[4] = strdup(get_user_country(user));
        int age = get_user_age(user);
        results[3] = malloc(12);
        sprintf(results[3], "%d", age);  
        Query_results query_results = create_query_results((const char **)results,5);
        for(int i=0;i<5;i++) free (results[i]); 
        return query_results;
    }
    if(artist_manager_contains(id)) {
        Artist artist = artist_manager_get(id); 
        results[0] = strdup(get_artist_name(artist));
        results[1] = strdup(get_artist_type(artist) ? "group" : "individual"); 
        results[2] = strdup(get_artist_country(artist));
        // Albums num
        int num_albuns = query1_manager_contains(id, 0) ? (int*)query1_manager_get(id, 0) : 0;
        results[3] = malloc(12);
        sprintf(results[3], "%d",num_albuns); 
        // Total recipe
        float total_revenue = query1_manager_contains(id, 1)? *(float*)query1_manager_get(id, 1) : 0.00;
        size_t size = snprintf(NULL,0,"%.2f",total_revenue) +1;
        results[4] = malloc(size);
        sprintf(results[4], "%.2f",total_revenue); 
        Query_results query_results = create_query_results((const char **)results,5);
        for(int i=0;i<5;i++) if(results[i]) free (results[i]); 
        return query_results;
    }
    return NULL; 
}
