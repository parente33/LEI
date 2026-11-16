#include "utils.h"

#include "error_handler.h"

#include <stdio.h>
#include <string.h>
#include <ctype.h>
#include <stdlib.h>
#include <glib.h>

#ifdef _WIN32
    #define PATH_SEPARATOR '\\'
#else
    #define PATH_SEPARATOR '/'
#endif

char * construct_file_path (const char * dir, const char * filename) {
    if (!dir)      HANDLE_ERROR ("Directory string is NULL");
    if (!filename) HANDLE_ERROR ("Filename string is NULL");

    size_t path_len     = strlen (dir);
    size_t filename_len = strlen (filename);

    char * full_path     = malloc (path_len + filename_len + 2); // separator and '\0'
    if (full_path == NULL) HANDLE_ERROR ("Allocation failed for full_path");

    strcpy (full_path, dir);

    // Check if the last character of the directory is the path separator
    if (dir[path_len - 1] != PATH_SEPARATOR) {
        size_t len = strlen (full_path);
        full_path [len]     = PATH_SEPARATOR;
        full_path [len + 1] = '\0';
    }
    strcat (full_path, filename);

    return full_path;
}

int compare_strings (const char * str, const char * target) {
    if (!str)      HANDLE_ERROR ("String is NULL");
    if (!target)   HANDLE_ERROR ("Target string is NULL");

    // check the lenght
    if (strlen (str) != strlen (target)) return 0;

    // Compare each character, case-sensitively
    for (int i = 0; target[i] != '\0'; i++) {
        if (tolower ((unsigned char)str[i]) != target[i]) return 0;
    }

    return 1; // Strings are equal
}

int calculate_age (const char * birth_date) {
    if (!birth_date) HANDLE_ERROR ("Birth date string is NULL");

    int birthYear, birthMonth, birthDay;
    sscanf (birth_date, "%4d/%2d/%2d", &birthYear, &birthMonth, &birthDay);

    int age = 2024 - birthYear;

    // Adjust age based on whether the birthday has occurred this year
    if (9 < birthMonth || (9 == birthMonth && 9 < birthDay)) age--;
    
    return age;
}

int validate_age_range (int init, int final) {
    return init <= final && init > 0 && final > 0;
}

int calculate_duration (const char * str_duration) {
    if (!str_duration) HANDLE_ERROR ("Duration string is NULL");
    
    int hours, minutes, seconds;
    sscanf(str_duration, "%d:%d:%d", &hours, &minutes, &seconds);
    
    return hours * 3600 + minutes * 60 + seconds;
}

char * format_duration_hms (int total_seconds) {
    // Format duration from seconds to HH:MM:SS
    int hours = total_seconds / 3600;
    int minutes = (total_seconds % 3600) / 60;
    int seconds = total_seconds % 60;

    char * formatted_duration = malloc(16); // "HH:MM:SS" + '\0'
    if (!formatted_duration) HANDLE_ERROR ("Formatted duration string is NULL");

    snprintf(formatted_duration, 16, "%02d:%02d:%02d", hours, minutes, seconds);

    return formatted_duration;
}



char *concat_strings(const char **strings, int start, int end) {
    if (!strings || start > end) return NULL;

    // Calcula o tamanho total necessário para alocar
    size_t total_length = 1; // Para o terminador '\0'
    for (int i = start; i <= end; i++) {
        if (strings[i]) total_length += strlen(strings[i]) + 1; // +1 para o espaço
    }

    char *result = malloc(total_length);
    if (!result) {
        HANDLE_ERROR("Failed to allocate memory for concatenated string\n");
        return NULL;
    }

    result[0] = '\0'; // Inicializa a string vazia
    for (int i = start; i <= end; i++) {
        if (strings[i]) {
            strcat(result, strings[i]);
            if (i < end) strcat(result, " "); // Adiciona espaço entre palavras
        }
    }

    return result;
}


char *remove_quotes(const char *str) {
    if (!str) return NULL;

    size_t len = strlen(str);
    if (len >= 2 && str[0] == '"' && str[len - 1] == '"') {
        char *clean_str = strndup(str + 1, len - 2);
        return clean_str;
    }
    return strdup(str);
}

int segundos (const char * duracao) {
    int horas = (duracao[0] - '0') * 10 + (duracao[1] - '0');
    int minutos = (duracao[3] - '0') * 10 + (duracao[4] - '0');
    int segundos = (duracao[6] - '0') * 10 + (duracao[7] - '0');
    return (horas*60*60 + minutos*60 + segundos);  
}

char *get_option (void) {
    printf("\n  »» Insira a opção: ");
    char str[20];
    scanf(" %[^\n]s",str);
    return strdup(str);
}

int map_query_to_index(const char *Query) {
    if (!Query || strlen(Query) < 1) {
        return -1;
    }

    char first_char = Query[0]; 
    if (first_char >= '1' && first_char <= '6') {
        return first_char - '1'; 
    }
    return -1; 
}
