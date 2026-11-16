#ifndef ALBUM_H
#define ALBUM_H

#include <glib.h>

/**
 * @brief Opaque struct containing the album data.
 */
typedef struct album * Album;

/**
 * @brief Creates an Album object from the given fields.
 * @return A pointer to a dynamically allocated Album object.
 */
Album create_album                (char ** fields);

/**
 * @brief Frees the memory allocated for an Album object.
 * @param data Pointer to the Album object to be freed.
 */
void free_album                   (gpointer data);

/**
 * @brief Get the id of a Album object
 * @param album Pointer to the Album object
 * @return internal pointer to the album id
 */
const char * get_album_id (Album album);
/**
 * @brief Get the artist ids of an album object
 * @param album Pointer to the Album object
 * @return internal pointer to the artist ids list
 */
const char ** get_artists_album_id(Album album); 
#endif // ALBUM_H