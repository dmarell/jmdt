/*
 * Created by Daniel Marell 13-01-03 7:59 PM
 */
package se.marell.jmdt.runnerui;

import se.marell.jmdt.commons.JmdtCompetitorRunResult;

public class EntityManagerResult {
    private String entityManagerName;
    private JmdtCompetitorRunResult result;

    public EntityManagerResult(String entityManagerName, JmdtCompetitorRunResult result) {
        this.entityManagerName = entityManagerName;
        this.result = result;
    }

    public String getEntityManagerName() {
        return entityManagerName;
    }

    public JmdtCompetitorRunResult getResult() {
        return result;
    }

    public void setResult(JmdtCompetitorRunResult result) {
        this.result = result;
    }
}
