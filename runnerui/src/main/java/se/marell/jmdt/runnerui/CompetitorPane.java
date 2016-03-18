/*
 * Created by Daniel Marell 12-12-29 5:39 PM
 */
package se.marell.jmdt.runnerui;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import se.marell.dcommons.progress.ProgressTrackerAdapter;
import se.marell.jmdt.commons.AbstractJmdtCompetitor;
import se.marell.jmdt.commons.JmdtCompetitorRunResult;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Marell DB Test
 * <p/>
 * EventSuite: Hierarchy writer, hierarchy reader, graph traversal update, partial delete
 * EventCompetitor: A test implementation or a DB technology plugin
 * <p/>
 * Pane with controls and information about a single EventCompetitor test.
 * <p/>
 * Information:
 * <p/>
 * Event suite:         Hierarchy writing
 * Event competitor:    JpaWriter
 * Entity manager:      openjpa-mysql
 * Last result (ms):    330
 * SQL inserts:         12
 * SQL selects:         12
 * <p/>
 * Button Run test
 * Progress bar
 * Button Show SQL
 * Button Show Source
 */

public class CompetitorPane extends BorderPane {
    private final static Logger logger = Logger.getLogger(CompetitorPane.class);

    public interface TestRunner {
        JmdtCompetitorRunResult runTest();
    }

    public static class ProgressAdapter extends ProgressTrackerAdapter {
        private CompetitorPane ecPane;

        public ProgressAdapter(CompetitorPane ecPane) {
            this.ecPane = ecPane;
        }

