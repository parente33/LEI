#ifndef MUSIC_MANAGER_H
#define MUSIC_MANAGER_H

#include "Data/Entities/music.h"

typedef void (*MusicListener)   (Music); // Listener

/**
 * @brief Initialize the music manager
 */
void music_manager_init        (void);

/**
 * @brief Adds a music to the manager.
 * @param id    ID of the music.
 * @param music Pointer to the music object.
 */
void music_manager_add         (const char * id, Music music);

/**
 * @brief Get a music by ID.
 * @param id ID of the music.
 * @return Pointer to the music object or NULL if not found.
 */
Music  music_manager_get      (const char * id);

/**
 * @brief Check if the music manager contains a music with the given ID.
 * @param id ID of the music.
 * @return TRUE if the music manager contains the music, FALSE otherwise.
 */
gboolean music_manager_contains (const char * id);

/**
 * @brief Destroy the music manager.
 */
void music_manager_destroy      (void);


/**
 * @brief Add a new function to the Music Listener 
 * @param function Function to add 
 * @param Query Number of the query adding the function
 */
void set_func_music_listener    (MusicListener function, int Query); 

#endif // MUSIC_MANAGER_H