#!/bin/bash
script_dir=$(cd `dirname $0`; pwd)
cd ${script_dir}
java -Xmx128m -Dorg.tensorflow.NativeLibrary.DEBUG=1 -Djava.library.path=src/main/resources/lib/tensorflow/linux_x86_64 -jar target/*.jar
