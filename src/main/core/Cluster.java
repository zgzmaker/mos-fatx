package main.core;

import main.exception.MosFileSystemException;

import java.io.FileNotFoundException;
import java.util.Arrays;

/**
 * Cluster
 * File system storage unit
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-16
 **/
public class Cluster implements ICluster{

    private IDisk disk;

    private int clusterId;

    private byte[] data;

    public Cluster(IDisk disk, int clusterId, boolean loadData) {
        this.disk = disk;
        this.clusterId = clusterId;
        this.data = new byte[sectorNum() * disk.secotrSize()];
        if (loadData) {
            readCluster();
        }
    }

    @Override
    public byte[] readCluster() {
        if (0 < clusterId || clusterId >= clusterCount()) {
            throw new MosFileSystemException("illegal sectorIdx. sectorIdx must in [0, " + clusterCount() + ")");
        }

        for (int i = 0; i < sectorNum(); i++) {
            int sectorIdx = clusterId * sectorNum() + i;
            byte[] data = disk.readSector(sectorIdx);
            for(int j = 0 ; j < data.length; j++) {
                this.data[i * disk.secotrSize() + j] = data[j];
            }
        }

        return this.data;
    }

    public byte[] getData(int offset, int len) {
        if (null == this.data) {
            readCluster();
        }
        if (0 > offset || offset + len > this.data.length) {
            throw new IllegalArgumentException("illegal parameter.");
        }
        return Arrays.copyOfRange(this.data, offset, offset + len);
    }

    @Override
    public void writeCluster(int clusterId, int offset, byte[] clusterData) {
        if (0 > clusterId || clusterId >= clusterCount()) {
            throw new IllegalArgumentException("illegal clusterId. clusterId must in [0, " + clusterCount() + ")");
        }

        if (null == clusterData || 0 == clusterData.length) {
            return;
        }

        int dataLen = clusterData.length;
        if (offset + dataLen > disk.secotrSize() * sectorNum()) {
            throw new MosFileSystemException("data too large");
        }

        /* start to write data to sectors */
        /* 簇内扇区下标 */
        int sectorIndexInCluster = offset / disk.secotrSize();
        int dataOffset = 0;
        while (dataOffset < dataLen && sectorIndexInCluster < sectorNum()) {
            int sectorIdx = clusterId * sectorNum() + sectorIndexInCluster;
            int startOffset = offset - sectorIdx * disk.secotrSize();
            int len = Math.min(disk.secotrSize() - startOffset, dataLen - dataOffset);
            disk.writeSector(sectorIdx, startOffset, clusterData, dataOffset, len);

            dataOffset += len;
            sectorIndexInCluster++;
            offset += len;
        }
    }

    public static void main(String arg[]) throws FileNotFoundException {
        IDisk disk = new FileDisk();
        ICluster cluster = new Cluster(disk, 0, false);
        byte[] data = new byte[612];
        for (int i = 0; i < 612; i++) {
            data[i] = 'b';
        }
        cluster.writeCluster(0, 2, data);
        System.out.println(data);
    }
}
