#include "Data/Managers/user_manager.h"

#include "Validator/common.h"              // fieldValidator
#include "Validator/syntactic.h"           // validate_date, validate_email, validate_subscription
#include "Validator/logical.h"             // validate_ids
#include "Data/Entities/user.h"            // create_user, free_user, get_user_username
#include "Data/Managers/music_manager.h"   // music_manager_contains
#include "Queries/query3.h"                // updates_genre_likes_table
#include "entity_interface.h"              // entityInterface
#include "entity_registry.h"               // entity_registry_register
#include "error_handler.h"                 // HANDLE_ERROR

#include <glib.h>                          // g_hash_table_new_full, g_str_hash, g_str_equal, g_hash_table_contains, g_hash_table_insert, g_hash_table_lookup, g_hash_table_destroy

static int validate_user_field          (char ** fields, int field_index);
static int validate_liked_songs_wrapper (char ** fields, int field_index);
static void process_user_line           (char ** fields);
static GHashTable * user_table = NULL;
static UserListener user_listeners [6] = {NULL}; 

static const entityInterface USER_INTERFACE = {
    .file_name              = "users.csv",
    .error_file             = "resultados/users_errors.csv",
    .error_header           = "\"username\";\"email\";\"first_name\";\"last_name\";\"birth_date\";\"country\";\"subscription_type\";\"liked_songs_id\"\n",
    .field_count            = 8,
    .validate_field         = validate_user_field,
    .process_line           = process_user_line
};

static const fieldValidator USER_VALIDATOR [] = {
    {0, NULL,                  NULL },
    {1, validate_email,        NULL },
    {2, NULL,                  NULL },
    {3, NULL,                  NULL },
    {4, validate_date,         NULL },
    {5, NULL,                  NULL },
    {6, validate_subscription, NULL },
    {7, NULL,                  validate_liked_songs_wrapper },
};

void user_manager_init (void) {
    user_table = g_hash_table_new_full (g_str_hash, g_str_equal, NULL, free_user);
    if (!user_table) HANDLE_ERROR ("Failed to create user table");

    entity_registry_register (&USER_INTERFACE);
}

void user_manager_add (const char * id, User user) {
    if (g_hash_table_contains (user_table, id)) {
        fprintf (stderr, "Warning: User with ID '%s' already exists. Skipping.\n", id);
        return;
    }
    g_hash_table_insert (user_table, (gpointer)id, user);
}


User  user_manager_get (const char * id) {
    return (User)g_hash_table_lookup (user_table, (gpointer)id);
}

gboolean user_manager_contains (const char * id) {
    return g_hash_table_contains (user_table, (gpointer)id);
}

void user_manager_destroy (void) {
    if (user_table) {
        g_hash_table_destroy (user_table);
        user_table = NULL;
    }
}

void set_func_user_listener (UserListener function, int Query) {
    user_listeners [Query-1] = function;
}

static void process_user_line (char ** fields) {
    User user = create_user (fields);
    for(int i=0; i<6; i++) if(user_listeners[i]) user_listeners[i](user);

    // Add the user to the entity manager
    user_manager_add (get_user_username (user), user);
    
}

static int validate_user_field (char ** fields, int field_index) {
    fieldValidator validator = USER_VALIDATOR[field_index];

    if (validator.simple_validator) {
        return validator.simple_validator(fields[field_index]);
    }

    if (validator.complex_validator) {
        return validator.complex_validator(fields, field_index);
    }
    // if no validator is defined, return 1
    return 1;
}

static int validate_liked_songs_wrapper (char ** fields, int field_index) {
    return validate_ids (fields[field_index], music_manager_contains);
}

void user_manager_foreach_user_in_age_range(void (*callback)(User user, int min_age, int max_age), int min_age, int max_age) {
    if (!callback) return;

    GHashTableIter iter;
    gpointer key, value;
    g_hash_table_iter_init(&iter, user_table);

    while (g_hash_table_iter_next(&iter, &key, &value)) {
        User user = (User)value;

        int age = get_user_age(user);
        if (age >= min_age && age <= max_age) {
            callback(user, min_age, max_age);
        }
    }
}