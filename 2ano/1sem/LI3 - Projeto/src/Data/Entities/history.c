#include "Data/Entities/history.h"          // History

#include "Data/StringPool/string_pool.h"    // string_pool_get
#include "utils.h"                          // compare_strings
#include "error_handler.h"                  // HANDLE_ERROR

#include <string.h>                         // strncpy

/**
 * @brief Struct containing the history data.
 * 
 * The history struct contains the following fields:
 * @param id        unique identifier of the history
 * @param user_id   unique identifier of the user
 * @param music_id  unique identifier of the music listened by the user
 * @param timestamp date and time of the music listened by the user
 * @param duration  duration time of music listened
 * @param platform  plataform in which the music was listened to. (1 = desktop, 0 = mobile)
 */
struct history {
    const char * id;
    const char * user_id;
    const char * music_id;
    char         timestamp [20];
    char         duration  [9];
    unsigned int platform : 1;
};

/**
 * @brief Creates an History object from the given fields.
 * 
 * This function allocates memory for an History object and initializes its fields
 * based on the provided array of strings. It uses a string pool for more common 
 * strings to reduce memory usage and for the others it duplicates the strings.
 * 
 * @param fields Array of strings containing history data. The array is expected to have 
 * the following elements:
 * @param fields[0]: id
 * @param fields[1]: user_id
 * @param fields[2]: music_id
 * @param fields[3]: timestamp
 * @param fields[4]: duration
 * @param fields[5]: platform (expected to be "mobile" or "desktop")
 * 
 * @return A pointer to a dynamically allocated History object.
 */
History create_history (char ** fields) {
    
    History history = malloc (sizeof (struct history));
    if (!history) HANDLE_ERROR ("Failed to allocate memory for History");

    history->id        = string_pool_get (fields[0]);
    history->user_id   = string_pool_get (fields[1]);
    history->music_id  = string_pool_get (fields[2]);

    // Copy timestamp and duration (fixed-length strings)
    strncpy (history->timestamp, fields[3], 19);
    history->timestamp [19] = '\0';

    strncpy (history->duration, fields[4], 8);
    history->duration [8] = '\0';

    // do not ignore case
    history->platform  = (compare_strings (fields[5], "mobile"));

    return history;
}

void free_history (gpointer data) {
    History history = (History)data;
    free (history);
}

// Getters
const char * get_history_id        (History history) { return history->id;       }   
const char * get_history_user_id   (History history) { return history->user_id;  }
const char * get_history_music_id  (History history) { return history->music_id; }
const char * get_history_timestamp (History history) { return history->timestamp;}
const char * get_history_duration  (History history) { return  history->duration;}
