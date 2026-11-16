#include "IO/ui.h"
#include "IO/io.h"
#include "IO/loader.h"
#include "Queries/query_manager.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

int main(void) {
    int dataset_loaded = 0;
    char dataset_path[512] = {0};

    char project_dir[512] = {0};
    if (!getcwd(project_dir, sizeof(project_dir))) {
        fprintf(stderr, "Erro ao obter o caminho da diretoria atual.\n");
        return 1;
    }

    while (1) {
        display_initial_menu();
        process_initial_menu(&dataset_loaded, dataset_path, project_dir);
    }

    return 0;
}