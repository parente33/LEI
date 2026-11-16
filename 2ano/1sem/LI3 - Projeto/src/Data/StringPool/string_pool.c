#include "Data/StringPool/string_pool.h"

#include "error_handler.h" // HANDLE_ERROR

static GHashTable * string_pool = NULL;

void string_pool_init (void) {
    // If the string pool is already initialized, return
    if (string_pool)  return;

    string_pool = g_hash_table_new_full (g_str_hash, g_str_equal, NULL, g_free);
    if (!string_pool) HANDLE_ERROR ("Failed to initialize string pool");
}

const char * string_pool_get (const char * str) {
    if (!str) HANDLE_ERROR ("Received NULL string in string pool.");

    // Check if the string already exists in the pool
    gpointer existing = g_hash_table_lookup (string_pool, str);
    if (existing) return (const char *) existing;

    // Add a new string to the pool
    char * dup_str = g_strdup (str);
    if (!dup_str) HANDLE_ERROR ("Failed to duplicate string in string pool.");
    
    g_hash_table_insert (string_pool, dup_str, dup_str);
    return dup_str;
}

gboolean string_pool_contains (const char * str) {
    if (!str) HANDLE_ERROR ("Received NULL string");
    
    return g_hash_table_contains (string_pool, str);
}

void string_pool_destroy (void) {
    if (!string_pool) HANDLE_ERROR ("String pool is not initialized or already destroyed.");
    
    g_hash_table_destroy (string_pool);
    string_pool = NULL;
}
