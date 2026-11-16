#include "Queries/query_manager.h"

#include "Queries/query1.h"
#include "Queries/query2.h"
#include "Queries/query3.h"
#include "Queries/query4.h"
#include "Queries/query6.h"
#include "Queries/recomendador.h"
#include "IO/io.h"                      // output_query
#include "error_handler.h"
#include "utils.h"                      // map_query_to_index

#include <stdio.h>                      // fopen() fgets()  fclose ()
#include <stdlib.h>                     // malloc()  free()  strdup() strlen() 
#include <string.h>                     // strtok()  strcpy()  

#define NUM_QUERIES 6
#define MAX_FIELDS 100

struct query_info {
    const char * Query; // Numero da query e formataçao  
    size_t     n_argumentos; // numero de argumentos que a query recebe 
    const char **argumentos; // argumentos 
};

struct query_results {
    size_t n; // numero de resultados 
    char **results; // resultados da query 
};

// Array com as funcoes das queries
QueryFuncs query_funcs [NUM_QUERIES] = {NULL};

// Setter
void set_Query_funcs (QueryFuncs func, int Query) {
    query_funcs[Query-1] = func;
}

// Initit queries managers
void query_manager_init(void) {
    query1_manager_init();
    query2_manager_init();
    query3_managar_init();
    query4_manager_init();
    query5_manager_init();
    query6_manager_init();
}

// Destroyer 
void query_manager_destroy(void) {
    query1_destroy();
    query2_destroy();
    query3_destroy();
    query4_destroy();
    query5_destroy();
}//

// Creates structs
static Query_info create_query_info(const char **fields,int num_fields) {
    if(fields == NULL || num_fields <=0) return NULL;
    Query_info query_info = malloc (sizeof(struct query_info));
     if (!query_info) {
        HANDLE_ERROR("Failed to allocate memory for query_info");
        return NULL;
    }
    query_info->Query = strdup(fields[0]);
    if (!query_info->Query) {
        HANDLE_ERROR("Failed to allocate memory for query string");
        free(query_info);
        return NULL;
    }
    query_info->n_argumentos = num_fields - 1;
    query_info->argumentos = malloc(query_info->n_argumentos * sizeof(char *));
    if (!query_info->argumentos) {
        HANDLE_ERROR("Failed to allocate memory for argumentos");
        free((void*)query_info->Query);
        free(query_info);
        return NULL;
    }
    for (size_t i = 0; i < query_info->n_argumentos; i++) {
        query_info->argumentos[i] = strdup(fields[i+1]);
        if (!query_info->argumentos[i]) {
            HANDLE_ERROR("Failed to allocate memory for an argument");
            for (size_t j = 0; j < i; j++) {
                free((void *)query_info->argumentos[j]);
            }
            free   (query_info->argumentos);
            free   ((void*)query_info->Query);
            free   (query_info);
            return NULL;
        }
    }
    return query_info;
}

Query_results create_query_results(const char **results, int n) {
    if (!results || n <= 0) {
        return NULL;
    }

    Query_results query_results = malloc(sizeof(struct query_results));
    if (!query_results) {
        return NULL;
    }

    query_results->n = (size_t)n;
    query_results->results = malloc((n + 1) * sizeof(char *));
    if (!query_results->results) {
        free(query_results);
        return NULL;
    }

    for (int i = 0; i < n; i++) {
        query_results->results[i] = strdup(results[i]);
        if (!query_results->results[i]) {
            for (int j = 0; j < i; j++) {
                free(query_results->results[j]);
            }
            free(query_results->results);
            free(query_results);
            return NULL;
        }
    }
    query_results->results[n] = NULL;

    return query_results;
}

// Structs destroyers 
void destroy_query_info(Query_info query_info) {
  if (!query_info) return;

    free((void *)query_info->Query);
    for (size_t i = 0; i < query_info->n_argumentos; i++) {
        free((void*)query_info->argumentos[i]);
    }
    free(query_info->argumentos);
    free(query_info);
}

void destroy_query_results(Query_results query_results) {
    if (!query_results) return;

    for (size_t i = 0; i < query_results->n; i++) {
        free(query_results->results[i]);
    }
    free(query_results->results);
    free(query_results);
}
// Getters 

const char** get_arguments    (Query_info query_info)       {return query_info->argumentos;}
size_t       get_n_arguments  (Query_info query_info)       {return query_info->n_argumentos;}
const char **get_results      (Query_results query_results) {return (const char **)query_results->results;} 
size_t       get_size         (Query_results query_results) {return query_results ->n;}

// Parser 

static int parser(char **fields, char *line) {
    char *token = strtok(line, " ");
    int num_fields = 0;
    while (token != NULL) {
        // Remove espaços extras ou caracteres de nova linha
        token[strcspn(token, "\n")] = '\0';  // Remove '\n' no final, caso haja
        fields[num_fields++] = token;
        token = strtok(NULL, " ");
    }
    fields[num_fields] = NULL; // Define o último campo como NULL
    return num_fields;
}

//EXECUTE FILE
void execute_file(const char *file_path,double *q_total_time, int *total_queries) {
    FILE *file = io_open_file(file_path, "r");
    if (!file) {
        HANDLE_ERROR("Error opening command file");
        return;
    }
    char *line = NULL;
    size_t len = 0;
    char *fields[MAX_FIELDS];
    int line_number = 0;
    struct timespec start, end;

    while (io_read_line(file, &line,&len)) {
        line_number++;
        char output_file_path[64];
        snprintf(output_file_path, sizeof(output_file_path), "resultados/command%d_output.txt", line_number);
        FILE * output_file = io_open_file(output_file_path, "w");
        if (!output_file) {
            HANDLE_ERROR("Error creating output file: %s\n", output_file_path);
        }
        int num_fields = parser(fields, line);

        Query_info query_info = create_query_info((const char **)fields,num_fields);

        int index = map_query_to_index (query_info->Query);
        Query_results query_results = NULL;
        if(index >=0 && index<6) {
            clock_gettime(CLOCK_REALTIME, &start);
            query_results = query_funcs[index] (query_info);
           if(total_queries) total_queries[index] +=1;
        }
        output_query(query_results, query_info->Query, output_file);
        destroy_query_info(query_info);
        if(query_results) destroy_query_results(query_results);
        io_close_file(output_file);
        clock_gettime(CLOCK_REALTIME, &end);
        double elapsed = (end.tv_sec - start.tv_sec) + (end.tv_nsec - start.tv_nsec) / 1e9;
        if(q_total_time) q_total_time[index] += elapsed;

    }
    free(line);
    query_manager_destroy();
    fclose(file);
}