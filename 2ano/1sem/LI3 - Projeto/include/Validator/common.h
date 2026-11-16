#ifndef COMMON_H
#define COMMON_H

typedef int (*simple_validator) (const char *);
typedef int (*complex_validator)(char **, int);

/**
 * @brief Structure to hold the simple and complex validators for a field.
 */
typedef struct {
    int               field_index;
    simple_validator  simple_validator;
    complex_validator complex_validator;
} fieldValidator;

#endif // COMMON_H