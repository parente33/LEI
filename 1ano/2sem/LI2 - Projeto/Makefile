CC = gcc
CFLAGS = -Wall -Wextra -g -std=c11 -Iinclude

SRC_DIR = src
INC_DIR = include
OBJ_DIR = obj

SRCS = Guiao1.c Guiao2.c Guiao3.c Guiao4.c
OBJS = $(SRCS:%.c=$(OBJ_DIR)/%.o)

EXEC = guiao4

all: $(EXEC)

$(EXEC): $(OBJS)
	$(CC) $(CFLAGS) -o $@ $^

$(OBJ_DIR)/%.o: $(SRC_DIR)/%.c $(INC_DIR)/%.h | $(OBJ_DIR)
	$(CC) $(CFLAGS) -c $< -o $@

$(OBJ_DIR):
	mkdir -p $(OBJ_DIR)

clean:
	rm -f $(OBJ_DIR)/*.o $(EXEC)

.PHONY: all clean