/*
 * Created by Daniel Marell 13-02-15 6:32 PM
 */
package se.marell.jmdt.commons;

import java.io.*;

public class JmdtUtil {
    private JmdtUtil() {
    }

    public static String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        return stringBuilder.toString();
    }

    public static void writeFile(File file, String text) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(text);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
}
