# Compiler
CC = gcc

# Compiler flags
CFLAGS += -Wall -Wextra -g3 -Iinclude -Wpedantic \
          -Wformat=2 -Wno-unused-parameter -Wshadow \
          -Wwrite-strings -Wstrict-prototypes -Wold-style-definition \
          -Wredundant-decls -Wnested-externs \
          $(shell pkg-config --cflags glib-2.0)

# Linker flags
LDFLAGS += $(shell pkg-config --libs glib-2.0)

# Directories
SRCDIR = src
OBJDIR = obj

# Automatically find all source files in the Data folder
DATA_SRCS 	   = $(shell find $(SRCDIR)/Data -type f -name "*.c")
IO_SRCS   	   = $(shell find $(SRCDIR)/IO -type f -name "*.c")
VALIDATOR_SRCS = $(shell find $(SRCDIR)/Validator -type f -name "*.c")
QUERIES_SRCS   = $(shell find $(SRCDIR)/Queries -type f -name "*.c")

# Source files for each mode
PRINCIPAL_SRCS  = $(SRCDIR)/programa-principal.c   \
                  $(DATA_SRCS) 				       \
				  $(IO_SRCS)			 	       \
				  $(VALIDATOR_SRCS) 			   \
				  $(QUERIES_SRCS)				   \
				  $(SRCDIR)/entity_registry.c 	   \
                  $(SRCDIR)/utils.c 			   \

TESTES_SRCS     = $(SRCDIR)/programa-testes.c      \
                  $(DATA_SRCS) 				       \
				  $(IO_SRCS) 				 	   \
				  $(VALIDATOR_SRCS) 			   \
				  $(QUERIES_SRCS)                  \
				  $(SRCDIR)/entity_registry.c 	   \
                  $(SRCDIR)/utils.c 			   \

INTERATIVO_SRCS = $(SRCDIR)/programa-interativo.c   \
                  $(DATA_SRCS) 				       \
				  $(IO_SRCS) 				 	   \
				  $(VALIDATOR_SRCS) 			   \
				  $(QUERIES_SRCS)                  \
				  $(SRCDIR)/entity_registry.c 	   \
                  $(SRCDIR)/utils.c 			   \

# Object files for each mode
PRINCIPAL_OBJS  = $(patsubst $(SRCDIR)/%.c, $(OBJDIR)/%.o, $(PRINCIPAL_SRCS))
TESTES_OBJS     = $(patsubst $(SRCDIR)/%.c, $(OBJDIR)/%.o, $(TESTES_SRCS))
INTERATIVO_OBJS = $(patsubst $(SRCDIR)/%.c, $(OBJDIR)/%.o, $(INTERATIVO_SRCS))

# Target executables
TARGETS = programa-principal programa-testes programa-interativo

# Default target
all: $(TARGETS)

# Build programa-principal
programa-principal: $(PRINCIPAL_OBJS)
	$(CC) $(CFLAGS) -o $@ $^ $(LDFLAGS)

# Build programa-testes
programa-testes: $(TESTES_OBJS)
	$(CC) $(CFLAGS) -o $@ $^ $(LDFLAGS)

# Build programa-interativo
programa-interativo: $(INTERATIVO_OBJS)
	$(CC) $(CFLAGS) -o $@ $^ $(LDFLAGS)

# Compile individual source files into object files
$(OBJDIR)/%.o: $(SRCDIR)/%.c
	@mkdir -p $(dir $@)
	$(CC) $(CFLAGS) -c $< -o $@

# Clean target
clean:
	rm -f $(OBJDIR)/*.o $(TARGETS)
	@if [ -d "resultados" ]; then rm -f resultados/*.csv resultados/*.txt; fi
	@rm -rf $(OBJDIR)

.PHONY: all clean
