#ifndef MUSIC_H
#define MUSIC_H

#include <glib.h>

/**
 * @brief Opaque struct containing the music data.
 */
typedef struct music * Music;

/**
 * @brief Creates and initializes a Music object.
 * @return A pointer to a dynamically allocated Music object.
 */
Music create_music                 (char ** fields);

/**
 * @brief Frees the memory allocated for an Music object.
 * @param data Pointer to the Music object to be freed.
 */
void free_music                    (gpointer data);

/**
 * @brief Get the ID of a Music object
 * @param music Pointer to the Music object.
 * @return Internal pointer to the ID string.
 */
const char * get_music_id          (Music music);

/**
 * @brief Get the artist ids of a Music object
 * @param music Pointer to the Music object.
 * @return Internal pointer to the artist ids list.
 */
const char ** get_music_artist_ids (Music music);

/**
 * @brief Get the album id of a Music object
 * @param music Pointer to the Music object
 * @return Internal pointer to the album id
*/
const char * get_music_album_id    (Music music);

/**
 * @brief Get music duration
 * @param music Pointer to the Music object.
 * @return Internal pointer to the duration string.
 */
const char * get_music_duration    (Music music);

/**
 * @brief Get music genre
 * @param music Pointer to the Music object.
 * @return Internal pointer to the genre string.
 */
const char * get_music_genre       (Music music);

#endif // MUSIC_H