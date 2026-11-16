#ifndef PARSER_H
#define PARSER_H

/**
 * @brief Function pointer type for validating a line of fields.
 * @param fields   An array of parsed fields (null-terminated strings).
 * @param nfields  The number of parsed fields.
 * @return         1 if the line is valid, 0 otherwise.
 * @note Typically lives in the entity/manager module that knows the field rules.
 */
typedef int  (*lineValidator)(char ** fields, int nfields);

/**
 * @brief Parses a line of the form:
 *        "field1";"field2";..."fieldN"
 *        in-place, verifying quoted fields. 
 * @param line        The line to parse (modified in-place if successful).
 * @param field_count Number of expected fields.
 * @param fields      Array of char*[field_count] to store pointers to parsed fields.
 * @param validator   Callback for per-field validation (may be NULL if not needed).
 * @return true if all fields parsed and validated successfully, else false.
 * @note On failure, the line is reverted so you can safely log or output it.
 */
int parse_entity_line (
    char         * line,
    char        ** fields,
    int            field_count,
    lineValidator  validator
);

/**
 * @brief Parses a list of IDs from a string.
 * The string is expected to be a list of IDs separated by commas.
 * The function returns an array of pointers to the IDs.
 * @param list_str: The string containing the list of IDs.
 * @return An array of pointers to the IDs.
 */
const char ** parse_ids_list (char * list_str);

#endif // PARSER_H