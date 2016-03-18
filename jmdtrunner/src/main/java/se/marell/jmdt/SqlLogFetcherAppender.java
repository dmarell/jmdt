/*
 * Created by Daniel Marell 12-12-29 1:48 AM
 */
package se.marell.jmdt;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

import java.util.ArrayList;
import java.util.List;

public class SqlLogFetcherAppender implements Appender {
    private String name;
    private static List<String> lines = new ArrayList<String>();

    @Override
    public void addFilter(Filter newFilter) {
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    @Override
    public void clearFilters() {
    }

    @Override
    public void close() {
    }

    @Override
    public void doAppend(LoggingEvent event) {
        synchronized (lines) {
            lines.add(event.getMessage().toString());
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return null;
    }

    @Override
    public void setLayout(Layout layout) {
    }

    @Override
    public Layout getLayout() {
        return null;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean requiresLayout() {
        return false;
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
