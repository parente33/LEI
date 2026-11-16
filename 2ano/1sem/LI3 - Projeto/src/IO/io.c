#include "IO/io.h"

#include <stdio.h>                  // FILE, fprintf, getline, fclose
#include <stdlib.h>                 // malloc, free
#include <string.h>                 // strlen 
#include <sys/types.h>              // ssize_t
#include <unistd.h>                 // access
#include <sys/stat.h>               // mkdir

#include "IO/loader.h"              // load_files
#include "IO/ui.h"                  // display_initial_menu, menu_after_loading_dataset, display_queries_menu
#include "Queries/query_manager.h"  // execute_file
#include "error_handler.h"          // HANDLE_ERROR
#include "utils.h"                  // get_option 

int io_read_line (FILE * file, char ** line, size_t * len) {
    if (!file || !line || !len) return 0;
    
    ssize_t read = getline (line, len, file);
    return (read != -1);
}

void io_write_line (FILE * file, const char * line) {
    if (!file || !line) return;
    fprintf (file, "%s", line);
}

FILE * io_open_file (const char * path, const char * mode) {
    if (!path || !mode) HANDLE_ERROR ("Invalid arguments for opening file.");
    
    FILE * fp = fopen (path, mode);
    if (!fp) HANDLE_ERROR ("Opening file %s with mode %s", path, mode);
    return fp;
}

void io_close_file (FILE * file) {
    if (!file) return;
    if (fclose (file) != 0) HANDLE_ERROR ("Warning: Failed to properly close file.");
}

void output_query (Query_results query_results, const char * formatacao, FILE *output_file) {
     if(!query_results) {
        fprintf(output_file, "\n"); 
        return;
    }
    size_t n = get_size(query_results); 
    const char ** results = get_results(query_results); 
    if(!results) {
        fprintf(output_file, "\n"); 
        return;
    }
    const char *separador = ";";
    if (formatacao[strlen(formatacao) - 1] == 'S') {
        separador = "=";
    }
    for(size_t i=0;i<n;i++) {
        fprintf(output_file,"%s",results[i]);
        if(i<n-1 && strchr(results[i], '\n') == NULL) fprintf(output_file, "%s",separador);
        if(i==n-1 && strchr (results[i],'\n')==NULL) fprintf(output_file, "\n");
    }

}

//-------------------------------------------------------------------------------------//

void create_query_file(int query_number, const char *project_dir) {
    char filename[512];
    snprintf(filename, sizeof(filename), "%s/interactive_query%d_input.txt", project_dir, query_number);

    FILE *file = fopen(filename, "w");
    if (!file) {
        HANDLE_ERROR("Erro ao criar o arquivo para a query");
        return;
    }
    if (query_number == 2) {
        int n;
        char country[100];

        printf("\nInsira o valor de N (inteiro): ");
        if (scanf("%d", &n) != 1) {
            fprintf(stderr, "Erro na entrada do número.\n");
            fclose(file);
            return;
        }
        printf("Insira o nome de um país: ");
        if (scanf("%s", country) != 1) {
            fprintf(stderr, "Erro na entrada do nome do país.\n");
            fclose(file);
            return;
        }
        fprintf(file, "2 %d %s", n, country);
    } else if (query_number == 3) {
        int age_group_start, age_group_end;

        printf("\nInsira o limite inicial da faixa etária (inteiro): ");
        if (scanf("%d", &age_group_start) != 1) {
            fprintf(stderr, "Erro na entrada da faixa etária inicial.\n");
            fclose(file);
            return;
        }
        printf("Insira o limite final da faixa etária (inteiro): ");
        if (scanf("%d", &age_group_end) != 1) {
            fprintf(stderr, "Erro na entrada da faixa etária final.\n");
            fclose(file);
            return;
        }

        fprintf(file, "3 %d %d", age_group_start, age_group_end);
    }
    fclose(file);

    execute_file(project_dir,NULL,NULL);
        
    printf("\nQuery %d executada! Consulte os resultados na pasta /resultados.\n", query_number);
}

// Used after the user loads up a valid dataset
void handle_post_loading_menu (const char *project_dir) {
    while (1) {
        menu_after_loading_dataset();

        char *post_loading_option = get_option();

        if (strcmp(post_loading_option, "1") == 0) {
            handle_queries_menu(project_dir);
        } else if (strcmp(post_loading_option, "2") == 0) {
            printf("\nSaindo do programa. Volte sempre!\n");
            free(post_loading_option);
            exit(0);
        } else {
            fprintf(stderr, "\nOpção inválida. Tente novamente!\n");
        }
        free(post_loading_option);
    }
}

// Deals with the user's input inside the query menu
void handle_queries_menu (const char *project_dir) {
    while (1) {
        display_queries_menu();

        char *query_option = get_option();
        int option = atoi(query_option);

        if (option == 1 || option == 4 || option == 5 || option == 6) {
            fprintf(stderr, "\nPedimos imensa desculpa! Esta query ainda não foi implementada com sucesso! :(\n");
        } else if (option == 2 || option == 3) {
            create_query_file(option, project_dir);
        } else {
            printf("\nSaindo do menu de queries...\n");
            free(query_option);
            return;
        }
        free(query_option);
    }
}

