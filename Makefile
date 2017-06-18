.PHONY: cpp java

cpp:
	make -f cpp/fastmath/fastmath/Makefile headers
	cd cpp/fastmath && make

java: cpp
	mvn install

clean:
	cd cpp/fastmath && make clean

