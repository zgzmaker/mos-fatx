package core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Directory
 *
 * 目录
 *
 * @author zhuganzheng001
 * @version: 1.0.0
 * @date 2023-3-16
 **/
public class Directory {

    private static final int DIRECTORY_ENTRY_BYTE_SIZE = 32;
    /**
     * 目录下的条目
     */
    private List<DirectoryEntry> entries;

    public Directory(byte[] data) {
        /* data to entries */
        if (null == data || 0 == data.length) {
            entries = new ArrayList<>();
            return;
        }

        for(int i = 0; i < data.length; i += DIRECTORY_ENTRY_BYTE_SIZE) {
            DirectoryEntry entry = new DirectoryEntry(
                    Arrays.copyOfRange(data, i, i + DIRECTORY_ENTRY_BYTE_SIZE));
            entries.add(entry);
        }
    }

    public String output() {
        if (null == this.entries || 0 == this.entries.size()) {
            return "";
        }

        StringBuilder sb = new StringBuilder(this.entries.get(0).getFormatDisplay());
        for(int i = 1; i < this.entries.size(); i++) {
            sb.append("\n")
                    .append(this.entries.get(i).getFormatDisplay());
        }
        return sb.toString();
    }
}
