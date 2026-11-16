#ifndef LOADER_H
#define LOADER_H

/**
 * @brief Iterates over all registered entities, constructs file paths,and calls load_file for each.
 * @param dir_path     The base directory where all files live.
 * Assumes each EntityInterface has:
 * @param file_name     (e.g. "artists")
 * @param error_file    (e.g. "artists_errors")
 * @param error_header  (e.g. "\"id\",\"name\"...\n")
 * @param validate_line (function pointer)
 * @param process_line  (function pointer)
 */
void load_files (const char * dir_path);

/**
 * @brief Function pointer type for validating a line of fields.
 * @param fields   An array of parsed fields (null-terminated strings).
 * @param nfields  The number of parsed fields.
 * @return         1 if the line is valid, 0 otherwise.
 * @note Typically lives in the entity/manager module that knows the field rules.
 */
typedef int  (*lineValidator)(char ** fields, int nfields);

/**
 * @brief Function pointer type for processing a valid line of fields.
 * @param fields   An array of parsed fields (null-terminated strings).
 */
typedef void (*lineProcessor)(char ** fields);

/**
 * @brief Loads a file and applies validation/processing to each line.
 * @param file_path       The path to the file to read.
 * @param error_path      The path to a file where invalid lines will be written.
 * @param error_header    The header line to write at the top of the error file.
 * @param field_count     The number of fields expected in each line.
 * @param validator       A function pointer to validate each parsed line.
 * @param processor       A function pointer to process each valid line.
 */
void load_entity_file (const char *  file_path,
                       const char *  error_path,
                       const char *  error_header,
                       int           field_count,
                       lineValidator validator,
                       lineProcessor processor);

#endif // LOADER_H
