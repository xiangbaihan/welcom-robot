#/*
# * Copyright (C) 2014  RoboPeak
# *
# * This program is free software: you can redistribute it and/or modify
# * it under the terms of the GNU General Public License as published by
# * the Free Software Foundation, either version 3 of the License, or
# * (at your option) any later version.
# *
# * This program is distributed in the hope that it will be useful,
# * but WITHOUT ANY WARRANTY; without even the implied warranty of
# * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# * GNU General Public License for more details.
# *
# * You should have received a copy of the GNU General Public License
# * along with this program.  If not, see <http://www.gnu.org/licenses/>.
# *
# */
#
HOME_TREE := ../
# For JNI
JAVA_HOME = /opt/java/jdk1.8.0_05

MODULE_NAME := $(notdir $(CURDIR))
LIB_NAME := librplidar_native.so

include $(HOME_TREE)/mak_def.inc

CXXSRC += main.cpp
C_INCLUDES += -I$(CURDIR)/../sdk/include -I$(JAVA_HOME)/include -I$(JAVA_HOME)/include/linux 

EXTRA_OBJ := 
LD_LIBS += -lstdc++ -lpthread

all: build_lib

include $(HOME_TREE)/mak_common.inc

clean: clean_lib
