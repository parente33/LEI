#ifndef QUERY3_H
#define QUERY3_H

#include "Data/Entities/user.h"
#include "Queries/query_manager.h"

#include <glib.h>
#include <stddef.h>

/**
 * @brief Initiates the genre_likes hashtable
*/
void query3_managar_init (void);

/**
 * @brief Destroys the genre_likes hashtable
*/
void query3_destroy (void);

/**
 * Every time a user is parsed, it updates the hashtable with the genre of his liked songs (is only called in user_manager.c)
*/
void updates_genre_likes_table (User user, int min_age, int max_age);

/**
 * @brief Executes query3
 * @param min_age First age in the interval
 * @param max_age Final age in the interval
 * @param result_count How many results there are in the struture
 * @return Structure with every genre, and corresponding likes
*/
Query_results query_3 (Query_info query_info);

#endif // QUERY3_H