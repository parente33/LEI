#ifndef ENTITY_INTERFACE_H
#define ENTITY_INTERFACE_H

/**
 * @brief A generic interface for entities.
 */
typedef struct {
    /**
     * @brief file name (relative or absolute path).
     */
    const char * file_name;

    /**
     * @brief File name for invalid lines.
     */
    const char * error_file;

    /**
     * @brief Header line to write at the top of the error file.
     */
    const char * error_header;

    /**
     * @brief Number of fields (columns) expected in the file.
     */
    size_t       field_count;

    /**
     * @brief Validate the specific field for this entity.
     * @param fields   Array of parsed fields.
     * @param index    Current field index
     * @return         true if valid, false otherwise.
     */
    int          (*validate_field) (char ** fields, int index);

    /**
     * @brief Process and store a validated line in the entity’s internal data structure.
     * @param fields   Array of parsed fields (already validated).
     */
    void         (*process_line) (char ** fields);

} entityInterface;

#endif // ENTITY_INTERFACE_H
