/*
 * Created by Daniel Marell 13-02-13 8:09 AM
 */
package se.marell.jmdt.runnerui;

import se.marell.dcommons.progress.ProgressTracker;
import se.marell.jmdt.EntityManagerWrapper;
import se.marell.jmdt.JmdtRunner;
import se.marell.jmdt.commons.AbstractJpaHierarchyWriter;
import se.marell.jmdt.commons.JmdtCompetitor;
import se.marell.jmdt.commons.JmdtCompetitorRunResult;

import java.util.List;

public class CompetitorsTopWritePane extends CompetitorsTopPane {

    public CompetitorsTopWritePane(final JmdtRunner runner,
                                   List<EntityManagerWrapper> entityManagerWrappers,
                                   List<? extends AbstractJpaHierarchyWriter> competitors) {
        tablePane = new CompetitorsTablePane(createTestRunner(runner), entityManagerWrappers, competitors,
                new CompetitorTable.SelectionListener() {
                    @Override
                    public void resultsSelected(List<CompetitorRunResultWrapper> results) {
                        bottomPane.getChildren().clear();
                        for (final CompetitorRunResultWrapper cr : results) {
                            if (cr.getRunResult() == null) {
                                continue;
                            }
                            JmdtCompetitor competitor = cr.getRunResult().getCompetitor();
                            if (competitor instanceof AbstractJpaHierarchyWriter) {
                                final AbstractJpaHierarchyWriter writer = (AbstractJpaHierarchyWriter) competitor;
                                final EntityManagerWrapper emp = findAbstractEntityManagerWrapper(runner.getEntityManagerWrappers(), cr.getRunResult().getEntityManagerName());

                                competitorPane = new CompetitorPane(writer, emp.getCaption(), getCompetitorPaneTestRunner(runner, emp, writer, cr));
                                competitorPane.setResult(cr.getRunResult());
                                bottomPane.getChildren().add(competitorPane);
                            }
                        }
                    }

                });
        setCenter(tablePane);
    }

    @Override
    protected CompetitorPane.TestRunner getCompetitorPaneTestRunner(final JmdtRunner runner,
                                                                    final EntityManagerWrapper emp,
                                                                    final JmdtCompetitor competitor,
                                                                    final CompetitorRunResultWrapper cr) {
        return new CompetitorPane.TestRunner() {
            @Override
            public JmdtCompetitorRunResult runTest() {
                final AbstractJpaHierarchyWriter writer = (AbstractJpaHierarchyWriter) competitor;
                runner.runJpaHierarchyWriteTest(emp, writer, new CompetitorPane.ProgressAdapter(competitorPane));
                List<String> sqlLog = emp.fetchSqlLog();
                JmdtCompetitorRunResult result = new JmdtCompetitorRunResult(
                        writer,
                        writer.getMeasuredTime(),
                        emp.getCaption(),
                        emp.getJpaImplementation().name(),
                        cr.getRunResult().getDbName(),
                        sqlLog,
                        emp.numInsertsInSqlLog(sqlLog),
                        emp.numSelectsInSqlLog(sqlLog),
                        "");
                tablePane.setCompetitorRunResult(result);
                return result;
            }
        };
    }

    @Override
    protected CompetitorsTablePane.TestRunner createTestRunner(final JmdtRunner runner) {
        return new CompetitorsTablePane.TestRunner() {
            @Override
            public JmdtCompetitorRunResult runTest(JmdtCompetitor c, EntityManagerWrapper emp, ProgressTracker pt) {
                AbstractJpaHierarchyWriter w = (AbstractJpaHierarchyWriter) c;
                runner.runJpaHierarchyWriteTest(emp, w, pt);
                List<String> sqlLog = emp.fetchSqlLog();
                JmdtCompetitorRunResult rr = new JmdtCompetitorRunResult(c, w.getMeasuredTime(), emp.getCaption(),
                        emp.getJpaImplementation().name(), emp.getDbType().name(), sqlLog,
                        emp.numInsertsInSqlLog(sqlLog),
                        emp.numSelectsInSqlLog(sqlLog),
                        "");
                if (competitorPane != null) {
                    if (competitorPane.getEntityManagerName().equals(rr.getEntityManagerName()) &&
                            competitorPane.getCompetitorName().equals(rr.getCompetitor().getName())) {
                        competitorPane.setResult(rr);
                    }
                }
                return rr;
            }
        };
    }
}
