#include "IO/parser.h"

#include "Data/StringPool/string_pool.h" // string_pool_get
#include "error_handler.h"          // HANDLE_ERROR

/** 
 * Store info to revert any quotes inserted as '\0'. 
 * We'll revert them if validation fails mid-way.
 */
typedef struct {
    char * pos;
    char   saved_char;
} revertPoint;

int parse_entity_line(
    char         * line,
    char        ** fields,
    int            field_count,
    lineValidator  validator
) {
    if (!line || !fields) return 0;

    revertPoint reverts [field_count];
    int revert_used = 0;

    unsigned int success = 1;
    char * ptr = line;  // pointer that will move across the line

    for (int i = 0; i < field_count && success; i++) {
        // Expect leading quote
        if (*ptr != '"') {
            success = 0;
            break;
        }
        char * field_start = ptr + 1;

        // Find closing quote manually
        char * closing_quote = field_start;
        while (*closing_quote && *closing_quote != '"') closing_quote++;
        if (*closing_quote != '"') {
            // no matching quote
            success = 0;
            break;
        }

        // Null-terminate the field
        reverts[revert_used].pos        = closing_quote;
        reverts[revert_used].saved_char = *closing_quote;
        revert_used++;

        *closing_quote  = '\0';
        fields [i]  = field_start;

        // Validate field if needed
        if (validator && !validator (fields, i)) {
            // revert this one
            *closing_quote = reverts[revert_used - 1].saved_char;
            revert_used--;
            success = 0;
            break;
        }

        // Move beyond the closing quote
        ptr = closing_quote + 1;

        // If not last field, expect ';"'
        if (i < field_count - 1) {
            if (*ptr != ';') {
                success = 0;
                break;
            }
            ptr++; // skip semicolon
            if (*ptr != '"') {
                success = 0;
                break;
            }
        } 
        //else {
            // last field => no semicolon check
            // optionally check if *ptr is end-of-line or \n
        //}
    }

    // If something failed, revert all partial modifications
    if (!success) {
        for (int j = 0; j < revert_used; j++) {
            *reverts[j].pos = reverts[j].saved_char;
        }
    }

    return success;
}

const char ** parse_ids_list(char * list_str) {
    char * ptr = list_str;

    // Verifica se a lista está vazia "[]"
    if (ptr[0] == '[' && ptr[1] == ']' && ptr[2] == '\0') {
        return NULL;
    }

    // Conta o número de vírgulas para determinar id_count
    int id_count = 0;
    while (*ptr) {
        if (*ptr == ',') {
            id_count++;
        }
        ptr++;
    }
    id_count++; // O número de IDs é o número de vírgulas + 1

    // Aloca o array de ponteiros para IDs (id_count + 1 para o terminador NULL)
    const char ** ids = malloc((id_count + 1) * sizeof(char *));
    if (!ids) {
        HANDLE_ERROR("Falha na alocação de memória para IDs: %s", list_str);
    }

    // Volta para o início da string
    ptr = list_str;

    // Pula o primeiro '['
    ptr++;

    // Parse da lista de IDs
    for (int i = 0; i < id_count; i++) {
        // Ignora espaços, aspas, colchetes e vírgulas
        while (*ptr == ' ' || *ptr == '\'' || *ptr == '[' || *ptr == ']' || *ptr == ',') {
            ptr++;
        }

        // Pega o início do ID
        char * id_start = ptr;

        // Encontra o final do ID (até a vírgula, ']' ou fim da string)
        while (*ptr != ',' && *ptr != ']' && *ptr != '\0') {
            ptr++;
        }

        // Temporariamente termina a string do ID
        char saved_char = *ptr;
        *ptr = '\0';

        // Remove as aspas simples no início e final
        if (id_start[0] == '\'') {
            id_start++;
        }
        int len = strlen(id_start);
        if (id_start[len - 1] == '\'') {
            id_start[len - 1] = '\0';
        }

        // Adiciona o ID à string pool e armazena o ponteiro no array
        ids[i] = string_pool_get(id_start);

        // Restaura o caractere e avança para o próximo caractere delimitador
        *ptr = saved_char;
        if (*ptr == ',' || *ptr == ']') {
            ptr++;
        }
    }

    // Null-termina o array de IDs
    ids[id_count] = NULL;

    return ids;
}
