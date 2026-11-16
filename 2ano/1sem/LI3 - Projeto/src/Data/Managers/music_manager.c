#include "Data/Managers/music_manager.h"

#include "Data/Entities/music.h"            // create_music, free_music, get_music_id, get_music_artist_ids, get_music_duration
#include "Data/Managers/artist_manager.h"   // artist_manager_contains
#include "Data/Managers/album_manager.h"    // album_manager_contains
#include "Validator/common.h"               // fieldValidator
#include "Validator/logical.h"              // validate_ids
#include "Validator/syntactic.h"            // validate_duration
#include "entity_interface.h"               // entityInterface
#include "entity_registry.h"                // entity_registry_register
#include "error_handler.h"                  // HANDLE_ERROR

#include <glib.h>                           // g_hash_table_new_full, g_str_hash, g_str_equal, g_hash_table_contains, g_hash_table_insert, g_hash_table_lookup, g_hash_table_destroy

static int validate_music_field      (char ** fields, int field_index);
static int validate_artists_ids_wrapper      (char ** fields, int field_index);
static int validate_album_id_wrapper (char ** fields, int field_index);
static void process_music_line       (char ** fields);
static GHashTable * music_table = NULL;
static MusicListener music_listeners [6] = {NULL};   


static const entityInterface MUSIC_INTERFACE = {
    .file_name               = "musics.csv",
    .error_file              = "resultados/musics_errors.csv",
    .error_header            = "\"id\";\"title\";\"artist_id\";\"album_id\";\"duration\";\"genre\";\"year\";\"lyrics\"\n",
    .field_count             = 8,
    .validate_field          = validate_music_field,
    .process_line            = process_music_line
};

static const fieldValidator MUSIC_VALIDATOR [] = {
    {0, NULL,              NULL },
    {1, NULL,              NULL },
    {2, NULL,              validate_artists_ids_wrapper },
    {3, NULL,              validate_album_id_wrapper },
    {4, validate_duration, NULL },
    {5, NULL,              NULL },
    {6, NULL,              NULL },
    {7, NULL,              NULL },
};

void music_manager_init (void) {
    music_table = g_hash_table_new_full (g_str_hash, g_str_equal, NULL, free_music);
    if (!music_table) HANDLE_ERROR ("Failed to create music table");

    entity_registry_register (&MUSIC_INTERFACE);
}

void music_manager_add (const char * id, Music music) {
    if (g_hash_table_contains (music_table, id)) {
        fprintf (stderr, "Warning: Music with ID '%s' already exists. Skipping.\n", id);
        return;
    }
    g_hash_table_insert (music_table, (gpointer)id, music);
}


Music music_manager_get (const char * id) {
    return (Music)g_hash_table_lookup (music_table, (gpointer)id);
}

gboolean music_manager_contains (const char * id) {
    return g_hash_table_contains (music_table, (gpointer)id);
}

void music_manager_destroy (void) {
    if (music_table) {
        g_hash_table_destroy (music_table);
        music_table = NULL;
    }
}

void set_func_music_listener (MusicListener function, int Query) {
    music_listeners [Query-1] = function;
}

static void process_music_line (char ** fields) {
    Music music = create_music (fields);
    // Add the music to the entity manager
    music_manager_add (get_music_id (music), music);
    for(int i=0;i<6;i++) if(music_listeners[i]) music_listeners[i](music);

}

static int validate_music_field (char ** fields, int field_index) {
    fieldValidator validator = MUSIC_VALIDATOR[field_index];

    if (validator.simple_validator) {
        return validator.simple_validator(fields[field_index]);
    }

    if (validator.complex_validator) {
        return validator.complex_validator(fields, field_index);
    }

    // if no validator is defined, return 1
    return 1;
}

static int validate_artists_ids_wrapper (char ** fields, int field_index) {
    return validate_ids (fields[field_index], artist_manager_contains);
}

static int validate_album_id_wrapper (char ** fields, int field_index) {
    return validate_id (fields[field_index], album_manager_contains);
}