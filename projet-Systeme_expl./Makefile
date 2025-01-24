# Nom de l'exécutable
EXECUTABLE = fsh

# Compilateur
CC = gcc

# Options de compilation
CFLAGS = -Wall -Wextra -pedantic -g

# repertoire des fichiers source
SRC_DIR = src/

# Fichiers source
SRC = $(wildcard $(SRC_DIR)*.c *.h)

# vérifie si fsh existe et est à jour si non execute la commande pour compiler fsh
all: $(EXECUTABLE)

# $(EXECUTABLE): $(SRC) signifie qu'il faut d'abord compiler les fichier de SRC avant de compiler l'executable
#$(CC) $(CFLAGS) -o $(EXECUTABLE) $(SRC) : compile les fichier dans SRC
$(EXECUTABLE): $(SRC)
	$(CC) $(CFLAGS) -o $(EXECUTABLE) $(SRC) -lreadline
	

# nettoie les fichier compilés
clean:
	rm -f $(EXECUTABLE) *.o *~ core

#.PHONY indique que all et clean ne sont pas des noms de fichier mais plutôt des "options" de make
.PHONY: all clean
