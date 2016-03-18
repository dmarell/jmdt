/*
 * Created by Daniel Marell 13-01-17 9:27 AM
 */
package se.marell.jmdt.runnerui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import se.marell.jmdt.commons.JmdtCompetitorRunResult;

public class CompetitorRunResultWrapper {
    private StringProperty execTimeMs = new SimpleStringProperty();
    private JmdtCompetitorRunResult runResult;

    public CompetitorRunResultWrapper(int execTimeMs, JmdtCompetitorRunResult runResult) {
        String s;
        if (execTimeMs == 0) {
            s = "";
        } else {
            s = String.format("%d", execTimeMs);
        }
        this.execTimeMs = new SimpleStringProperty(s);
        this.runResult = runResult;
    }

    public String getExecTimeMs() {
        return execTimeMs.get();
    }

    public void setExecTimeMs(String execTimeMs) {
        this.execTimeMs.set(execTimeMs);
    }

    public StringProperty getExecTimeMsProperty() {
        return execTimeMs;
    }

    public JmdtCompetitorRunResult getRunResult() {
        return runResult;
    }

    public void setRunResult(JmdtCompetitorRunResult runResult) {
        this.runResult = runResult;
    }

    @Override
    public String toString() {
        return "CompetitorRunResultWrapper{" +
                "execTimeMs=" + execTimeMs +
                ", runResult=" + runResult +
                '}';
    }
}
