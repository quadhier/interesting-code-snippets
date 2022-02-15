CXXFLAGS := -O3 -g
CXXFLAGS_4 := $(CXXFLAGS)
CXXFLAGS_9 := $(CXXFLAGS) # -fno-lifetime-dse

SRCS := lifetime.cpp

.PHONY: default c4 c9 r4 r9 cln
default: c9

c4:
	g++-4.8 $(CXXFLAGS_4) -o lifetime $(SRCS)

c9:
	g++-9   $(CXXFLAGS_9) -o lifetime $(SRCS)

r4: c4
	./lifetime

r9: c9
	./lifetime

cln:
	rm -f lifetime
