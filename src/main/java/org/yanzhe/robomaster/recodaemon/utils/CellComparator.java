package org.yanzhe.robomaster.recodaemon.utils;

import org.yanzhe.robomaster.recodaemon.net.proto.RpcMessageProto.Cell;

import java.util.Comparator;

public class CellComparator implements Comparator<Cell> {
    @Override
    public int compare(Cell o1, Cell o2) {
        return Long.compare(o1.getSeq().getValue(),o2.getSeq().getValue());
    }
}
