package core;

/**
 * IDisk
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-15
 **/
public interface IDisk {

    /**
     * 读取一个指定扇区的数据
     *
     * @param sectorIdx 扇区索引，起始索引为0，终止索引为 {@code sectorCount()-1}
     * @return 扇区数据，返回的字节数组长度必须等于{@code sectorSize()}
     */
    byte[] readSector(int sectorIdx);

    /**
     * 读取一个指定扇区的数据
     *
     * @param sectorIdx 扇区索引，起始索引为0，终止索引为 {@code sectorCount()-1}
     * @param offset 扇区偏移量，从索引为offset时开始读取。
     * @param maxLen  最大读取字节数，当maxLen > {@code sectorCount()-1} 时，读取整个扇区信息
     * @return 扇区数据，返回的字节数组长度必须等于{@code sectorSize()}
     */
    byte[] readSector(int sectorIdx, int offset, int maxLen);

    /**
     * 写一个指定扇区
     *
     * @param sectorIdx 扇区索引，起始索引为0，终止索引为 {@code sectorCount()-1}
     * @param sectorData 待写入的数据. 长度必须等于{@code sectorSize()}
     */
    void writeSector(int sectorIdx, byte[] sectorData);

    /**
     * 写一个指定扇区
     *
     * @param sectorIdx 扇区索引，起始索引为0，终止索引为 {@code sectorCount()-1}
     * @param sectorOffset 扇区偏移量，从索引为offset时开始写入。
     * @param sectorData 待写入的数据. 长度必须等于{@code sectorSize()}
     * @param dataOffset 待写入数据偏移量
     * @param len 待写入的数据长度，len < 0时，取sectorData数据长度
     */
    void writeSector(int sectorIdx, int sectorOffset, byte[] sectorData, int dataOffset, int len);

    /**
     * 磁盘每个扇区的大小，固定为512字节
     * @return
     */
    default int secotrSize() {
        return 512;
    }

    /**
     * 扇区数量
     *
     * @return
     */
    default int sectorCount() {
        return 2 * 1024 * 1024 * 2;
    }

}
