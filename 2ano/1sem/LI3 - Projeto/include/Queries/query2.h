#ifndef QUERY2_H
#define QUERY2_H

#include "Queries/query_manager.h"
#include "Data/Entities/music.h"

typedef struct id_disco *Id_Disco; 
typedef struct  traverse_data *Traverse_Data;
/**
 * @brief Initializes the query2 structure
*/
void query2_manager_init (void);

/**
 * @brief Add the discography to the artist in the hash_table
 * @param music Pointer to an music object
 */
void query2_add(Music music);


/**
 * @brief Executes query2
 * @param qm Auxiliar structure
 * @param N How many artists we want in the top
 * @param country_filter TopN filtered to that specific country (could be NULL)
 * @param result_count How many results are there in the (char **) structure
 * @return Pointer to an array of strings (each string is a result calculated during the query execution)
*/
Query_results query_2 (Query_info query_info);

/**
 * @brief Frees the memory used in the query2 structure
*/
void query2_destroy (void);

#endif // QUERY2_H