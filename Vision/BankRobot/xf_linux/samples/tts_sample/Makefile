#common makefile header

DIR_INC = ../../include
DIR_BIN = ../../bin
DIR_LIB = ../../libs
# For JNI
JAVA_HOME = /opt/java/jdk1.8.0_05

TARGET	= tts_sample
BIN_TARGET = $(DIR_BIN)/$(TARGET)
LIB_TARGET = $(DIR_BIN)/libtts_native.so

CROSS_COMPILE = 
CFLAGS = -g -Wall -I$(DIR_INC) -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux -fPIC

ifdef LINUX64
LDFLAGS := -L$(DIR_LIB)/x64
else
LDFLAGS := -L$(DIR_LIB)/x86 
endif
LDFLAGS += -lmsc -lrt -ldl -lpthread

OBJECTS := $(patsubst %.c,%.o,$(wildcard *.c))

all: $(BIN_TARGET) $(LIB_TARGET) 

$(BIN_TARGET) : $(OBJECTS)
	$(CROSS_COMPILE)gcc $(CFLAGS) $^ -o $@ $(LDFLAGS)

$(LIB_TARGET) : $(OBJECTS)
	$(CROSS_COMPILE)gcc $(CFLAGS) -shared -fPIC $^ -o $@ $(LDFLAGS)
	
%.o : %.c
	$(CROSS_COMPILE)gcc -c $(CFLAGS) $< -o $@
clean:
	@rm -f *.o $(BIN_TARGET)

.PHONY:clean

#common makefile foot
