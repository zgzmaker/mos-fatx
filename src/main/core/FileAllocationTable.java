package main.core;

import java.util.List;

/**
 * FileAllcationTable
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-16
 **/
public class FileAllocationTable {

    private List<Cluster> clusters;

    public FileAllocationTable(List<Cluster> clusterList) {
        this.clusters = clusterList;
    }

}
