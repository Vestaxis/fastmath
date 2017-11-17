÷OS:=$(shell uname -o)
MAKEFILE_DIR:=$(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))
LDFLAGS := -L../lib

CFLAGS := -g -O3 -fPIC  -Ifastmath 

prefix := $(DESTDIR)

FULL_JAVA_HOME := /usr/lib/jvm/default-java
PLATFORM := linux
LIBS := -llapack -latlas -lblas -llapack_atlas -lmpfr -lflint-arb -lflint
OUTLIBDIR := $(MAKEFILE_DIR)/../../target/classes
OUTLIB := $(OUTLIBDIR)/libfastmath.so 
LDFLAGS := $(LDFLAGS) \
	-L/usr/lib/x86_64-linux-gnu \
	-L/usr/lib/atlas-base \
	-L/usr/lib64/atlas
CFLAGS := -std=c++11 $(CFLAGS) -I/usr/include/flint -I /usr/include/x86_64-linux-gnu -I. -I $(FULL_JAVA_HOME)/include -I $(FULL_JAVA_HOME)/include/$(PLATFORM)/ 

all: $(OUTLIB) cool cmpzeros

OBJS := fastmath.o jni.o Matrix.o Vector.o LambertW.o chebychev.o cnlnewt.o cbroyden.o cged.o selfexciting.o

cool: $(OBJS) cool.o
	g++ -o $@ $(filter %.o,$^) $(LDFLAGS) $(LIBS)

cmpzeros: $(OBJS) cmpzeros.o
	g++ -o $@ $(filter %.o,$^) $(LDFLAGS) $(LIBS)

$(OUTLIB): $(OBJS) 
	g++ -o $@ $(filter %.o,$^) -shared  $(LDFLAGS) $(LIBS)

%.o: %.c
	gcc -c -o $@ $< $(CFLAGS)

%.o: %.cpp 
	g++ -c -o $@ $< $(CFLAGS)

clean:
	-rm -rf *.o $(OUTLIB)

install:
	mkdir -p $(prefix)/usr/lib
	cp -pv $(OUTLIB) $(prefix)/usr/lib
	mkdir -p $(prefix)/usr/include
	cp -pv fastmath.h fastmath_*.h $(prefix)/usr/include
	mkdir -p $(prefix)/usr/include/fastmath
	cp -pv fastmath/*.h $(prefix)/usr/include/fastmath

