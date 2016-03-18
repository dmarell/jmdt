/*
 * Created by Daniel Marell 12-12-29 1:12 AM
 */
package se.marell.jmdt.eclipse;

import org.eclipse.persistence.logging.AbstractSessionLog;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.logging.SessionLogEntry;

import java.util.ArrayList;
import java.util.List;

public class CustomSessionLog extends AbstractSessionLog implements SessionLog {
    private static List<String> lines = new ArrayList<String>();

    @Override
    public void log(SessionLogEntry sessionLogEntry) {
        String ns = sessionLogEntry.getNameSpace();
        if (ns != null && ns.equals("sql")) {
            synchronized (lines) {
                String line = sessionLogEntry.getMessage();
                int idx = line.indexOf('\n');
                if (idx != -1) {
                    line = line.substring(0, idx);
                }
                lines.add(line);
            }
        }
    }

    public static void clearSqlLog() {
        synchronized (lines) {
            lines = new ArrayList<String>();
        }
    }

    public static List<String> fetchSqlLog() {
        synchronized (lines) {
            List<String> result = lines;
            lines = new ArrayList<String>();
            return result;
        }
    }
}
