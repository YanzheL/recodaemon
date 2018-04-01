package org.yanzhe.robomaster.recodaemon.net.detector;

import com.google.protobuf.Any;

public interface Detector {

  Any detect(Any body) throws Exception;
}
