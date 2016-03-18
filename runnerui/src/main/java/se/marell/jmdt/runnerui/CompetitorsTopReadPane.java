/*
 * Created by Daniel Marell 13-02-13 8:10 AM
 */
package se.marell.jmdt.runnerui;

import se.marell.dcommons.progress.ProgressTracker;
import se.marell.jmdt.EntityManagerWrapper;
import se.marell.jmdt.JmdtRunner;
import se.marell.jmdt.commons.AbstractJpaHierarchyReader;
import se.marell.jmdt.commons.JmdtCompetitor;
import se.marell.jmdt.commons.JmdtCompetitorRunResult;

import java.util.List;

public class CompetitorsTopReadPane extends CompetitorsTopPane {
    private static final String customerNoPattern = "%0";

    public CompetitorsTopReadPane(final JmdtRunner runner,
                                  final List<EntityManagerWrapper> entityManagerWrappers,
                                  List<? extends JmdtCompetitor> competitors) {
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
                            if (competitor instanceof AbstractJpaHierarchyReader) {
                                final AbstractJpaHierarchyReader reader = (AbstractJpaHierarchyReader) competitor;
                                final EntityManagerWrapper emp = findAbstractEntityManagerWrapper(entityManagerWrappers, cr.getRunResult().getEntityManagerName());

                                competitorPane = new CompetitorPane(reader, emp.getCaption(), getCompetitorPaneTestRunner(runner, emp, reader, cr));
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
                final AbstractJpaHierarchyReader reader = (AbstractJpaHierarchyReader) competitor;
                runner.runJpaHierarchyReadTest(customerNoPattern, emp, reader, new CompetitorPane.ProgressAdapter(competitorPane));
                List<String> sqlLog = emp.fetchSqlLog();
                JmdtCompetitorRunResult result = new JmdtCompetitorRunResult(
                        reader,
                        reader.getMeasuredTime(),
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
                AbstractJpaHierarchyReader w = (AbstractJpaHierarchyReader) c;
                runner.runJpaHierarchyReadTest(customerNoPattern, emp, w, pt);
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