// Base user handler function when in the interactive program
void process_initial_menu(int *dataset_loaded, char *dataset_path, const char *project_dir) {
    char *option = get_option();

    if (strcmp(option, "1") == 0) {
        if (!(*dataset_loaded)) {
            printf("\nInsira o caminho do dataset: ");
            if (scanf("%s", dataset_path) == 1) {
                load_files(dataset_path);

                if (access(dataset_path, F_OK) == 0) {
                    printf("\nDataset carregado com sucesso!\n");
                    *dataset_loaded = 1;
                    handle_post_loading_menu(project_dir);
                } else {
                    fprintf(stderr, "\nErro ao carregar o dataset. Certifique-se de que o caminho é válido.\n");
                }
            } else {
                fprintf(stderr, "\nErro ao carregar o dataset. Certifique-se de que o caminho é válido.\n");
            }
        }
    } else if (strcmp(option, "2") == 0) {
        if (!(*dataset_loaded)) {
            fprintf(stderr, "\nErro: Não pode executar nenhuma query até carregar e validar o dataset. Tente outra vez!\n");
        } else {
            handle_queries_menu(project_dir);
        }
    } else if (strcmp(option, "3") == 0) {
        printf("\nSaindo do programa. Volte sempre!\n");
        free(option);
        exit(0);
    } else {
        fprintf(stderr, "\nOpção inválida. Tente outra vez!\n");
    }
    free(option);
}
#define MAX_COMMANDS 6
#define MAX_LINE_LENGTH 1024

void module_teste(const char *file_path, const char *comand_path, double *q_total_time, int *total_queries) {
    int total_commands = 0;
    for (int i = 0; i < MAX_COMMANDS; i++) {
        total_commands += total_queries[i];
    }

    int num_differences[MAX_COMMANDS] = {0};
    int *differences[MAX_COMMANDS];
    int *command_numbers[MAX_COMMANDS]; 
    for (int i = 0; i < MAX_COMMANDS; i++) {
        differences[i] = calloc(total_commands, sizeof(int));
        command_numbers[i] = calloc(total_commands, sizeof(int)); 
        if (differences[i] == NULL || command_numbers[i] == NULL) {
            HANDLE_ERROR("Erro ao alocar memória para differences ou command_numbers");
            return;
        }
    }

    FILE *file_comandos = io_open_file(comand_path, "r");
    if (!file_comandos) {
        HANDLE_ERROR("Erro ao abrir arquivo de comandos.");
        return;
    }

    char *line_comando = NULL;
    size_t len_comando = 0;
    size_t line_count = 0; 

    while (io_read_line(file_comandos, &line_comando, &len_comando)) {
        line_count++;

        char *firstElement = strtok(line_comando, " \t\n");
        if (!firstElement) {
            continue;
        }

        // Determinar o índice da query
        int index = map_query_to_index(firstElement);
        if (index < 0 || index >= MAX_COMMANDS) {
            HANDLE_ERROR("Query desconhecida: %s na linha %zu.", firstElement, line_count);
            continue;
        }

        char file_name[64];
        snprintf(file_name, sizeof(file_name), "command%zu_output.txt", line_count);

        char *file_path_esperado = construct_file_path(file_path, file_name);
        char *file_path_results = construct_file_path("resultados", file_name);

        FILE *file_esperado = io_open_file(file_path_esperado, "r");
        FILE *file_results = io_open_file(file_path_results, "r");

        if (!file_esperado || !file_results) {
            HANDLE_ERROR("Erro ao abrir arquivos de resultados esperados ou obtidos para linha %zu.", line_count);
            free(file_path_esperado);
            free(file_path_results);
            continue;
        }

        char *line_esperado = NULL, *line_results = NULL;
        size_t len_esperado = 0, len_results = 0;
        size_t line_number = 1;

        while (io_read_line(file_esperado, &line_esperado, &len_esperado)) {
            int read_results = io_read_line(file_results, &line_results, &len_results);
            if (read_results != 1 || strcmp(line_esperado, line_results) != 0) {
                differences[index][num_differences[index]] = line_number;
                command_numbers[index][num_differences[index]] = (int)line_count; // Armazenar o número do comando
                num_differences[index]++;
            }
            line_number++;
        }

        if (io_read_line(file_results, &line_results, &len_results)) {
            differences[index][num_differences[index]] = line_number;
            command_numbers[index][num_differences[index]] = (int)line_count; // Armazenar o número do comando
            num_differences[index]++;
        }

        fclose(file_esperado);
        fclose(file_results);
        free(file_path_esperado);
        free(file_path_results);
        free(line_esperado);
        free(line_results);
    }

    fclose(file_comandos);
    free(line_comando);

    for (int i = 0; i < MAX_COMMANDS; i++) {
        printf("Q%d : %d de %d ok!\n", i + 1, total_queries[i] - num_differences[i], total_queries[i]);
        for (int j = 0; j < num_differences[i]; j++) {
            printf("Discrepância na query %d: linha %d em resultados/command%d_output.txt\n",
                   i + 1, differences[i][j], command_numbers[i][j]); 
        }
    }

    printf("Execution times:\n");
    for (int i = 0; i < MAX_COMMANDS; i++) {
        printf("Q%d: %.6f ms\n", i + 1, q_total_time[i]);
    }

    for (int i = 0; i < MAX_COMMANDS; i++) {
        free(differences[i]);
        free(command_numbers[i]); 
    }
}
