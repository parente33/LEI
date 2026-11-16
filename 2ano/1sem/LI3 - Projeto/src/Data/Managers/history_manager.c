#include "Data/Managers/history_manager.h"

#include "Queries/query_manager.h"        // query1_add
#include "Queries/recomendador.h"
#include "Data/Entities/history.h"        // create_history, free_history, get_history_id
#include "Data/Entities/music.h"          // get_music_artist_ids
#include "Data/Managers/music_manager.h"  // music manager get
#include "Validator/common.h"             // fieldValidator
#include "Validator/syntactic.h"          // validate_plataform_type
#include "entity_interface.h"             // entityInterface
#include "entity_registry.h"              // entity_registry_register
#include "error_handler.h"                // HANDLE_ERROR

#include <glib.h>                         // g_hash_table_new_full, g_hash_table_contains, g_hash_table_insert, g_hash_table_lookup, g_hash_table_destroy

static int validate_history_field (char ** fields, int field_index);
static void process_history_data  (char ** fields);
GHashTable * history_table = NULL;
static HistoryListener history_listeners [6] = {NULL};   

static const entityInterface HISTORY_INTERFACE = {
    .file_name              = "history.csv",
    .error_file             = "resultados/history_errors.csv",
    .error_header           = "\"id\";\"user_id\";\"music_id\";\"timestamp\";\"duration\";\"platform\"\n",
    .field_count            = 6,
    .validate_field         = validate_history_field,
    .process_line           = process_history_data
};

static const fieldValidator HISTORY_VALIDATOR [] = {
    {0, NULL, NULL },
    {1, NULL, NULL },
    {2, NULL, NULL },
    {3, NULL, NULL },
    {4, NULL, NULL },
    {5, validate_plataform_type, NULL },
};

void history_manager_init (void) {
    history_table = g_hash_table_new_full (g_str_hash, g_str_equal, NULL, free_history);
    if (!history_table) HANDLE_ERROR ("Failed to create history table");

    entity_registry_register (&HISTORY_INTERFACE);
}

void history_manager_add (const char * id, History history) {
    if (g_hash_table_contains (history_table, id)) {
        fprintf (stderr, "Warning: History with ID '%s' already exists. Skipping.\n", id);
        return;
    }
    g_hash_table_insert (history_table, (gpointer)id, history);
}

History history_manager_get (const char * id) {
    return (History)g_hash_table_lookup (history_table, (gpointer)id);
}

gboolean history_manager_contains (const char * id) {
    return g_hash_table_contains (history_table, (gpointer)id);
}

void history_manager_destroy (void) {
    if (history_table) {
        g_hash_table_destroy (history_table);
        history_table = NULL;
    }
}

void set_func_history_listener (HistoryListener function, int Query) {
    history_listeners [Query-1] = function;
}

static void process_history_data (char ** fields) {
    History history = create_history (fields);
    history_manager_add (get_history_id (history), history);
    for(int i=0;i<6;i++) if(history_listeners[i]) history_listeners[i](history);
}

static int validate_history_field (char ** fields, int field_index) {
    fieldValidator validator = HISTORY_VALIDATOR[field_index];

    if (validator.simple_validator) {
        return validator.simple_validator(fields[field_index]);
    }

    if (validator.complex_validator) {
        return validator.complex_validator(fields, field_index);
    }

    // if no validator is defined, return 1
    return 1;
}