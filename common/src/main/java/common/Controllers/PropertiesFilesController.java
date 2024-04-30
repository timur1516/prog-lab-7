package common.Controllers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PropertiesFilesController {
    public Properties readProperties(String filename) throws IOException {
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(filename);
        properties.load(inputStream);
        return properties;
    }
    public void writeProperties(Properties properties, String filename, String comments) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(filename);
        properties.store(outputStream, comments);
    }
}
