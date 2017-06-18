S:=$(shell uname -o)
MAKEFILE_DIR:=$(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))
BASE_DIR:=$(MAKEFILE_DIR)/../../..

FULL_JAVA_HOME := /usr/lib/jvm/default-java
PLATFORM := linux

CFLAGS := -I /usr/include -I $(FULL_JAVA_HOME)/include -I $(FULL_JAVA_HOME)/include/$(PLATFORM) -I../headers -g -O3 -march=i686
LDFLAGS := -shared -Wl,-rpath=lib $(LIBS)

LIBDIR := ..
LIB := $(LIBDIR)/libfastmath.so

NATIVE_CLASSES := fastmath.BLAS1 fastmath.BufferUtils fastmath.DoubleMatrix fastmath.Vector fastmath.Functions
.PHONY: headers

all: $(LIB)

headers:
	#mvn -f $(BASE_DIR)/pom.xml dependency:build-classpath
	javah -force -v -jni -d $(BASE_DIR)/cpp/fastmath -classpath $(BASE_DIR)/target/classes:`cat $(BASE_DIR)/class.path` $(NATIVE_CLASSES)

OBJS := fastmath.o Matrix.o Vector.o

$(LIB): $(OBJS)
	g++ $(LDFLAGS) -o $@ $(filter %.o,$^) 

%.o: %.c
	gcc -c -o $@ $< $(CFLAGS)

%.o: %.cpp
	g++ -c -o $@ $< $(CFLAGS)

clean:
	-rm -rf *.o $(LIB)

