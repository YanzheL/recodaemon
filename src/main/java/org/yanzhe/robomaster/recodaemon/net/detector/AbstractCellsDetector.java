package org.yanzhe.robomaster.recodaemon.net.detector;

import com.google.protobuf.Any;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yanzhe.robomaster.recodaemon.net.proto.RpcMessageProto.Cell;
import org.yanzhe.robomaster.recodaemon.net.proto.RpcMessageProto.RepeatedData;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCellsDetector implements Detector {
    protected static Logger logger = LogManager.getLogger(AbstractCellsDetector.class);

    @Override
    public Any detect(Any body) throws Exception {

        RepeatedData targetCells = body.unpack(RepeatedData.class);
        List<Cell> cells = new ArrayList<>();
        for (Any o : targetCells.getItemsList()) {
            cells.add(o.unpack(Cell.class));
        }

        long t1 = System.currentTimeMillis();
        Cell resultCell = _detect(cells);
//    logger.debug(resultCell);
        long t2 = System.currentTimeMillis();
        logger.debug("Batch size = {}, Time used = {} ms\n", cells.size(), t2 - t1);
        Any result = Any.pack(resultCell);
//    logger.debug(result);


        return result;
    }

    protected abstract Cell _detect(List<Cell> cells);
}
