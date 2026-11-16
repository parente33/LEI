#include "Data/Entities/user.h"             // User

#include "IO/parser.h"                      // parse_ids_list
#include "Data/StringPool/string_pool.h"    // string_pool_get
#include "utils.h"                          // calculate_age
#include "error_handler.h"                  // HANDLE_ERROR

#include <string.h>                         // strcmp, strncpy

/**
 * @brief Struct containing the user data.
 *
 * The artist struct contains the following fields:
 * @param liked_songs:       Array of song IDs
 * @param first_name:        First name of the user
 * @param last_name:         Last name of the user
 * @param username:          Username of the user
 * @param email:             Email of the user
 * @param country:           Country of the user
 * @param subscription_type: Subscription type of the user (1 = premium, 0 = normal)
 * @param age:               Age of the user
 * @param birth_date:        Birth date of the user
 */
struct user {
    const char ** liked_songs;
    const char * first_name;
    const char * last_name;
    const char * username;
    char       * email;
    const char * country;            
    unsigned int subscription_type : 1;
    int          age;
    char         birth_date[11];
};

/**
 * @brief Creates a User object from the given fields.
 * 
 * This function allocates memory for a User object and initializes its fields
 * based on the provided array of strings. It uses a string pool for more common
 * strings to reduce memory usage and for the others it duplicates the strings.
 * 
 * @param fields: Array of strings containing user data. The array is expected to have
 * the following elements:
 * @param fields[0]: username
 * @param fields[1]: email
 * @param fields[2]: first_name
 * @param fields[3]: last_name
 * @param fields[4]: birth_date
 * @param fields[5]: country
 * @param fields[6]: subscription_type (expected to be "normal" or "premium")
 * @param fields[7]: liked_songs_id   (string representation of a list of IDs)
 * 
 * @return A pointer to a dynamically allocated User object.
 */
User create_user (char ** fields) {

    User user = malloc (sizeof (struct user));  // Allocate memory for the user object
    if (!user) HANDLE_ERROR ("Failed to allocate memory for user");
    
    // Add to the string pool or get the existing one
    user->username   = string_pool_get (fields[0]);
    user->first_name = string_pool_get (fields[2]);
    user->last_name  = string_pool_get (fields[3]);
    user->country    = string_pool_get (fields[5]);

    user->email = strdup        (fields[1]);
    user->age   = calculate_age (fields[4]);

    // Copy birth date (fixed-length string)
    strncpy (user->birth_date, fields[4], 10);
    user->birth_date[10] = '\0';

    // Set the subscription type (1 = premium, 0 = free) based on input
    user->subscription_type = (strcmp (fields[6], "normal") != 0);

    // Tranfer ownership of the liked_songs array to the user struct
    user->liked_songs = parse_ids_list (fields[7]);

    return user;  // Return the fully initialized User object
}

// Free memory associated with a User object
// Frees all dynamically allocated fields to prevent memory leaks
void free_user (gpointer data) {
    User user = (User)data;

    if (user->email) free (user->email);

    // Free the array itself, the strings are owned by the string pool
    free (user->liked_songs);

    free (user);  // Free the user struct itself
}

// Getters
const char * get_user_username   (User user) { return user->username;   }
const char * get_user_email      (User user) { return user->email;      }
const char * get_user_first_name (User user) { return user->first_name; }
const char * get_user_last_name  (User user) { return user->last_name;  }
int          get_user_age        (User user) { return user->age;        }
const char * get_user_country    (User user) { return user->country;    }

const char ** get_user_liked_songs (User user) {
    if (user->liked_songs) return user->liked_songs;
    return NULL;
}