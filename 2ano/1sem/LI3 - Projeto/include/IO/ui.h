#ifndef UI_H
#define UI_H

/**
 * @brief Shows the initial interactive menu in the terminal
 * @note This is the menu that appears before loading the datasets, or choosing the queries to execute
*/
void display_initial_menu(void);

/**
 * @brief Shows the same initial menu, but without the dataset loading option
*/
void menu_after_loading_dataset (void);

/**
 * @brief Shows the interactive menu once dataset loading is done
*/
void display_queries_menu(void);

#endif // UI_H