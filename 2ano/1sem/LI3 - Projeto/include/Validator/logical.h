#ifndef LOGICAL_H
#define LOGICAL_H

#include <glib.h> // gboolean

/**
 * @brief Validate a string and check against the specific entity manager to verify if it exists.
 * @param ids                     ids validate.
 * @param entity_manager_contains function that ch
 * @return 1 if valid, 0 if invalid.
 */
int validate_ids (const char * ids, gboolean(*entity_manager_contains)(const char *));

/**
 * @brief Validates an id against a entity manager.
 * 
 * @param id                      album id to validate.
 * @param entity_manager_contains function that checks if the id exists.
 * 
 * @return 1 if valid, 0 if invalid.
 */
int validate_id (const char * album_id, gboolean(*entity_manager_contains)(const char *));

#endif // LOGICAL_H