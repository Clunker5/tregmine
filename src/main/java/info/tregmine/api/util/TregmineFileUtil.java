package info.tregmine.api.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ericrabil on 3/27/17.
 */
public class TregmineFileUtil {
    public static File fileFromInputStream(InputStream stream) throws IOException {
        File tempFile = File.createTempFile(new RandomString(6).nextString(), "temp");
        tempFile.deleteOnExit();
        FileOutputStream out = new FileOutputStream(tempFile);
        IOUtils.copy(stream, out);
        return tempFile;
    }
}
