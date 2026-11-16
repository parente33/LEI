#ifndef ERROR_HANDLER_H
#define ERROR_HANDLER_H

#include <stdio.h>
#include <stdlib.h>


/**
 * @brief Macro to handle errors.
 * A dumb error handler i know
 * @param fmt Format string
 * @param ... Arguments
 * @note This macro will print the error message and the location of the error
 */
#define HANDLE_ERROR(fmt, ...)                                      \
    do {                                                            \
        fprintf(stderr, "\nError: " fmt "\n", ##__VA_ARGS__);            \
        fprintf(stderr, "Located at %s:%d\n", __FILE__, __LINE__);  \
        exit(EXIT_FAILURE);                                         \
    } while(0)

#endif // ERROR_HANDLER_H
