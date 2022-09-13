# Please refer to https://stackoverflow.com/a/40603678/10302506

.PHONY: rightorder wrongorder buildlib

rightorder: buildlib
	gcc -L. main.c -llib

wrongorder: buildlib
	gcc -L. -llib main.c

buildlib:
	gcc -shared -o liblib.so lib.c

clean:
	/bin/rm -f *.so a.out
