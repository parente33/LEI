#ifndef STRING_POOL_H
#define STRING_POOL_H

#include <glib.h>

/**
 * @brief Initialize the string pool.
 */
void string_pool_init         (void);

/**
 * @brief Adds or gets a string from the pool. Adds the string if it doesn't exist, duplicating the received string. Gets the string if it already exists.
 * @param str String to add.
 * @return Pointer to the string in the pool.
 */
const char * string_pool_get  (const char * str);

/**
 * @brief Checks if a string is in the pool.
 * @param str String to check.
 * @return TRUE if the string is in the pool, FALSE otherwise.
 */
gboolean string_pool_contains (const char * str);

/**
 * @brief Destroy the string pool.
 */
void string_pool_destroy      (void);

#endif // STRING_POOL_H
