#ifndef HISTORY_MANAGER_H
#define HISTORY_MANAGER_H

#include "Data/Entities/history.h"

extern GHashTable *history_table;
typedef void (*HistoryListener)   (History); // Listener

/**
 * @brief Initialize the history manager
 */
void history_manager_init         (void);

/**
 * @brief Adds a history to the manager.
 * @param id      ID of the history.
 * @param history Pointer to the history object.
 */
void history_manager_add          (const char * id, History history);

/**
 * @brief Get a history by ID.
 * @param id ID of the history.
 * @return Pointer to the history object or NULL if not found.
 */
History  history_manager_get     (const char * id);

/**
 * @brief Check if the history manager contains a history with the given ID.
 * @param id ID of the history.
 * @return TRUE if the history is in the manager, FALSE otherwise.
 */
gboolean history_manager_contains (const char * id);

/**
 * @brief Destroy the history manager.
 */
void history_manager_destroy      (void);

/**
 * @brief Add a new function to the History Listener 
 * @param function Function to add 
 * @param Query Number of the query adding the function
 */
void set_func_history_listener    (HistoryListener function, int Query); 

#endif // HISTORY_MANAGER_H