#ifndef ENTITY_REGISTRY_H
#define ENTITY_REGISTRY_H

#include <stddef.h>

#include "entity_interface.h"

/**
 * @brief Initialize the global entity registry.
 */
void entity_registry_init                   (void);

/**
 * @brief Registers a new entity interface with the global registry.
 * @param iface  Pointer to an entityInterface struct for the entity.
 */
void entity_registry_register               (const entityInterface * iface);

/**
 * @brief Gets the number of registered entities.
 * @return The count of registered entities.
 */
size_t entity_registry_count                (void);

/**
 * @brief Retrieves an entity interface at a given index.
 * @param index  The index in the registry (0 <= index < entity_registry_count()).
 * @return Pointer to the entity interface at that index.
 */
const entityInterface * entity_registry_get (size_t index);

/**
 * @brief Destroys/frees the global registry.
 */
void entity_registry_destroy                (void);

#endif // ENTITY_REGISTRY_H
