#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <assert.h>
#include <limits.h>
#include <stdbool.h>

#define TABLE_SIZE INT_MAX

typedef struct Node { 
    int key;
    struct Node *next;
} Node;

typedef struct HashTable {
    Node *table1;
    Node *table2;
    int size;
} HashTable;

// ---- // ---- // ---- // ---- // ---- // ---- // ----

int hash(int key, int table_size) {
    return key % table_size;
}

int hash2(int key, int table_size) {
    return (key / table_size) % table_size;
}

void criarHashTable(HashTable *hash_table, int tamanho) {
    hash_table->table1 = (Node *)malloc(tamanho * sizeof(Node));
    hash_table->table2 = (Node *)malloc(tamanho * sizeof(Node));
    hash_table->size = tamanho;

    for (int i = 0; i < tamanho; i++) {
        hash_table->table1[i].key = -1;
        hash_table->table1[i].next = NULL;
        hash_table->table2[i].key = -1;
        hash_table->table2[i].next = NULL;
    }
}

struct Node* newNode(int key) {
    struct Node* node = (struct Node*)malloc(sizeof(struct Node));
    node->key = key;
    node->next = NULL;
    return node;

    free(node);
}

// ---- // ---- // ---- // ---- // ---- // ---- // ----

int linearProbing(int index, int table_size) {
    return (index + 1) % table_size;
}

void adicionaOpen(HashTable *hash_table, int key, int table_size) {
    int index = hash(key, table_size);
    int originalIndex = index;
    int deletedIndex = -1;

    while (hash_table->table1[index].key != -1 && hash_table->table1[index].key != key) {
        if (hash_table->table1[index].key == -2 && deletedIndex == -1) {
            deletedIndex = index;
        }
        index = linearProbing(index, table_size);
        if (index == originalIndex) {
            printf("GIVING UP!\n");
            return;
        }
    }

    if (hash_table->table1[index].key == key) {
        if (deletedIndex != -1) {
            hash_table->table1[deletedIndex].key = hash_table->table1[index].key;
            hash_table->table1[index].key = -2;
            printf("%d -> %d\n", deletedIndex, key);
            printf("OK\n");
            return;
        } else {
            printf("%d EXISTS\n", key);
            return;
        }
    }

    hash_table->table1[index].key = key;
    printf("%d -> %d\n", index, key);
    printf("OK\n");
}

void removeOpen(HashTable *hash_table, int key, int table_size) {
    int index = hash(key, table_size);

    while (hash_table->table1[index].key != -1) {
        if (hash_table->table1[index].key == key) {
            hash_table->table1[index].key = -2;
            printf("OK\n");
            return;
        }
        index = linearProbing(index, table_size);
        if (index == hash(key, table_size)) {
            break;
        }
    }

    printf("NO\n");
}

int consultaOpen(HashTable *hash_table, int key, int table_size) {
    int index = hash(key, table_size);
    int originalIndex = index;
    int deletedIndex = -1;

    while (hash_table->table1[index].key != -1) {
        if (hash_table->table1[index].key == key) {
            if (deletedIndex != -1) {
                hash_table->table1[deletedIndex].key = hash_table->table1[index].key;
                hash_table->table1[index].key = -2;
                return deletedIndex;
            } else {
                return index;
            }
        }
        if (hash_table->table1[index].key == -2 && deletedIndex == -1) {
            deletedIndex = index;
        }
        index = linearProbing(index, table_size);
        if (index == originalIndex) {
            break;
        }
    }

    return -1;
}

void imprimeOpen(HashTable *hash_table) {
    for (int i = 0; i < hash_table->size; i++) {
        if (hash_table->table1[i].key != -1) {
            if (hash_table->table1[i].key == -2) {
                printf("%d\tD\n", i);
            } else {
                printf("%d\t%d\n", i, hash_table->table1[i].key);
            }
        }
    }
}


// ---- // ---- // ---- // ---- // ---- // ---- // ----

void adicionaLink(HashTable *hash_table, int key, int table_size) {
    int index = hash(key, table_size);

    Node* newNode = (Node*)calloc(1, sizeof(Node));
    if (newNode == NULL) {
        printf("Erro na alocação de memória.\n");
        return;
    }
    
    newNode->key = key;
    newNode->next = NULL;

    if (hash_table->table1[index].key == -1) {
        hash_table->table1[index] = *newNode;
        printf("%d -> %d\n", index, key);
        printf("OK\n");
    } else {
        Node* current = &hash_table->table1[index];
        while (current->next != NULL) {
            if (current->key == key) {
                printf("%d EXISTS\n", key);
                free(newNode);
                return;
            }
            current = current->next;
        }
        current->next = newNode;
        printf("%d -> %d\n", index, key);
        printf("OK\n");
    }
}

