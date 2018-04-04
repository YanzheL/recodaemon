#!/bin/bash
script_dir=$(cd `dirname $0`; pwd)
JAVA_HOME=/usr/lib/jvm/jdk-9.0.4
cd ${script_dir}
$JAVA_HOME/bin/java -Xmx1000m -Dorg.tensorflow.NativeLibrary.DEBUG=1 -Djava.library.path=src/main/resources/lib/processors/linux-x86_64 -jar target/*.jar
