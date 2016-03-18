/*
 * Created by Daniel Marell 12-12-25 1:38 PM
 */
package se.marell.jmdt.commons;

import org.apache.log4j.Logger;

public abstract class AbstractJmdtCompetitor implements JmdtCompetitor {
    protected final Logger logger = Logger.getLogger(getClass());

    private JmdtSuite suite;

    private long reportIntervalStart;
    private int loggingInterval;
    private String loggingActivity;
    private int reportCount;
    private long startTime;
    private long stopTime;

    protected AbstractJmdtCompetitor(JmdtSuite suite) {
        this.suite = suite;
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public JmdtSuite getSuite() {
        return suite;
    }

    public int getMeasuredTime() {
        return (int) (stopTime - startTime);
    }

    public void startMeasureTime() {
        startTime = System.currentTimeMillis();
    }

    public void stopMeasureTime() {
        stopTime = System.currentTimeMillis();
    }

    protected void startIntervalLogging(int loggingInterval, String loggingActivity) {
        reportCount = 0;
        reportIntervalStart = System.currentTimeMillis();
        this.loggingActivity = loggingActivity;
        this.loggingInterval = loggingInterval;
        logger.debug(loggingActivity + ": Started");
    }

    protected void intervalLogging() {
        ++reportCount;
        if (System.currentTimeMillis() - reportIntervalStart > loggingInterval) {
            logger.debug(loggingActivity + ": #" + reportCount);
            reportIntervalStart = System.currentTimeMillis();
        }
    }
}
