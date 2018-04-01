package org.yanzhe.robomaster.recodaemon.net.detector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yanzhe.robomaster.recodaemon.core.classifier.ClassifierFactory;
import org.yanzhe.robomaster.recodaemon.core.classifier.ImageClassifier;
import org.yanzhe.robomaster.recodaemon.core.processor.ImageProcessor;
import org.yanzhe.robomaster.recodaemon.core.processor.ProcessorFactory;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto.TargetCells.Cell;

import java.util.List;

public abstract class Detector<C extends ImageClassifier, P extends ImageProcessor> {
    public static Logger logger = LogManager.getLogger(Detector.class);

    protected C classifier;
    protected P processor;

    public Detector(Class<C> classifierCls, Class<P> processorCls) {
        this(ClassifierFactory.getDetector(classifierCls), ProcessorFactory.getProcessor(processorCls));
    }

    public Detector(C classifier, P processor) {
        this.classifier = classifier;
        this.processor = processor;
    }

    public abstract Cell detect(List<Cell> cells);

}
