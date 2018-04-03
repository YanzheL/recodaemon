#!/bin/bash
script_dir=$(cd `dirname $0`; pwd)
cd ${script_dir}
java -Xmx1000m -Dorg.tensorflow.NativeLibrary.DEBUG=1 -Djava.library.path=src/main/resources/lib/tensorflow/linux-x86_64-gpu:src/main/resources/lib/processors/linux-x86_64 -jar target/*.jar
