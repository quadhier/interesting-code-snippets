.PHONY: run repeat gdb clean
BIN := iriw

$(BIN): iriw.cpp
	@g++ -g -O3 -std=c++14 -pthread iriw.cpp -o $(BIN)

run: $(BIN)
	@./$(BIN)

repeat: $(BIN)
	@for i in $$(seq 1 100); \
  do \
    ./$(BIN); \
  done

gdb: $(BIN)
	gdb ./$(BIN)

clean:
	/bin/rm -f $(BIN)
