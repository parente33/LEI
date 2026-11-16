#ifndef QUERY_MANAGER_H
#define QUERY_MANAGER_H 

#include <stddef.h>

typedef struct query_info    *Query_info;
typedef struct query_results *Query_results;

//Array de Func 
typedef Query_results (*QueryFuncs) (Query_info);

/**
 * @brief Add a new function to the QueryFuncs list  
 * @param function Function to add 
 * @param Query Number of the Query adding the function
 */
void set_Query_funcs (QueryFuncs func, int Query);

/**
 * @brief Calls all the queries initialize functions
 */
void query_manager_init(void);

/**
 * @brief Calls all the queries destroy functions
 */
void query_manager_destroy(void);
/**
 * @brief Alocates memory for an query_results struct an fill it 
 * @param fields Array of strings containin the resuts of the query
 * @return Query_results struct 
 */
Query_results create_query_results(const char **results, int n);

/**
 * @brief Frees the memory allocated for a Query_info structure.
 * @param query_info Pointer to the Query_info structure to be destroyed.
 */
void destroy_query_info (Query_info query_info); 

/**
 * @brief Frees the memory allocated for a Query_results structure.
 * @param query_results Pointer to the Query_results structure to be destroyed.
 */
void destroy_query_results (Query_results query_results);

/**
 * @brief Get the arguments of a query 
 * @param query_info Pointer to a Query_info object 
 * @return Pointer too an array of strings 
 */
const char **get_arguments(Query_info query_info);

/**
 * @brief Get the arguments number of a query 
 * @param query_info Pointer to a Query_info object 
 * @return Number of arguments 
 */
size_t get_n_arguments(Query_info query_info);

/**
 * @brief Get the results  of a query 
 * @param query_info Pointer to a Query_results object 
 * @return Pointer too an array of strings 
 */
const char **get_results (Query_results query_results);

/**
 * @brief Get the results number of a query 
 * @param query_info Pointer to a Query_results object 
 * @return Number of arguments 
 */
size_t get_size (Query_results query_results); 


/**
 * @brief Resolves the queries asked by the command file
 * @param file_path String containing the commands file path 
 * @param q_total_time Array of doubles to be filled with the total time solve query number
 * @param total_queries Array of inst to be filled with the number of comands for each query
 */
void execute_file(const char *file_path, double *q_total_time, int *total_queries);

#endif