#ifndef IO_H
#define IO_H

#include <stdio.h>
#include <Queries/query_manager.h>
/**
 * @brief Reads a line from the given file.
 * @param file   The file pointer.
 * @param line   Pointer to a buffer that will hold the line.
 * @param len    Pointer to the buffer size.
 * @return       1 if a line was successfully read, 0 otherwise.
 * @note The caller is responsible for freeing the line buffer (if not reused).
 */
int io_read_line   (FILE * file, char ** line, size_t * len);

/**
 * @brief Writes a line (null-terminated string) to the given file.
 * @param file   The file pointer.
 * @param line   The string to write (must be null-terminated).
 */
void io_write_line (FILE * file, const char * line);

/**
 * @brief Safely opens a file with the specified mode (e.g., "r", "w", "a").
 * @param path   The file path.
 * @param mode   The mode string as in fopen (e.g., "r", "w", "a").
 * @return       A pointer to the opened file, or NULL if it fails.
 */
FILE * io_open_file (const char * path, const char * mode);

/**
 * @brief Closes the given file. Logs a warning if closing fails.
 * @param file   The file pointer to close.
 */
void io_close_file  (FILE *file); 

/**
 * @brief Outputs the results of a query to a specified file, formatted according to the provided format string.
 * @param query_results A pointer to an Query_results object 
 * @param formatacao String containing number of the query and can have "S" or no 
 * @param output_file The file pointer to which the results will be printed. It must be a valid, open file handle.
 */
void output_query(Query_results query_results, const char *formatacao, FILE *output_file);

/**
 * @brief Creates a text file with the arguments for a query and executes it
 * @param query_number The number of the query to be executed
 * @param project_dir A pointer to the project's main directory, where the input file will be created.
*/
void create_query_file(int query_number, const char *project_dir);

/**
 * @brief Handles the menu that is shown after a valid dataset is loaded
 * @param project_dir A pointer to the project's main directory
*/
void handle_post_loading_menu (const char *project_dir);

/**
 * @brief After choosing to execute the queries, user choses which one to execute
 * @param project_dir A pointer to the project's main directory
 * @note Leaving the program at this point is also possible, however,
 *       the possibility to go back and load a different dataset is not available
*/
void handle_queries_menu (const char *project_dir);

/**
 * @brief Handles the initial menu in the interactive program, allowing the user to load a dataset or execute queries
 * @param dataset_loaded A pointer to an integer indicating whether the dataset has been loaded (1 if loaded, 0 if not)
 * @param dataset_path A string to store the path of the dataset folder
 * @param project_dir A pointer to the project's main directory
*/
void process_initial_menu(int *dataset_loaded, char *dataset_path, const char *project_dir);

/**
 * @brief Prints the query diferences and times 
 * @param filepath Array with the path of the expectated resuls 
 * @param comand_path Array with the path of the comand file 
 * @param q_total_time Array with the times of the queries
 * @param total_queries Array with the total commands for each query
 */
void module_teste(const char *file_path,const char *comand_path, double *q_total_time, int *total_queries);

#endif // IO_H
