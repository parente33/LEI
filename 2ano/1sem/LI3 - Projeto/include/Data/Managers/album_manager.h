#ifndef ALBUM_MANAGER_H
#define ALBUM_MANAGER_H

#include "Data/Entities/album.h"

typedef void (*AlbumListener)      (Album); // Listener 

/**
 * @brief Initialize the album manager
 */
void album_manager_init            (void);

/**
 * @brief Adds an album to the manager.
 * @param id    ID of the album.
 * @param album Pointer to the album object.
 */
void album_manager_add             (const char * id, Album album);

/**
 * @brief Get an album by ID.
 * @param id ID of the album. 
 * @return Pointer to the album object or NULL if not found.
 */
Album  album_manager_get          (const char * id);

/**
 * @brief Check if the album manager contains an album with the given ID.
 * @param id ID of the album.
 * @return TRUE if the album is in the manager, FALSE otherwise.
 */
gboolean album_manager_contains    (const char * id);

/**
 * @brief Destroy the album manager.
 */
void album_manager_destroy         (void);

/**
 * @brief Add a new function to the Album Listener 
 * @param function Function to add 
 * @param Query Number of the Query adding the function
 */
void set_func_album_listener       (AlbumListener function, int Query);

#endif // ALBUM_MANAGER_H