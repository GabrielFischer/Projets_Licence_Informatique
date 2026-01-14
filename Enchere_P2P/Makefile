# === Compilation configuration ===
CC = gcc
CFLAGS = -Wall -Wextra -std=c11 -Iinclude
LDFLAGS = -lpthread -lssl -lcrypto

# === Source files ===
SRC = src/connexions.c src/msg.c src/state.c src/enchere.c src/keys.c 
OBJ = $(SRC:.c=.o)
EXEC = auction_p2p

# === Rules ===
all: $(EXEC)

$(EXEC): $(OBJ)
	$(CC) -o $@ $^ $(LDFLAGS)

%.o: %.c
	$(CC) $(CFLAGS) -c $< -o $@

run: $(EXEC)
	./$(EXEC) ::1 10000

clean:
	rm -f $(OBJ) $(EXEC)

.PHONY: all clean run
