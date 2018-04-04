package org.yanzhe.robomaster.recodaemon.net.detector;

import com.google.protobuf.Any;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yanzhe.robomaster.recodaemon.net.proto.TargetCellsProto;

import java.util.List;

public abstract class AbstractCellsDetector implements Detector {
    protected static Logger logger = LogManager.getLogger(AbstractCellsDetector.class);

    @Override
    public Any detect(Any body) throws Exception {

        TargetCellsProto.TargetCells targetCells = body.unpack(TargetCellsProto.TargetCells.class);
        List<TargetCellsProto.Cell> cells = targetCells.getCellsList();

        long t1 = System.currentTimeMillis();
        TargetCellsProto.Cell resultCell = _detect(cells);
//    logger.debug(resultCell);
        long t2 = System.currentTimeMillis();
        logger.debug("Batch size = {}, Time used = {} ms\n", cells.size(), t2 - t1);
        Any result = Any.pack(resultCell);
//    logger.debug(result);


        return result;
    }

    protected abstract TargetCellsProto.Cell _detect(List<TargetCellsProto.Cell> cells);
}
