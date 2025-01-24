#!/bin/bash

javac -sourcepath src/ -d out/ src/main/Main.java
java -classpath out/:res/ main.Main
