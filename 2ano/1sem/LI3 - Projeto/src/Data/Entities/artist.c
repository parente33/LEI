#include "Data/Entities/artist.h"           // Artist

#include "IO/parser.h"                      // parse_ids_list
#include "Data/StringPool/string_pool.h"    // string_pool_get
#include "utils.h"                          // calculate_duration, compare_strings
#include "error_handler.h"                  // HANDLE_ERROR

#include <stdlib.h>                         // atof
#include <string.h>                         // strdup

/**
 * @brief Struct containing the artist data.
 * 
 * The artist struct contains the following fields:
 * @param id_constituents:   Array of artist IDs
 * @param name:              Name of the artist
 * @param description:       Description of the artist
 * @param country:           Country of the artist
 * @param id:                ID of the artist
 * @param recipe_per_stream: Recipe per stream
 * @param type:              Type of the artist (1 = group, 0 = individual)
 */
struct artist {
    const char ** id_constituents;
    char *        name;
    char *        description;
    const char *  country;
    const char *  id;
    float         recipe_per_stream;
    unsigned int  type : 1;
};

/**
 * @brief Creates an Artist object from the given fields.
 * 
 * This function allocates memory for an Artist object and initializes its fields
 * based on the provided array of strings. It uses a string pool for more common 
 * strings to reduce memory usage and for the others it duplicates the strings.
 * 
 * @param fields: Array of strings containing artist data. The array is expected to have 
 * the following elements:
 * @param fields[0]: id
 * @param fields[1]: name
 * @param fields[2]: description       (currently unused)
 * @param fields[3]: recipe_per_stream (string representation of a float)
 * @param fields[4]: id_constituents   (string representation of a list of IDs)
 * @param fields[5]: country
 * @param fields[6]: type              (expected to be "group" or "individual")
 * 
 * @return A pointer to a dynamically allocated Artist object.
 */
Artist create_artist (char ** fields) {
 
    Artist artist = malloc (sizeof (struct artist));
    if (!artist) HANDLE_ERROR ("Failed to allocate memory for Artist");

    // add to the string pool or get the existing one
    artist->id      = string_pool_get (fields[0]);
    artist->country = string_pool_get (fields[5]);

    artist->name              = strdup (fields[1]);
    //artist->description     = strdup (fields[2]);
    artist->description       = NULL;
    artist->recipe_per_stream = atof   (fields[3]);
    artist->type              = (compare_strings (fields[6], "group"));

    // transfer ownership of the array of ids
    artist->id_constituents = parse_ids_list (fields[4]);

    return artist;
}

void free_artist (gpointer data) {
    Artist artist = (Artist)data;

    if (artist->name) free (artist->name);
    //if (artist->description) free (artist->description);

    // Free the array itself, the strings are owned by the string pool
    free (artist->id_constituents);

    free (artist);
}

// Getters

const char * get_artist_id        (Artist artist)  { return artist->id;                }
char * get_artist_name            (Artist artist)  { return artist->name;              }
const char * get_artist_country   (Artist artist)  { return artist->country;           }
int get_artist_type               (Artist artist)  { return artist->type;              }
float get_artist_recipe_per_stream(Artist artist)  { return artist->recipe_per_stream; }
const char **get_id_constituents  (Artist artist)  {return artist->id_constituents;    }