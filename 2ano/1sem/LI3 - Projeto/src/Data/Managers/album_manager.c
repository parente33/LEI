#include "Data/Managers/album_manager.h" 

#include "Data/Entities/album.h"        // Album, create_album, free_album, get_album_id
#include "Queries/query_manager.h"
#include "entity_interface.h"           // entityInterface
#include "entity_registry.h"            // entity_registry_register
#include "error_handler.h"              // HANDLE_ERROR

#include <glib.h>                       // g_hash_table_new_full, g_hash_table_contains, g_hash_table_insert, g_hash_table_lookup, g_hash_table_destroy

// static int validate_album_field (char ** fields, int field_index);
static GHashTable * album_table = NULL;  
static AlbumListener album_listeners [6] = {NULL};
static void process_album_line (char ** fields);


static const entityInterface ALBUM_INTERFACE = {
    .file_name              = "albums.csv",
    .error_file             = "resultados/albums_errors.csv",
    .error_header           = "\"id\";\"title\";\"artists_id\";\"year\";\"producers\"\n",
    .field_count            = 5,
    .validate_field         = NULL, // validate_album_field,
    .process_line           = process_album_line
};

void album_manager_init (void) {
    album_table = g_hash_table_new_full (g_str_hash, g_str_equal, NULL, free_album);
    if (!album_table) HANDLE_ERROR ("Failed to create album table");

    entity_registry_register (&ALBUM_INTERFACE);
}

void album_manager_add (const char * id, Album album) {
    if (g_hash_table_contains (album_table, id)) {
        fprintf (stderr, "Warning: Album with ID '%s' already exists. Skipping.\n", id);
        return;
    }
    g_hash_table_insert (album_table, (gpointer)id, album);
}

Album  album_manager_get (const char * id) {
    return (Album)g_hash_table_lookup (album_table, (gpointer)id);
}

gboolean album_manager_contains (const char * id) {
    return g_hash_table_contains (album_table, (gpointer)id);
}

void album_manager_destroy (void) {
    if (album_table) {
        g_hash_table_destroy (album_table);
        album_table = NULL;
    }
}

void set_func_album_listener (AlbumListener function, int Query) {
    album_listeners[Query-1] = function;
}

static void process_album_line (char ** fields) {
    Album album = create_album (fields);
    album_manager_add (get_album_id (album), album);
    for(int i=0; i<6; i++) if(album_listeners[i]) album_listeners[i](album);
}

// static int validate_album_field (char ** fields, int field_index) { return 1; }
