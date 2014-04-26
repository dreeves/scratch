#!/bin/bash
java -cp "$CLOJURE;src;java/_.jar;dep/src;dep/java/_.jar;dep/jna-4.0.0.jar" clojure.main -e "(use 'batteries) (use 'life) (use 'clojure.java.io) (main)" -r