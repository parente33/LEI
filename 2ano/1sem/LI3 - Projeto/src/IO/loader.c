#include "IO/loader.h"

#include "IO/parser.h"          // parse_entity_line
#include "IO/io.h"              // io_read_line, io_write_line, io_open_file, io_close_file
#include "entity_interface.h"   // entityInterface
#include "entity_registry.h"    // entity_registry_count, entity_registry_get
#include "utils.h"              // construct_file_path
#include "error_handler.h"      // HANDLE_ERROR

#include <unistd.h>             // access
#include <string.h>             // strrchr

void load_files (const char * dir_path) {
    if (access(dir_path, F_OK) != 0) {
        fprintf(stderr, "Error: The directory '%s' wasn't found or isn't accessible.\n", dir_path);
        return;
    }
    int count = entity_registry_count();
    for (int i = 0; i < count; i++) {
        const entityInterface *iface = entity_registry_get(i);
        if (!iface) HANDLE_ERROR ("Null interface at index %d", i);

        // Build the CSV file path
        char *file_path = construct_file_path(dir_path, iface->file_name);
        if (!file_path) HANDLE_ERROR ("Failed to construct CSV path for %s", iface->file_name);

        if (access(file_path, F_OK) != 0) {
            fprintf(stderr, "Error: The file '%s' wasn't found in '%s'.\n", iface->file_name, dir_path);
            free(file_path);
            return;
        }

        // Now call the CSV loader function
        load_entity_file(file_path,                 // path to the entity’s file
                         iface->error_file,         // path to the error file
                         iface->error_header,       // header line to write in error file
                         iface->field_count,        // number of fields expected
                         iface->validate_field,     // entity-specific validation
                         iface->process_line);      // entity-specific processing
        free(file_path);
    }
}

void load_entity_file (const char *  file_path,
                       const char *  error_path,
                       const char *  error_header,
                       int           field_count,
                       lineValidator validator,
                       lineProcessor processor) {

    if (!file_path || !error_path) HANDLE_ERROR ("Invalid files paths provided.\nFile: %s\nError: %s", file_path, error_path);
    
    FILE * ent_file = io_open_file (file_path, "r");
    FILE * err_file = io_open_file (error_path, "a");
    if (!err_file) {
        io_close_file (ent_file);
        return;
    }

    // Write the error file header if provided
    if (error_header && *error_header) io_write_line (err_file, error_header);

    // skip the first line
    char * line = NULL;
    size_t len  = 0;
    if (io_read_line (ent_file, &line, &len)) {
        // Do nothing, just skip the header line
    }

    // Buffer to store the parsed fields
    char * fields [field_count];

    // Read each subsequent line
    while (io_read_line (ent_file, &line, &len)) { 
        int valid = parse_entity_line (line, fields, field_count, validator);

        if (!valid) {
            // Write the original line to the error file if invalid
            io_write_line (err_file, line);
        } else {
            // Process the valid line
            if (processor) {
                processor (fields);
            }
        }
    }

    free (line);
    io_close_file (ent_file);
    io_close_file (err_file);
}
