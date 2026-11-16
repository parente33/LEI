#include "Validator/syntactic.h"

#include <stdio.h>
#include <string.h>  // strlen, strchr, strcmp, strncpy
#include <ctype.h>   // isalnum, islower, isupper, isdigit

#include "utils.h"

#include <string.h>

int validate_email (const char * email) {
    char * arroba = strchr(email, '@');  // Find '@' symbol in email
    char * point  = NULL;
    if (arroba != NULL) point = strchr(arroba, '.');  // Find '.' after '@'

    // Check if both '@' and '.' are present
    if (point == NULL || arroba == NULL) return 0;

    // Validate username part (before '@')
    int username_len = arroba - email;
    if (username_len == 0) return 0;  // Empty username

    // Check if username contains only alphanumeric lowercase characters
    for (int i = 0; i < username_len; i++) {
        if (!isalnum(email[i]) || isupper(email[i])) return 0;
    }

    // Validate lstring part (between '@' and '.')
    char * domain = arroba + 1;
    int lstring_len = point - domain;
    if (lstring_len < 1) return 0;  // lstring must have at least 1 character

    // Ensure lstring contains only lowercase characters
    for (int i = 0; i < lstring_len; i++) {
        if (!islower(domain[i])) return 0;
    }

    // Validate rstring part (after '.')
    char *rstring = point + 1;
    int rstring_len = strlen(rstring);
    if (rstring_len < 2 || rstring_len > 3) return 0;  // rstring must have 2-3 characters

    // Ensure rstring contains only lowercase characters
    for (int i = 0; i < rstring_len; i++) {
        if (!islower(rstring[i])) return 0;
    }

    return 1;  // Email is valid
}

int validate_duration (const char * duracao) {
    if (strlen(duracao) != 8) return 0;  // Invalid length
    if (duracao[2] != ':' || duracao[5] != ':') return 0;  // Incorrect format (missing ':')

    // Ensure all characters except ':' are digits
    for (int i = 0; i < 8; i++) {
        if (i != 2 && i != 5 && !isdigit(duracao[i])) return 0;
    }

    // Parse hours, minutes, and seconds
    int horas    = (duracao[0] - '0') * 10 + (duracao[1] - '0');
    int minutos  = (duracao[3] - '0') * 10 + (duracao[4] - '0');
    int segundos = (duracao[6] - '0') * 10 + (duracao[7] - '0');

    // Validate the ranges of hours, minutes, and seconds
    if (horas < 0 || horas > 99 || minutos < 0 || minutos > 59 || segundos < 0 || segundos > 59) return 0;

    return 1;  // Duration is valid
}

int validate_subscription (const char * subscription ) {
    return (strcmp (subscription , "premium") == 0 || strcmp (subscription , "normal") == 0);  // Return 1 if valid
}

int validate_artist_type (const char * type, const char * id_constituent) {
    // First validate artist type
    if ((compare_strings (type, "group") == 0) && (compare_strings (type, "individual") == 0)) return 0;

    // Then validate if the artist is individual and has constituents
    if ((compare_strings (type, "individual") == 1) && (strcmp (id_constituent, "[]") != 0)) return 0;

    return 1; // Subscription is valid
}

int validate_plataform_type (const char * type) {
    return (compare_strings (type, "desktop") == 1 || compare_strings (type, "mobile") == 1);  // Return 1 if valid
}

int validate_date (const char * date) {
    if (strlen(date) != 10) return 0;  // Invalid length
    if (date[4] != '/' || date[7] != '/') return 0;  // Incorrect format (missing '/')

    // Ensure all characters except '/' are digits
    for (int i = 0; i < 10; i++) {
        if (i != 4 && i != 7 && !isdigit(date[i])) return 0;
    }

    // Parse year, month, and day
    int ano = (date[0] - '0') * 1000 + (date[1] - '0') * 100 + (date[2] - '0') * 10 + (date[3] - '0');
    int mes = (date[5] - '0') * 10 + (date[6] - '0');
    int dia = (date[8] - '0') * 10 + (date[9] - '0');

    // Validate the ranges of year, month, and day
    if (mes < 1 || mes > 12 || dia < 1 || dia > 31 || ano < 1 || ano > 2024) return 0;

    // Check if the date is more recent than the current date (2024/09/09)
    if (ano == 2024 && (mes > 9 || (mes == 9 && dia > 9))) return 0;

    return 1;  // Date is valid
}