        @Override
        public void setTotalProgress(final float v) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ecPane.setProgress(v);
                }
            });
        }

        @Override
        public void setProgressLabel(final String text) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    ecPane.setProgressMessage(text);
                }
            });
        }
    }

    private TextField suiteField;
    private TextField competitorField;
    private TextField entityManagerField;
    private TextField lastExecutionTimeField;
    private TextField numSqlInsertsField;
    private TextField numSqlSelectsField;
    private ProgressBar progressBar;
    private TextField progressTextField;
    private List<String> sqlStatements = new ArrayList<>();
    private int numSqlInserts;
    private int numSqlSelects;
    private Button runButton;

    public CompetitorPane(AbstractJmdtCompetitor competitor, String entityManagerName, final TestRunner testRunner) {
        /*
         * Init UI controls
         */
        suiteField = new TextField();
        suiteField.setEditable(false);
        suiteField.setText(competitor.getSuite().getName());
        competitorField = new TextField();
        competitorField.setEditable(false);
        competitorField.setText(competitor.getName());
        entityManagerField = new TextField();
        entityManagerField.setEditable(false);
        entityManagerField.setText(entityManagerName);
        lastExecutionTimeField = new TextField();
        lastExecutionTimeField.setEditable(false);
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(200);
        progressTextField = new TextField();
        numSqlInsertsField = new TextField();
        numSqlInsertsField.setEditable(false);
        numSqlSelectsField = new TextField();
        numSqlSelectsField.setEditable(false);
        runButton = new Button("Run");
        EventHandler<ActionEvent> runButtonHandler = new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                Task<JmdtCompetitorRunResult> runTask = new Task<JmdtCompetitorRunResult>() {
                    @Override
                    protected JmdtCompetitorRunResult call() throws Exception {
                        return testRunner.runTest();
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        try {
                            CompetitorPane.this.setResult(get());
                        } catch (InterruptedException | ExecutionException ignore) {
                        }
                        CompetitorPane.this.setRunDisable(false);
                    }

                    @Override
                    protected void cancelled() {
                        super.cancelled();
                        logger.debug("Run cancelled");
                        CompetitorPane.this.setRunDisable(false);
                    }

                    @Override
                    protected void failed() {
                        super.failed();
                        logger.debug("Run failed:", getException());
                        CompetitorPane.this.setRunDisable(false);
                    }
                };
                CompetitorPane.this.setRunDisable(true);
                new Thread(runTask).start();
            }
        };
        runButton.setOnAction(runButtonHandler);
        RunnerUiUtil.addDropShadow(runButton);

        Button showSqlButton = new Button("Show SQL");
        showSqlButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showSqlAction();
            }
        });
        RunnerUiUtil.addDropShadow(showSqlButton);

        Button showSourceButton = new Button("Show Source");
        showSourceButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showSourceAction();
            }
        });
        RunnerUiUtil.addDropShadow(showSourceButton);

        /*
         * UI layout
         */
        setPadding(new Insets(10, 10, 10, 10));
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        int row = 0;

        gridPane.add(new Text("Suite:"), 0, row);
        gridPane.add(suiteField, 1, row);

        gridPane.add(new Text("Competitor:"), 0, ++row);
        gridPane.add(competitorField, 1, row);

        gridPane.add(new Text("Entity manager:"), 0, ++row);
        gridPane.add(entityManagerField, 1, row);

        gridPane.add(new Text("Execution time (ms):"), 0, ++row);
        gridPane.add(lastExecutionTimeField, 1, row);

        gridPane.add(new Text("SQL inserts:"), 0, ++row);
        gridPane.add(numSqlInsertsField, 1, row);

        gridPane.add(new Text("SQL selects:"), 0, ++row);
        gridPane.add(numSqlSelectsField, 1, row);

        gridPane.add(runButton, 0, ++row);
        gridPane.add(showSqlButton, 1, row);
        gridPane.add(showSourceButton, 1, row);

        gridPane.add(progressTextField, 0, ++row, 2, 1);
        gridPane.add(progressBar, 0, ++row, 2, 1);

        setTop(gridPane);

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.getChildren().addAll(runButton, showSqlButton, showSourceButton);
        setBottom(buttonBox);
    }

    public void setResult(JmdtCompetitorRunResult result) {
        sqlStatements = result.getSqlStatements();
        numSqlInserts = result.getNumSqlInserts();
        numSqlSelects = result.getNumSqlSelects();
        suiteField.setText(result.getCompetitor().getSuite().getName());
        competitorField.setText(result.getCompetitor().getName());
        entityManagerField.setText(result.getEntityManagerName());
        lastExecutionTimeField.setText(String.format("%d", result.getExecTimeMs()));
        numSqlInsertsField.setText(String.format("%d", getNumSqlInserts()));
        numSqlSelectsField.setText(String.format("%d", getNumSqlSelects()));
        progressBar.setProgress(0);
        progressTextField.setText("");
    }

    public String getEntityManagerName() {
        return entityManagerField.getText();
    }

    public String getCompetitorName() {
        return competitorField.getText();
    }

    public void setProgress(double progress) {
        progressBar.setProgress(progress);
    }

    public void setProgressMessage(String text) {
        progressTextField.setText(text);
    }

    public void setRunDisable(boolean disable) {
        runButton.setDisable(disable);
    }

    private void showSqlAction() {
        StringBuilder sb = new StringBuilder();
        int cols = 0;
        for (String s : sqlStatements) {
            if (s.length() > cols) {
                cols = s.length();
            }
            if (sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(s);
        }
        TextArea sqlArea = new TextArea(sb.toString());
//        sqlArea.setPrefRowCount(sqlStatements.size());
//        sqlArea.setPrefColumnCount(cols);
        sqlArea.setPrefRowCount(30);
        sqlArea.setPrefColumnCount(100);
//        ScrollPane scrollPane = new ScrollPane();
//        scrollPane.setContent(sqlArea);
//        scrollPane.setPrefWidth(1000);
//        scrollPane.setPrefHeight(700);
        showDialog(sqlArea, suiteField.getText() + " - " + competitorField.getText());
    }

    private void showDialog(Node node, String title) {
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.show();
        dialogStage.setScene(new Scene(VBoxBuilder.create()
                .children(node)
                .alignment(Pos.CENTER)
                .padding(new Insets(5))
                .build()));
    }

    private void showSourceAction() {
        //Todo
    }

    public int getNumSqlInserts() {
        return numSqlInserts;
    }

    public int getNumSqlSelects() {
        return numSqlSelects;
    }
}
