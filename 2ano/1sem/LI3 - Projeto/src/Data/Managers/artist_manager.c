#include "Data/Managers/artist_manager.h"

#include "Data/Entities/artist.h"        // Artist, create_artist, free_artist, get_artist_id
#include "Queries/query_manager.h"
#include "Validator/common.h"            // fieldValidator
#include "Validator/syntactic.h"         // validate_artist_type
#include "error_handler.h"               // HANDLE_ERROR
#include "entity_interface.h"            // entityInterface 
#include "entity_registry.h"             // entity_registry_register

#include <glib.h>                        // g_hash_table_new_full, g_hash_table_contains, g_hash_table_insert, g_hash_table_lookup, g_hash_table_destroy

static int validate_artist_field        (char ** fields, int field_index);
static int validate_artist_type_wrapper (char ** fields, int field_index);
static void process_artist_line         (char ** fields);
static GHashTable * artist_table = NULL;
static ArtistListener artist_listeners [6] = {NULL}; 

static const entityInterface ARTIST_INTERFACE = {
    .file_name              = "artists.csv",
    .error_file             = "resultados/artists_errors.csv",
    .error_header           = "\"id\";\"name\";\"description\";\"recipe_per_stream\";\"id_constituent\";\"country\";\"type\"\n",
    .field_count            = 7,
    .validate_field         = validate_artist_field,
    .process_line           = process_artist_line
};

static const fieldValidator ARTIST_VALIDATOR [] = {
    {0, NULL, NULL },
    {1, NULL, NULL },
    {2, NULL, NULL },
    {3, NULL, NULL },
    {4, NULL, NULL },
    {5, NULL, NULL },
    {6, NULL, validate_artist_type_wrapper },
};

void artist_manager_init (void) {
    artist_table = g_hash_table_new_full (g_str_hash, g_str_equal, NULL, free_artist);
    if (!artist_table) HANDLE_ERROR ("Failed to create artist table");

    entity_registry_register (&ARTIST_INTERFACE);
}

void artist_manager_add (const char * id, Artist artist) {
    if (g_hash_table_contains (artist_table, id)) {
        fprintf (stderr, "Warning: Artist with ID '%s' already exists. Skipping.\n", id);
        return;
    }
    g_hash_table_insert (artist_table, (gpointer)id, artist);
}

Artist artist_manager_get (const char * id) {
    return (Artist)g_hash_table_lookup (artist_table, (gpointer)id);
}

gboolean artist_manager_contains (const char * id) {
    return g_hash_table_contains (artist_table, (gpointer)id);
}

void artist_manager_destroy (void) {
    if (artist_table) {
        g_hash_table_destroy (artist_table);
        artist_table = NULL;
    }
}

static void process_artist_line (char ** fields) {
    Artist artist = create_artist (fields);
    artist_manager_add (get_artist_id (artist), artist);
}

void set_func_artist_listener (ArtistListener function, int Query) {
    artist_listeners [Query-1] = function;
}

static int validate_artist_field (char ** fields, int field_index) {
    fieldValidator validator = ARTIST_VALIDATOR[field_index];

    if (validator.simple_validator)
        return validator.simple_validator(fields[field_index]);

    if (validator.complex_validator)
        return validator.complex_validator(fields, field_index);
    
    // if no validator is defined, return 1
    return 1;
}

static int validate_artist_type_wrapper (char ** fields, int field_index) {
    // type - validate artist type and also validate if the artist is individual and has no constituents
    return validate_artist_type (fields[field_index], fields[4]);
}
