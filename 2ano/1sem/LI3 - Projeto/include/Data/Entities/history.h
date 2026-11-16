#ifndef HISTORY_H
#define HISTORY_H

#include <glib.h>

/**
 * @brief Opaque struct containing the history data.
 */
typedef struct history * History;

/**
 * @brief Creates an History object from the given fields.
 * @return A pointer to a dynamically allocated History object.
 */
History create_history                (char ** fields);
                                    
/**
 * @brief Frees the memory allocated for an History object.
 * @param data Pointer to the History object to be freed.
 */
void free_history                     (gpointer data);
                                      
/**
 * @brief Gets the ID of an History object.
 * @param history Pointer to the History object.
 * @return Internal pointer to the ID string.
 */
const char * get_history_id           (History history);
                                    
/**
 * @brief Gets the ID of a User object in an History object
 * @param history Pointer to the History object
 * @return Internal pointer to the ID string
*/
const char * get_history_user_id (History history);

/**
 * @brief Gets the ID music of an History object 
 * @param history Pointer to the History object 
 * @return Internal pointer to the music ID string
 */
const char * get_history_music_id     (History history);

/**
 * @brief Gets the timestamp of an History object
 * @param history Pointer to the History object
 * @return Internal pointer to the timestamp string
*/
const char * get_history_timestamp (History history);

/**
 * @brief Gets the duration of an History object 
 * @param history Pointer to the History object
 * @return Internal pointer to the duration string
*/
const char * get_history_duration (History history);
#endif // HISTORY_H