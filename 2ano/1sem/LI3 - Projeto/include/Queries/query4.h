#ifndef QUERY4_H
#define QUERY4_H

#include "Data/Entities/history.h"
#include "Queries/query_manager.h"

typedef struct artist_time * Artist_time;

/**
 * @brief Initializes the table query5 needs and fills the needed listeners
 */
void query4_manager_init (void);

/**
 * @brief Adds the time an aritst was listened to an GTree, located in HashTable(The week is the key)
 * @param history Pointer to an history object 
 */
void add_Tops_N          (History history);

/**
 * @brief Solves the fourth query by retrieving the artist that was the most times in week top10 
 * @param query_info  A pointer to the query information object containing the arguments
 * @return A pointer to the `Query_results` object  containing the formatted results for the query, or NULL if the query could not be processed.
 */
Query_results query_4    (Query_info);
                         
/**
 * @brief Destroys the query manager and frees all allocated resources.
 */
void query4_destroy      (void);
#endif