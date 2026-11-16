#include <stdio.h>

#include "Data/module_manager.h"
#include "IO/loader.h"
#include "error_handler.h"
#include "Queries/query_manager.h"

#include <sys/resource.h>
#include <time.h>

double q_total_time[6] = {0.00};
int total_queries[6] = {0};
int main (int argc, char * argv[]) {

    struct timespec start, end;
    double elapsed;

    clock_gettime (CLOCK_REALTIME, &start);

    // Check for required arguments (dataset folder and command file)
    if (argc < 3) HANDLE_ERROR ("Incorrect number of arguments %d instead of 2", argc - 1);

    module_manager_init    ();
    load_files             (argv[1]);
    execute_file           (argv[2], q_total_time, total_queries);
    module_manager_destroy ();

    clock_gettime (CLOCK_REALTIME, &end);

    elapsed = (end.tv_sec - start.tv_sec) + (end.tv_nsec - start.tv_nsec) / 1e9;
    printf("Elapsed time: %.2f seconds\n", elapsed);

    struct rusage r_usage;
    getrusage(RUSAGE_SELF, &r_usage);
    printf("Memory usage: %ld MB\n", r_usage.ru_maxrss / 1024);

    return 0;
}