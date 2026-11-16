#include "Data/Entities/album.h"            // Album

#include "IO/parser.h"                      // parse_ids_list
#include "Data/StringPool/string_pool.h"    // string_pool_get
#include "error_handler.h"                  // HANDLE_ERROR

#include <stdlib.h>                         // atoi
#include <string.h>                         // strdup

/**
 * @brief Struct containing the album data.
 * 
 * The album struct contains the following fields:
 * @param id          unique identifier of the album
 * @param title       album title
 * @param artists_id  list of artists that participated in the album
 * @param year        year of release
 * @param producers   list of producers that participated in the album
 */
struct album {
    const char *  id;
    char       *  title;
    const char ** artists_id;
    const char ** producers;
    int           year;
};

/**
 * @brief Creates an Album object from the given fields.
 * 
 * This function allocates memory for an Album object and initializes its fields
 * based on the provided array of strings. It uses a string pool for more common 
 * strings to reduce memory usage and for the others it duplicates the strings.
 * 
 * @param fields: Array of strings containing album data. The array is expected to have 
 * the following elements:
 * @param fields[0]: id
 * @param fields[1]: title
 * @param fields[2]: artists_id   (string representation of a list of IDs)
 * @param fields[3]: year         (string representation of an integer)
 * @param fields[4]: producers    (string representation of a list of IDs)
 * 
 * @return A pointer to a dynamically allocated Album object.
 */
Album create_album (char ** fields) {
    
    Album album = malloc (sizeof (struct album));
    if (!album) HANDLE_ERROR ("Failed to allocate memory for Album");

    album->id         = string_pool_get (fields[0]);
    album->title      = strdup          (fields[1]);

    album->artists_id = parse_ids_list  (fields[2]);

    album->year       = atoi            (fields[3]);
    album->producers  = parse_ids_list  (fields[4]);

    return album;
}

void free_album (gpointer data) {
    Album album = (Album)data;

    if (album->title)      free (album->title);

    // free the array itself, the strings are owned by the string pool
    if (album->artists_id) free (album->artists_id);
    if (album->producers)  free (album->producers);

    free (album);
}

// Getters
const char * get_album_id         (Album album) { return album->id; }
const char ** get_artists_album_id(Album album) { return album->artists_id;}