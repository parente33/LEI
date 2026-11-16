#ifndef ARTIST_MANAGER_H
#define ARTIST_MANAGER_H

#include "Data/Entities/artist.h"

typedef void (*ArtistListener)   (Artist); // Listener

/**
 * @brief Initialize the artist manager
 */
void artist_manager_init         (void);

/**
 * @brief Adds an artist to the manager.
 * @param id     ID of the artist.
 * @param artist Pointer to the artist object.
 */
void artist_manager_add          (const char * id, Artist artist);

/**
 * @brief Get an artist by ID.
 * @param id ID of the artist.
 * @return Pointer to the artist object or NULL if not found.
 */
Artist  artist_manager_get      (const char * id);

/**
 * @brief Check if the artist manager contains an artist with the given ID.
 * @param id ID of the artist.
 * @return TRUE if the artist is in the manager, FALSE otherwise.
 */
gboolean artist_manager_contains (const char * id);

/**
 * @brief Destroy the artist manager.
 */
void artist_manager_destroy      (void);

/**
 * @brief Add a new function to the Artist Listener
 * @param function Function to add
 * @param Query Number of the query adding the function
*/
void set_func_artist_listener    (ArtistListener function, int Query);

#endif // ARTISTS_MANAGER_H