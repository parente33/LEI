#include "Validator/logical.h"

#include <stdio.h>
#include <glib.h>
#include <string.h>

int validate_ids (const char * ids, gboolean (*entity_manager_contains)(const char *)) {
    // Fast check for empty list "[]"
    if (ids[0] == '[' && ids[1] == ']' && ids[2] == '\0') {
        return 1;
    }

    // Check for single ID (does not start with '[')
    if (ids[0] != '[') return entity_manager_contains(ids);

    // Initialize pointer to the first character after '['
    const char * ptr = ids + 1;
    
    while (*ptr != ']' && *ptr != '\0') {
        // Skip any leading spaces or single/double quotes
        while (*ptr == ' ' || *ptr == '\'' || *ptr == '\"') {
            ptr++;
        }

        // Start of the ID
        const char * start = ptr;

        // Find the end of the ID (next single/double quote, comma or closing bracket)
        while (*ptr != '\'' && *ptr != '\"' && *ptr != ',' && *ptr != ']' && *ptr != '\0') {
            ptr++;
        }

        // Calculate the length of the ID (max 8 characters)
        int len = ptr - start;
        if (len > 8) len = 8;

        // Temporarily store the ID with null-termination
        char id[9];
        memcpy(id, start, len);
        id[len] = '\0';

        // Validate the ID
        if (!entity_manager_contains(id)) return 0;

        // If current character is a comma, move past it
        if (*ptr == ',') {
            ptr++;
        } else if (*ptr == '\'' || *ptr == '\"') {
            ptr++;
            // After a quote, there might be a comma or space
            while (*ptr == ' ' || *ptr == ',') {
                if (*ptr == ',') {
                    ptr++;
                    break;
                }
                ptr++;
            }
        }
    }
    return 1;
}

int validate_id (const char * id, gboolean (*entity_manager_contains)(const char *)) {
    return entity_manager_contains (id);
}
