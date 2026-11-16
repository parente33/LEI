#include "Queries/query2.h"

#include "utils.h"                                // concat_strings remove_quotes seconds
#include "Data/Managers/music_manager.h"          // set_func_music_listener get_music_artists_ids get_music_duration
#include "Data/Managers/artist_manager.h"         // artist_manager_get e getters do artist
#include "Queries/query_manager.h"                // set_Query_funcs get_n_arguments get_arguments create_query_results
#include "error_handler.h"                        // HANDLE_ERROR



#include <glib.h>
#include <stdlib.h>
#include <stdio.h> 


GHashTable *add_discography = NULL;
GTree *discography = NULL;
static char **results ;


struct id_disco {
    const char *id; 
    const char *name;
    const char *country; 
    const char *type;
    int discography;
};

struct traverse_data {
    int N; 
    int args; // number of elements added 
    const char *country;
};


// Structs 
static Id_Disco create_id_disco(const char *id,const char *name, int valor,int type,const char *country) {
    Id_Disco new = malloc(sizeof(struct id_disco)); 
    if(!new) HANDLE_ERROR("Failed to create id_disco \n");
    new->discography = valor; 
    new->id = strdup(id);
    if(!country) new->country = NULL;
    else new->country = strdup(country);
    new->type= strdup(type ? "group" : "individual");
    if(name) new->name = strdup(name);
    else new->name = NULL;
    return new; 
}

static void clean_id_disco (Id_Disco id_disco) {
    free((char*)id_disco->type);
    free((char*)id_disco->country);
    free((char*)id_disco->id);
    free((char*)id_disco->name);
    free(id_disco);
}


void query2_manager_init(void) {
    add_discography = g_hash_table_new_full (g_str_hash, g_str_equal, NULL, (GDestroyNotify)clean_id_disco);
    if (!add_discography) HANDLE_ERROR    ("Failed to create add_discography tree"); 
    set_func_music_listener           (query2_add,2);
    set_Query_funcs                   (query_2,2); 
    
}


static gint compare_id_disco (gconstpointer a, gconstpointer b, gpointer user_data){
    const Id_Disco artist_a = (Id_Disco)a;
    const Id_Disco artist_b = (Id_Disco)b;
    if(strcmp(artist_a->id,artist_b->id) ==0) return 0; // artistas sao iguais 

    if (artist_a->discography > artist_b->discography) return -1; // artist_a comes before artist_b
    if (artist_a->discography < artist_b->discography) return 1;  // artist_a comes after artist_b

    // If total_duration is equal, sort by ascending order of id
    return strcmp(artist_a->id, artist_b->id);
}



static gboolean traverse_callback(gpointer key, gpointer value, gpointer user_data) {
    Traverse_Data  data = (Traverse_Data )user_data;
    if (data->N==0) return TRUE; // Stop traversal
    Id_Disco artist = (Id_Disco)value;
    if(!data->country || strcmp(artist->country, data->country)==0){
        // Seconds to hours
        char duration[16]; // "HH:MM:SS" + '\0'
        int duration_ = artist->discography; 
        int hours = duration_ / 3600;
        int minutes = (duration_ % 3600) / 60;
        int secs = duration_ % 60;
        snprintf(duration, 16, "%02d:%02d:%02d", hours, minutes, secs);
        int len = strlen(artist->country)+2;
        char country_new_line[len]; 
        snprintf(country_new_line, len,"%s\n", artist->country);
        int count = data->args;
        results[count++] = strdup (artist ->name);
        results[count++] = strdup (artist ->type);
        results[count++] = strdup (duration);
        results[count++] = strdup (country_new_line);
        data->args = count; 
        data->N--; 
    }
    return FALSE; // Continue traversal
}



// ADDERS
void query2_add(Music music) {
    const char **id = get_music_artist_ids(music);
    int valor = segundos(get_music_duration(music));
    for(int i=0;id[i]!=NULL;i++) {
    Id_Disco exists = g_hash_table_lookup(add_discography,id[i]); 
    if(exists) {
        int x= exists->discography; 
        exists->discography = x+valor;
    }
    else {
        Artist artist = artist_manager_get(id[i]);
        Id_Disco new  = create_id_disco (id[i], get_artist_name(artist), valor,get_artist_type(artist),get_artist_country(artist));
        g_hash_table_insert(add_discography,(gpointer)id[i],new);
    }
}
}

static void add_tree(gpointer key,gpointer value, gpointer user_data) {
    Id_Disco id_disco = (Id_Disco) value;
    g_tree_insert(discography, id_disco,id_disco);
}



/// Solver 

Query_results query_2(Query_info query_info) {
    size_t n = get_n_arguments(query_info);
    if(n<1) return NULL;
    const char **args = get_arguments(query_info);
    int N = atoi(args[0]);
    if(N==0) return NULL;
    char *country = NULL;
    Traverse_Data data = malloc(sizeof(struct traverse_data));
    data->N = N;
    data->args = 0;
    if(n==1) data->country = NULL;
    else {
    char *country_quotes = concat_strings(args,1,n-1);
    country = remove_quotes(country_quotes);
    data->country = strdup(country);
    free(country_quotes);
    }
    if(country) free(country);
    results = malloc(N*4*sizeof(char*));
    if(!discography) discography = g_tree_new_full(compare_id_disco,NULL,NULL,NULL);
    g_hash_table_foreach(add_discography,add_tree, data);
    g_tree_foreach(discography, traverse_callback,data);
    Query_results query_results = create_query_results((const char **)results,(data->args));
     for(int i=0;i<data->args;i++) {
        if(results[i]) free(results[i]);
    }
    if(results)free(results);
    if(data->country) free((char*)data->country);
    if(data)free(data);
    return query_results;

}

// Destroyer
void query2_destroy(void) {
  if(add_discography){ 
    g_hash_table_destroy(add_discography);
   add_discography = NULL;
  }
   if(discography) {
    g_tree_destroy(discography);
    discography = NULL;}
}