void removeLink (HashTable *hash_table, int key, int table_size) {
    int index = hash(key, table_size);
    Node* current = &hash_table->table1[index];
    Node* prev = NULL;

    if (hash_table->table1[index].key == key) {
        Node* temp = hash_table->table1[index].next;
        if (temp != NULL) {
            hash_table->table1[index].next = temp->next;
            free(temp);
        }
        hash_table->table1[index].key = -1;
        printf("OK\n");
        return;
    }

    while (current != NULL && current->key != key) {
        prev = current;
        current = current->next;
    }

    if (current == NULL) {
        printf("NO\n");
    } else {
        prev->next = current->next;
        free(current);
        printf("OK\n");
    }
}

void consultaLink (HashTable *hash_table, int key, int table_size) {
    int index = hash(key, table_size);

    Node* current = &hash_table->table1[index];
    while (current != NULL) {
        if (current->key == key) {
            printf("%d\n", index);
            return;
        }
        current = current->next;
    }
    printf("NO\n");
}

void imprimeReverse (Node *node) {
    if (node == NULL)
        return;
    imprimeReverse(node->next);
    printf(" %d", node->key);
}

void imprimeLink (HashTable *hash_table) {
    for (int i = 0; i < hash_table->size; i++) {
        if (hash_table->table1[i].key != -1) {
            printf("%d", i);
            imprimeReverse(hash_table->table1[i].next);
            printf(" %d\n", hash_table->table1[i].key);
        }
    }
}

// ---- // ---- // ---- // ---- // ---- // ---- // ----

//void adicionaCuckoo() {
//  
//}
//
//void removeCuckoo() {
//   
//}
//
//void consultaCuckoo() {
//    
//}
//
//
//void imprimeCuckoo() {
//    
//}

// ---- // ---- // ---- // ---- // ---- // ----

void processaOpen (HashTable hash_table, int key, int table_size, char resposta) {
    if (resposta == 'P') {
        imprimeOpen(&hash_table);
    } else if (resposta == 'D') {
        removeOpen(&hash_table, key, table_size);
    } else if (resposta == 'C') {
        consultaOpen(&hash_table, key, table_size);
    } else if (resposta == 'I') {
        adicionaOpen(&hash_table, key, table_size);
    }
}

void processaLink (HashTable hash_table, int key, int table_size, char resposta) {
    if (resposta == 'P') {
        imprimeLink(&hash_table);
    } else if (resposta == 'D') {
        removeLink(&hash_table, key, table_size);
    } else if (resposta == 'C') {
        consultaLink(&hash_table, key, table_size);
    } else if (resposta == 'I') {
        adicionaLink(&hash_table, key, table_size);
    }
}

//void processaCuckoo (HashTable hash_table, int key, char resposta) {
//    if (resposta == 'P') {
//        imprimeCuckoo(&hash_table, key);
//    } else if (resposta == 'D') {
//        removeCuckoo(&hash_table, key);
//    } else if (resposta == 'C') {
//        consultaCuckoo(&hash_table, key);
//    } else if (resposta == 'I') {
//        adicionaCuckoo(&hash_table, key, hash_table.size);
//    }
//}

// ---- // ---- // ---- // ---- // ---- // ----

int main() {
    int P;
    char resolution_type[10];
    assert(scanf("%d", &P) == 1);
    assert(scanf("%s", resolution_type) == 1);

    HashTable hash_table;
    criarHashTable(&hash_table, P);
    
    char input[20];
    while (fgets(input, sizeof(input), stdin) != NULL) {
        char word;
        int key;

        if (sscanf(input, " %c %d", &word, &key) == 2) {
            if (resolution_type[0] == 'O') {
                processaOpen(hash_table, key, P, word);
            } else if (resolution_type[0] == 'L') {
                processaLink(hash_table, key, P, word);
            } else if (resolution_type[0] == 'C') {
                return 1; //processaCuckoo(hash_table, key, word);
            }
        } else if (sscanf(input, " %c", &word) == 1) {
            if (resolution_type[0] == 'O') {
                imprimeOpen(&hash_table);
            } else if (resolution_type[0] == 'L') {
                imprimeLink(&hash_table);
            } else if (resolution_type[0] == 'C') {
                return 1; //imprimeCuckoo(&hash_table, key);
            }
        }
    }

    for (int i = 0; i < hash_table.size; i++) {
        Node *current = hash_table.table1[i].next;
        while (current != NULL) {
            Node *temp = current;
            current = current->next;
            free(temp);
        }
    }

    // Free memory for hash table arrays
    free(hash_table.table1);
    free(hash_table.table2);

    return 0;
}