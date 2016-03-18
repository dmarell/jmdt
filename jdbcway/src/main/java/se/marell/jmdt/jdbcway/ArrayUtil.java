/*
 * Created by Daniel Marell 12-11-14 2:45 PM
 */
package se.marell.jmdt.jdbcway;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ArrayUtil {
    private ArrayUtil() {
    }

    public static List<String[]> divideArray(String[] source, int chunksize) {
        List<String[]> result = new ArrayList<String[]>();
        int fullLengthChunks = (int) (source.length / (double) chunksize);
        int start = 0;
        for (int i = 0; i < fullLengthChunks; ++i) {
            String[] chunk = Arrays.copyOfRange(source, start, start + chunksize);
            start += chunksize;
            result.add(chunk);
        }
        if (start < source.length) {
            String[] chunk = Arrays.copyOfRange(source, start, source.length);
            result.add(chunk);
        }
        return result;
    }
}
