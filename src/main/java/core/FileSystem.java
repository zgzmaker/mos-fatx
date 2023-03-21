package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * FileSystem
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-16
 **/
public class FileSystem implements IFileSystem{

    private static final int DIR_ENTRY_DATA_OFFSET = 512;
    private static final int DIR_ENTRY_DATA_LENGTH = 512 * 63;

    public static IDisk disk;
    public static FileAllocationTable fileAllocationTable;
    private Cluster bootCluster;

    private Directory rootDirectory;

    private Directory currentDirectory;


    /**
     * 当前路径
     */
    private List<String> curPath = new ArrayList<>();


    public FileSystem(IDisk iDisk) {
        disk = iDisk;
        /* init file system */
        /* init root */
        bootCluster = new Cluster(disk,0, true);
        currentDirectory = new Directory(bootCluster.getData(512, 512 * disk.sectorCount() - 512), bootCluster.getClusterId(), new ArrayList<>());

        /* init Root Directory */
        byte[] rootDirData = bootCluster.getData(DIR_ENTRY_DATA_OFFSET, DIR_ENTRY_DATA_LENGTH);
        rootDirectory = new Directory(rootDirData, bootCluster.getClusterId(), new ArrayList<>());

        /* init file Allocation Table */
        List<Cluster> fatClusterList = new ArrayList<>();
        for(int i = 1; i <= 4; i++) {
            Cluster fatCluster = new Cluster(disk, i, true);
            fatClusterList.add(fatCluster);
        }
        fileAllocationTable = new FileAllocationTable(fatClusterList);
    }

    @Override
    public void newDirectory(String dirPath, String dirName) {
        doNewMosFile(dirPath, dirName, true);
    }

    @Override
    public void newFile(String filePath, String fileName) {
        doNewMosFile(filePath, fileName, false);
    }

    private void doNewMosFile(String path, String fileName, boolean directoryFlag) {
        /* 当前目录文件接入 */
        currentDirectory.addEntry(fileName, directoryFlag);

        List<Integer> parentclusterIds = currentDirectory.getParents();
        if (null == parentclusterIds || 0 == parentclusterIds.size()) {
            /* root dir */
            return;
        }
        List<Integer> clusterIds = new ArrayList<>(parentclusterIds);
        clusterIds.remove(parentclusterIds.size() - 1);
        Directory parentDir = new Directory(parentclusterIds.get(parentclusterIds.size() - 1), clusterIds);

        int index = path.lastIndexOf("/");
        String parentDirName = path.substring(index+1);
        DirectoryEntry entry = parentDir.findEntryByFileName(parentDirName);
        parentDir.incrEntryFileSize(entry, Directory.DIRECTORY_ENTRY_BYTE_SIZE);
    }

    @Override
    public void write2File(String fileName, byte[] data) {
        if (null == data || 0 == data.length) {
            return;
        }
        DirectoryEntry entry = currentDirectory.findEntryByFileName(fileName);
        if (null == entry) {
            throw new IllegalArgumentException("file does not exist");
        }
        MosFile file = new MosFile(entry.getStartingCluster(), entry.getFileSize());
        file.append(data);

        currentDirectory.incrEntryFileSize(entry, data.length);
    }

    @Override
    public Byte[] readFromFile(String fileName) {
        return new Byte[0];
    }

    @Override
    public String currentDirPath() {
        StringBuilder sb = new StringBuilder();
        for (String dirName : curPath) {
            sb.append("/").append(dirName);
        }
        return sb.toString();
    }

   public void moveToPath(String path) {
       String[] dirNameList = path.split("/");
        if (path.startsWith("/")) {
            /* 绝对路径 */
            Directory curDir = currentDirectory;
            List<String> pathNameList = new ArrayList<>();
            for(String dirName : dirNameList) {
                curDir = cdSubDir(curDir, pathNameList, dirName);
            }
            currentDirectory = curDir;
            curPath = pathNameList;
        } else {
            /* 相对路径 */
            Directory curDir = currentDirectory;
            List<String> pathNameList = new ArrayList<>(curPath);

            for(String dirName : dirNameList) {
                if (".".equals(dirName)) {
                    continue;
                } else if ("..".equals(dirName)) {
                    List<Integer> parents = curDir.getParents();
                    if (null == parents || 0 == parents.size()) {
                        throw new IllegalArgumentException("no such path");
                    }

                    List<Integer> clusterIds = new ArrayList<>(parents);
                    clusterIds.remove(parents.size() - 1);
                    curDir = new Directory(parents.get(parents.size() - 1), clusterIds);

                    pathNameList.remove(pathNameList.size() - 1);
                } else {
                    curDir = cdSubDir(curDir, pathNameList, dirName);
                }
            }
            currentDirectory = curDir;
            curPath = pathNameList;
        }
   }

    /**
     * 进入到子目录
     * @param curDir
     * @param pathNameList
     * @param dirName
     * @return
     */
    private Directory cdSubDir(Directory curDir, List<String> pathNameList, String dirName) {
        DirectoryEntry entryByFileName = curDir.findEntryByFileName(dirName);
        if (null == entryByFileName) {
            throw new IllegalArgumentException("no such directory");
        }
        List<Integer> parents = curDir.getParents();
        List<Integer> clusterIds = new ArrayList<>(parents);
        clusterIds.add(entryByFileName.getStartingCluster());
        curDir = new Directory(entryByFileName.getStartingCluster(), clusterIds);

        pathNameList.add(dirName);
        return curDir;
    }
}
