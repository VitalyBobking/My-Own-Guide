package diploma.edu.zp.guide_my_own.utils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by Val on 2/16/2017.
 */

public class DeleteFileByPath {
    public static void deleteFile(String path) {
        try {
            File file = new File(path);
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
