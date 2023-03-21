package core;

/**
 * IFileSystem
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-16
 **/
public interface IFileSystem {

    /**
     * 新建目录
     * @param dirPath 目录路径
     * @param dirName 目录名称
     */
    void newDirectory(String dirPath, String dirName);

    /**
     * 新建文件
     * @param filePath 文件路径
     * @param fileName 文件名称
     */
    void newFile(String filePath, String fileName);

    /**
     * 写文件
     * @param fileName 文件全路径
     * @param data
     */
    void write2File(String fileName, byte[] data);

    /**
     * 读取文件
     * @param fileName 文件全路径
     * @return
     */
    Byte[] readFromFile(String fileName);

    /**
     * 当前路径
     *
     * @return
     */
    String currentDirPath();
}
