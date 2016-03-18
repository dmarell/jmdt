/*
 * Created by Daniel Marell 13-01-03 7:41 PM
 */
package se.marell.jmdt.runnerui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import se.marell.jmdt.commons.JmdtCompetitorRunResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

public class CompetitorResult {
    private StringProperty competitor;
    private ObservableMap<String/*entityManagerName*/, CompetitorRunResultWrapper> resultMap = FXCollections.observableMap(new TreeMap<String, CompetitorRunResultWrapper>());

    public CompetitorResult(String competitor) {
        this.competitor = new SimpleStringProperty(competitor);
    }

    public String getCompetitor() {
        return competitor.get();
    }

    public void setCompetitor(String competitor) {
        this.competitor.set(competitor);
    }

    public StringProperty getCompetitorProperty() {
        return competitor;
    }

    public ObservableMap<String, CompetitorRunResultWrapper> getResultMapProperty() {
        return resultMap;
    }

    public Collection<JmdtCompetitorRunResult> getResults() {
        Collection<JmdtCompetitorRunResult> result = new ArrayList<>();
        for (CompetitorRunResultWrapper w : resultMap.values()) {
            result.add(w.getRunResult());
        }
        return result;
    }

    public CompetitorRunResultWrapper getResult(String entityManagerName) {
        return resultMap.get(entityManagerName);
    }

    public void setResult(JmdtCompetitorRunResult rr) {
        CompetitorRunResultWrapper rrw = resultMap.get(rr.getEntityManagerName());
        if (rrw != null) {
            if (rr.getExecTimeMs() == 0) {
                rrw.setExecTimeMs("");
            } else {
                rrw.setExecTimeMs(String.format("%d ms", rr.getExecTimeMs()));
            }
            rrw.setRunResult(rr);
        } else {
            resultMap.put(rr.getEntityManagerName(), new CompetitorRunResultWrapper(rr.getExecTimeMs(), rr));
        }
    }
}
