#ifndef USER_H
#define USER_H

#include <glib.h>

/**
 *  @brief Opaque data structure that represents a User object.
 */
typedef struct user * User;

/**
 * @brief Creates a User object from the given fields.
 * @return A pointer to a dynamically allocated User object.
 */
User create_user                 (char ** fields);

/**
 * @brief Frees the memory allocated for an User object.
 * @param data Pointer to the User object to be freed.
 */
void free_user                   (gpointer data);

/**
 * @brief Get the username of a User object
 * @param user Pointer to the User object
 * @return internal pointer to the username string
 */
const char * get_user_username   (User user);

/**
 * @brief Get the email of a User object
 * @param user Pointer to the User object
 * @return internal pointer to the email string
 */
const char * get_user_email      (User user);

/**
 * @brief Get the first name of a User object
 * @param user Pointer to the User object
 * @return internal pointer to the first name string
 */
const char * get_user_first_name (User user);

/**
 * @brief Get the last name of a User object
 * @param user Pointer to the User object
 * @return internal pointer to the last name string
 */
const char * get_user_last_name  (User user);

/**
 * @brief Get the age of a User object
 * @param user Pointer to the User object
 * @return the age of the user
 */
int get_user_age                 (User user);

/**
 * @brief Get the country of a User object
 * @param user Pointer to the User object
 * @return internal pointer to the country string
 */
const char * get_user_country    (User user);

/**
 * @brief Get the liked songs of a User object
 * @param user Pointer to the User object
 * @return Pointer to an array of strings (each string is a song ID)
*/
const char ** get_user_liked_songs (User user);

#endif // USER_H