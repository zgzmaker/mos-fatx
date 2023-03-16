package main.core;

/**
 * ICluster
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-16
 **/
public interface ICluster {

    /**
     * 读取一个指定扇区的数据
     *
     * @param clusterId 簇索引，起始索引为0，终止索引为 {@code clusterCount()-1}
     * @return 扇区数据，返回的字节数组长度必须等于{@code sectorSize()}
     */
    byte[] readCluster();

    /**
     * 写一个指定簇
     *
     * @param clusterId 簇索引，起始索引为0，终止索引为 {@code clusterCount()-1}
     * @param offset  簇偏移量，从offset下标开始写入数据
     * @param clusterData 待写入的数据. 长度必须等于{@code clusterSize()}
     */
    void writeCluster(int clusterId, int offset, byte[] clusterData);


    /**
     * 磁盘每个扇区的大小，固定为512字节
     * @return
     */
    default int sectorNum() {
        return 64;
    }

    /**
     * 扇区数量
     *
     * @return
     */
    default int clusterCount() {
        return 2 * 1024 * 1024 * 2 / 64;
    }
}
