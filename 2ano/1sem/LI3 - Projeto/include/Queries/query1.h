#ifndef QUERY1_H
#define QUERY1_H 

#include "Data/Entities/album.h"
#include "Data/Entities/history.h"
#include "Queries/query_manager.h"

#include <glib.h>

/**
 * @brief Initializes the tables query1 needs and fills the needed listeners
*/
void query1_manager_init (void);

/**
 * @brief Adds the album count for an artist to the album_num_table
 * @param album  Pointer to an Album object 
 * @note If the album has only one artist, the function adds the album count to the hash table.
 *       If the artist already exists in the hash table, it increments the count.
*/
void album_num_add(Album album);

/**
 * @brief Adds the revenue for an artist to the revenue hash table.
 * @param history  Pointer to an history object 
*/
void artists_revenue_add(History history);

/**
 * @brief Destroys the query manager and frees all allocated resources.
*/
void query1_destroy(void);

/**
 * @brief Solves the first query by retrieving and formatting user or artist information.
 * @param query_info  A pointer to the query information object containing the arguments.
 * @return            A pointer to the `Query_results` object  containing the formatted results for the query, or NULL if the query could not be processed.
*/
Query_results query_1(Query_info query_info);

#endif