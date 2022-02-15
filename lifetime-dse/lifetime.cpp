#include <stdlib.h>
#include <string.h>
#include <stdio.h>

#if __GNUC__ >= 5
#define NO_OPT_ATTR __attribute__((optimize(0)))
#else
#define NO_OPT_ATTR
#endif

NO_OPT_ATTR void mark(size_t size, void *mem) {
  memset(mem, 0xff, size);
}

class A {
 public:
  int tag;

  void * operator new(size_t x) {
    A *a = (A *) malloc(sizeof(A));
    mark(sizeof(A), a);
    a->tag = 2;
    return a;
  }

  A() {
    printf("[in ctor] %x\n", this->tag);
  }
};

int main() {
  A *a = new A();
  printf("[in main] %x\n", a->tag);
}
