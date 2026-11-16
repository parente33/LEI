#ifndef UTILS_H
#define UTILS_H

#include <glib.h>

/**
 * @brief Append a file to a directory path, also handles different path separators
 * @param dir      The directory path
 * @param filename The file to append at the end of the path
 * @return Pointer to the full path string
 */
char * construct_file_path (const char * dir, const char * filename);

/**
 * @brief Compares two strings, its case-insensible
 * @param str    A string that needs verification
 * @param target The target string to compare
 * @return 1 if valid, 0 if failed
 */
int compare_strings        (const char * str, const char * target);

/**
 * @brief Calculates age based on the birth date
 * @param string Birth Date
 * @return The calculated age as an integer
 */
int calculate_age          (const char * birth_date);

/**
 * @brief Given an age-range, validates it
 * @param init First age
 * @param final Second age
 * @return 1 if it's valid, 0 if it's invalid
*/
int validate_age_range     (int init, int final);

/**
 * @brief Calculated duration in seconds based on a string in the format: hh:mm:ss
 * @param str_duration Duration as a string
 * @return The duration in seconds as a integer
 */
int calculate_duration     (const char * str_duration);

/**
 * @brief Converts number of seconds into the desired format (hh:mm:ss)
 * @param total_seconds Number of seconds
 * @return The duration in the format
*/
char * format_duration_hms (int total_seconds);

/**
 * @brief Concat a string 
 * @param Strings Array of stings to be concated 
 * @param start Index to start 
 * @param end Index to end
 * @return String with the strings concated (memory alocated)
 */
char *concat_strings      (const char **strings, int start, int end); 

/**
 * @brief Removes quotes from a string
 * @param str Original string with quotes
 * @return String without the qoutes (memory alocated)
 */
char *remove_quotes(const char *str); 

/**
 * @brief Converts time to total seconds
 * @param duracao String containing the time
 * @return Number of total seconds(int) 
 */
int segundos (const char * duracao);

/**
 * @brief Gets the user input
 * @returns Integer (in the form of a char) corresponding to the option the user wants to follow
*/
char *get_option (void);



/**
 * @brief Indice of an Query
 * @param Query String with query number and could have S too 
 * @return Number of the query
 */
int map_query_to_index(const char *Query);




#endif // UTILS_H