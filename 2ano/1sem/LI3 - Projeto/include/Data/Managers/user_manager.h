#ifndef USER_MANAGER_H
#define USER_MANAGER_H

#include "Data/Entities/user.h"

typedef void (*UserListener)   (User); // Listener

/**
 * @brief Initialize the user manager
 */
void user_manager_init         (void);

/**
 * @brief Adds an user to the manager.
 * @param id   ID of the user.
 * @param user Pointer to the user object.
 */
void user_manager_add          (const char * id, User user);

/**
 * @brief Get an user by ID.
 * @param id ID of the user.
 * @return Pointer to the user object or NULL if not found.
 */
User  user_manager_get        (const char * id);

/**
 * @brief Check if the user manager contains an user with the given ID.
 * @param id ID of the user.
 * @return TRUE if the user manager contains the user, FALSE otherwise.
 */
gboolean user_manager_contains (const char * id);

/**
 * @brief Destroy the user manager.
 */
void user_manager_destroy      (void);

/**
 * @brief Add a new function to the User Listener
 * @param function Function to add
 * @param Query Number of the query adding to the function
*/
void set_func_user_listener (UserListener function, int Query);

void user_manager_foreach_user_in_age_range(void (*callback)(User user, int min_age, int max_age), int min_age, int max_age);

#endif // USER_MANAGER_H