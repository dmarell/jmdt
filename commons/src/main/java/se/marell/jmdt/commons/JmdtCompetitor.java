/*
 * Created by Daniel Marell 13-01-03 8:01 PM
 */
package se.marell.jmdt.commons;

public interface JmdtCompetitor {
    String getName();

    JmdtSuite getSuite();

    int getMeasuredTime();

    void startMeasureTime();

    void stopMeasureTime();
}
