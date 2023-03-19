package core;

import main.java.ByteUtil;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * FileAllcationTable
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-16
 **/
public class FileAllocationTable {

    private static final int FAT_ENTRY_BYTE_SIZE = 2;

    private List<Cluster> clusters;

    private int fatEntrySizePerCluster;

    private int fatEntrySize;

    public FileAllocationTable(List<Cluster> clusterList) {
        if (null == clusterList || clusterList.size() == 0) {
            throw new IllegalArgumentException("clusterList should not be empty");
        }
        this.clusters = clusterList;
        this.fatEntrySizePerCluster = clusterList.get(0).getDataLen() / FAT_ENTRY_BYTE_SIZE;
        this.fatEntrySize = clusterList.size() * this.fatEntrySizePerCluster;
    }

    public int getNextEmptyCluster() {
        for (int i = 0; i < fatEntrySize; i++) {
            int val = getFatEntryValue(i);
            if (0 == val) {
                return i;
            }
        }
        return -1;
    }

    public int getFatEntryValue(int fatEntryId) {
        byte[] data = clusters.get(fatEntryId / this.fatEntrySizePerCluster)
                .getData(fatEntryId % this.fatEntrySizePerCluster * FAT_ENTRY_BYTE_SIZE, FAT_ENTRY_BYTE_SIZE);
        return ByteUtil.byte2Short(data);
    }

    /**
     * 重置各个Cluster的状态
     */
    public void reset() {
        byte[] data = new byte[this.fatEntrySizePerCluster * FAT_ENTRY_BYTE_SIZE];
        for (int i = 0; i < data.length; i++) {
            data[i] = 0x00;
        }

        clusters.get(1).writeCluster(data);
        clusters.get(2).writeCluster(data);
        clusters.get(3).writeCluster(data);


        data[0] = (byte) 0xff;
        data[1] = (byte) 0xf8;

        data[2] = (byte) 0xff;
        data[3] = (byte) 0xff;

        data[4] = (byte) 0xff;
        data[5] = (byte) 0xff;

        data[6] = (byte) 0xff;
        data[7] = (byte) 0xff;

        data[8] = (byte) 0xff;
        data[9] = (byte) 0xff;

        clusters.get(0).writeCluster(data);
    }

    public static void main(String arg[]) throws FileNotFoundException {
        IDisk disk = new FileDisk();
        Cluster cluster1 = new Cluster(disk, 1, false);
        Cluster cluster2 = new Cluster(disk, 2, false);
        Cluster cluster3 = new Cluster(disk, 3, false);
        Cluster cluster4 = new Cluster(disk, 4, false);
        List<Cluster> clusters = new ArrayList<>();
        clusters.add(cluster1);
        clusters.add(cluster2);
        clusters.add(cluster3);
        clusters.add(cluster4);

        FileAllocationTable fat = new FileAllocationTable(clusters);
        fat.reset();
        byte[] data = new byte[612];
        for (int i = 0; i < 612; i++) {
            data[i] = 'c';
        }

        System.out.println(data);
    }

}
