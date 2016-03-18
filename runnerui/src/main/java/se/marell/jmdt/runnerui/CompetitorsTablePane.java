/*
 * Created by Daniel Marell 13-01-05 1:03 PM
 */
package se.marell.jmdt.runnerui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.apache.log4j.Logger;
import se.marell.dcommons.progress.EmptyProgressTracker;
import se.marell.dcommons.progress.ProgressTracker;
import se.marell.jmdt.AbstractEntityManagerWrapper;
import se.marell.jmdt.EntityManagerWrapper;
import se.marell.jmdt.commons.JmdtCompetitor;
import se.marell.jmdt.commons.JmdtCompetitorRunResult;

import java.util.ArrayList;
import java.util.List;


/**
 * openjpa                 hibernate               eclipse
 * mysql   db2     h2      mysql   db2     h2      mysql   db2     h2
 * JpaWriter        123     123     123     123     123     123     123     123     123
 * JdbcWriter       123     123     123     123     123     123     123     123     123
 * JdbcBatchWriter  123     123     123     123     123     123     123     123     123
 * DbUtilsWriter    123     123     123     123     123     123     123     123     123
 * MarellWriter     123     123     123     123     123     123     123     123     123
 * SpringWriter     123     123     123     123     123     123     123     123     123
 * <p/>
 * Button "Start"
 * Button "Cancel"
 * Progress bar
 */
public class CompetitorsTablePane extends BorderPane {
    private final static Logger logger = Logger.getLogger(CompetitorsTablePane.class);

    public interface TestRunner {
        JmdtCompetitorRunResult runTest(JmdtCompetitor c, EntityManagerWrapper emp, ProgressTracker pt);
    }

    private List<EntityManagerWrapper> entityManagerWrappers;
    private CompetitorTable table;
    private ObservableList<CompetitorResult> tableData;
    private Button startButton;
    private Button cancelButton;
    private Task<Void> runTask;

    public CompetitorsTablePane(final TestRunner testRunner, List<EntityManagerWrapper> entityManagerWrappers,
                                final List<? extends JmdtCompetitor> competitors,
                                CompetitorTable.SelectionListener resultsSelectionListener) {
        this.entityManagerWrappers = entityManagerWrappers;
        tableData = createData(competitors);
        table = new CompetitorTable(tableData, resultsSelectionListener);

        startButton = new Button("Run");
        EventHandler<ActionEvent> startButtonHandler = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                runTask = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        for (JmdtCompetitor c : competitors) {
                            for (EntityManagerWrapper emp : CompetitorsTablePane.this.entityManagerWrappers) {
                                JmdtCompetitorRunResult rr = testRunner.runTest(c, emp, new EmptyProgressTracker()/*todo*/);
                                setCompetitorRunResult(rr);
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        updateEnablestate();
                    }

                    @Override
                    protected void cancelled() {
                        super.cancelled();
                        logger.debug("Run cancelled");
                        updateEnablestate();
                    }

                    @Override
                    protected void failed() {
                        super.failed();
                        logger.debug("Run failed:", getException());
                        updateEnablestate();
                    }
                };
                new Thread(runTask).start();
                updateEnablestate();
            }
        };
        startButton.setOnAction(startButtonHandler);
        RunnerUiUtil.addDropShadow(startButton);

        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //todo implement
            }
        });
        RunnerUiUtil.addDropShadow(cancelButton);

        updateEnablestate();

        // Layout
        setCenter(table);
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(10, 10, 10, 10));
        hbox.setSpacing(10);
        hbox.getChildren().addAll(startButton, cancelButton);
        setBottom(hbox);
    }

    private void updateEnablestate() {
        startButton.setDisable(runTask != null && runTask.isRunning());
        cancelButton.setDisable(runTask == null || !runTask.isRunning());
    }

    public void setCompetitorRunResult(JmdtCompetitorRunResult result) {
        for (CompetitorResult cr : tableData) {
            if (cr.getCompetitor().equals(result.getCompetitor().getName())) {
                cr.setResult(result);
                return;
            }
        }
    }

    private ObservableList<CompetitorResult> createData(List<? extends JmdtCompetitor> competitors) {
        ObservableList<CompetitorResult> result = FXCollections.observableArrayList();
        for (JmdtCompetitor c : competitors) {
            CompetitorResult cr = new CompetitorResult(c.getName());
            for (EntityManagerWrapper emp : entityManagerWrappers) {
                JmdtCompetitorRunResult rr = new JmdtCompetitorRunResult(
                        c,
                        0, // Not ran yet
                        emp.getCaption(),
                        emp.getJpaImplementation().name(),
                        emp.getDbType().name(),
                        new ArrayList<String>(),
                        0,
                        0,
                        "");
                cr.setResult(rr);
            }
            result.add(cr);
        }
        return result;
    }
}
