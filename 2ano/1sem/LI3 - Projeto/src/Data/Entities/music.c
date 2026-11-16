#include "Data/Entities/music.h"            // Music

#include "IO/parser.h"                      // parse_ids_list
#include "Data/StringPool/string_pool.h"    // string_pool_get    
#include "error_handler.h"                  // HANDLE_ERROR

#include <stdlib.h>                         // atoi
#include <string.h>                         // strncpy

/**
 * @brief Struct containing the music data.
 * 
 * The music struct includes the following fields:
 * @param id:         Unique ID for the music.
 * @param genre:      Genre of the music.
 * @param artist_ids: List of artist IDs for collaborations.
 * @param album_id:   ID of the album the music belongs to.
 * @param title:      Title of the music track.
 * @param lyrics:     Lyrics of the music.
 * @param duration:   Duration of the track (fixed-length string).
 * @param year:       Year of release.
 */
struct music {
    const char *  id;
    const char *  genre;
    const char *  album_id;
    const char ** artist_ids;
    char        * title;
    char        * lyrics;
    char          duration [9];
    int           year;
};

/**
 * @brief Creates and initializes a Music object.
 * 
 * This function allocates memory for a Music object and initializes its fields
 * based on the provided array of strings. It uses a string pool for more common
 * strings to reduce memory usage and for the others it duplicates the strings.
 * 
 * @param fields An array of strings containing the following music data:
 * @param fields[0]: id
 * @param fields[1]: title      (currently unused)
 * @param fields[2]: artist_ids (string representation of a list of IDs)
 * @param fields[3]: album_id
 * @param fields[4]: duration   (fixed-length string)
 * @param fields[5]: genre
 * @param fields[6]: year       (string representation of an integer)
 * @param fields[7]: lyrics     (currently unused)
 * 
 * @return A pointer to a dynamically allocated Music object.
 */
Music create_music (char ** fields) {

    Music music = malloc (sizeof (struct music));
    if (!music) HANDLE_ERROR ("Failed to allocate memory for Music");

    // Add to the string pool or get the existing one
    music->id    = string_pool_get (fields[0]);

    //music->title = strdup (fields[1]);
    music->title   = NULL;

    // Transfer ownership of the artist IDs array to the music struct
    music->artist_ids = parse_ids_list (fields[2]);

    music->album_id   = string_pool_get (fields[3]);

    // Copy the duration (fixed-length string)
    strncpy (music->duration, fields[4], 8);
    music->duration[8] = '\0';  // Null-terminate the duration string

    music->genre  = string_pool_get (fields[5]);

    music->year   = atoi (fields[6]);
    //music->lyrics = strdup (fields[7]);
    music->lyrics = NULL;
    
    return music;
}

void free_music (gpointer data) {
    Music music = (Music)data;

    //if (music->title)  free(music->title);  // Free the title string
    //if (music->lyrics) free(music->lyrics);  // Free the lyrics string

    // Free the array itself, the strings are owned by the string pool
    free (music->artist_ids);
    
    free (music);
}

// Getters
const char * get_music_id          (Music music) { return music->id;         }
const char ** get_music_artist_ids (Music music) { return music->artist_ids; }
const char * get_music_album_id    (Music music) { return music->album_id;   }
const char * get_music_duration    (Music music) { return music->duration;   }
const char * get_music_genre       (Music music) { return music->genre;      }
