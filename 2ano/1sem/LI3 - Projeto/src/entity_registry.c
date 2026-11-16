#include "entity_registry.h"

#include <glib.h>           // For GPtrArray
#include "error_handler.h"  // HANDLE_ERROR

static GPtrArray * entities = NULL;

void entity_registry_init (void) {
    // If the entity registry is already initialized, return
    if (entities) return;

    entities = g_ptr_array_new ();
    if (!entities) HANDLE_ERROR ("Failed to initialize entity registry");
}

void entity_registry_register (const entityInterface * iface) {
    if (!iface) HANDLE_ERROR ("Received NULL entity interface");
    g_ptr_array_add (entities, (gpointer) iface);
}

size_t entity_registry_count (void) {
    return (entities ? entities->len : 0);
}

const entityInterface * entity_registry_get (size_t index) {
    if (index >= entity_registry_count ()) HANDLE_ERROR ("Index out of bounds in entity registry.");
    return (const entityInterface *) g_ptr_array_index (entities, index);
}

void entity_registry_destroy (void) {
    if (!entities) HANDLE_ERROR ("Entity registry is not initialized or already destroyed.");
    if (entities) {
        g_ptr_array_free (entities, TRUE);
        entities = NULL;
    }
}
