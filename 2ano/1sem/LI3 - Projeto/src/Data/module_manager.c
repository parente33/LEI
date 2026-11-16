#include "Data/module_manager.h"

#include "entity_registry.h"

#include "Data/Managers/album_manager.h"
#include "Data/Managers/artist_manager.h"
#include "Data/Managers/history_manager.h"
#include "Data/Managers/music_manager.h"
#include "Data/Managers/user_manager.h"
#include "Queries/query_manager.h"

#include "Data/StringPool/string_pool.h"

void module_manager_init (void) {
    entity_registry_init ();
    query_manager_init   ();
    album_manager_init   ();
    artist_manager_init  ();
    history_manager_init ();
    music_manager_init   ();
    user_manager_init    ();

    string_pool_init     ();
}

void module_manager_destroy (void) {
    entity_registry_destroy ();

    album_manager_destroy   ();
    artist_manager_destroy  ();
    history_manager_destroy ();
    music_manager_destroy   ();
    user_manager_destroy    ();
    query_manager_destroy   ();
    string_pool_destroy     ();
}
