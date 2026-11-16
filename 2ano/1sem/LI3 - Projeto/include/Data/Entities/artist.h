#ifndef ARTIST_H
#define ARTIST_H

#include <glib.h>

/**
 * @brief Opaque struct containing the artist data.
 */
typedef struct artist * Artist;

/**
 * @brief  Creates an Artist object from the given fields.
 * @return A pointer to a dynamically allocated Artist object.
 */
Artist create_artist               (char ** fields);

/**
 * @brief Frees the memory allocated for an Artist object.
 * @param data Pointer to the Artist object to be freed.
 */
void free_artist                   (gpointer data);

/**
 * @brief Gets the ID of an Artist object.
 * @param artist Pointer to the Artist object.
 * @return Internal pointer to the ID string.
 */
const char * get_artist_id         (Artist artist);

/**
 * @brief Gets the name of an Artist object.
 * @param artist Pointer to the Artist object.
 * @return Internal pointer to the ID string.
 */
char * get_artist_name       (Artist artist);

/**
 * @brief Gets the country of an Artist object.
 * @param artist Pointer to the Artist object.
 * @return Internal pointer to the country string.
 */
const char * get_artist_country    (Artist artist);

/**
 * @brief Gets the type of an Artist object.
 * @param artist Pointer to the Artist object.
 * @return 1 if the artist is a group, 0 otherwise.
 */
int get_artist_type                (Artist artist);

/**
 * @brief Gets the recipe per stream of an Artist object
 * @param artist Pointer to the Artist object.
 * @return Artist's recipe per stream as a float
 */
float get_artist_recipe_per_stream (Artist artist);

/**
 * @brief Gets the ids of the constituintes 
 * @param artist Pointer to the Artist object 
 * @return Array of strings with ids 
 */
const char **get_id_constituents   (Artist artist);
#endif // ARTIST_H
