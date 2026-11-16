#include "Queries/query_manager.h"                // get_arguments set_Query_funcs
#include "Queries/query6.h" 

void query6_manager_init(void) {
    set_Query_funcs(query_6,6);
} 

Query_results query_6(Query_info query_info) {
    return NULL;
}