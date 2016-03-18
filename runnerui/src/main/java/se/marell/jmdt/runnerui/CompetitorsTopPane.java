/*
 * Created by Daniel Marell 13-02-12 8:40 AM
 */
package se.marell.jmdt.runnerui;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import se.marell.jmdt.EntityManagerWrapper;
import se.marell.jmdt.JmdtRunner;
import se.marell.jmdt.commons.JmdtCompetitor;

import java.util.List;

public abstract class CompetitorsTopPane extends BorderPane {
    protected CompetitorsTablePane tablePane;
    protected CompetitorPane competitorPane;
    protected HBox bottomPane = new HBox();

    protected CompetitorsTopPane() {
        setBottom(bottomPane);
    }

    protected abstract CompetitorsTablePane.TestRunner createTestRunner(JmdtRunner runner);

    protected abstract CompetitorPane.TestRunner getCompetitorPaneTestRunner(final JmdtRunner runner,
                                                                             final EntityManagerWrapper emp,
                                                                             final JmdtCompetitor writer,
                                                                             final CompetitorRunResultWrapper cr);

    protected EntityManagerWrapper findAbstractEntityManagerWrapper(List<EntityManagerWrapper> entityManagerWrappers, String entityManagerName) {
        for (EntityManagerWrapper w : entityManagerWrappers) {
            if (w.getCaption().equals(entityManagerName)) {
                return w;
            }
        }
        return null;
    }
}
