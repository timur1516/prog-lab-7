package common.Controllers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class to work with properties files
 */
public class PropertiesFilesController {
    /**
     * Method to read data from properties fie
     * @param filename Name of file to read
     * @return Properties object which was read
     * @throws IOException If any I\O error occurred
     */
    public Properties readProperties(String filename) throws IOException {
        Properties properties = new Properties();
        FileInputStream inputStream = new FileInputStream(filename);
        properties.load(inputStream);
        return properties;
    }

    /**
     * Method to white data to properties file
     * @param properties Data to write
     * @param filename Name of file
     * @param comments Comments to mention in file
     * @throws IOException If any I\O error occurred while writing to file
     */
    public void writeProperties(Properties properties, String filename, String comments) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(filename);
        properties.store(outputStream, comments);
    }
}
