#!/bin/bash

cd src/main/java/


javac -cp '/home/trinity/.m2/repository/org/bytedeco/javacpp/1.4.1/javacpp-1.4.1.jar:/home/trinity/.m2/repository/org/bytedeco/javacpp-presets/opencv/3.4.0-1.4/opencv-3.4.0-1.4.jar' \
      org/yanzhe/robomaster/recodaemon/core/processor/nativeimpl/NativeProcessorLibrary.java

java --class-path '/home/trinity/.m2/repository/org/bytedeco/javacpp-presets/opencv/3.4.0-1.4/opencv-3.4.0-1.4.jar' \
      -jar '/home/trinity/.m2/repository/org/bytedeco/javacpp/1.4.1/javacpp-1.4.1.jar' \
      org.yanzhe.robomaster.recodaemon.core.processor.nativeimpl.NativeProcessorLibrary

rm -rf ../resources/lib/processors/*

mv 'org/yanzhe/robomaster/recodaemon/core/processor/nativeimpl/linux-x86_64' '../resources/lib/processors/'
