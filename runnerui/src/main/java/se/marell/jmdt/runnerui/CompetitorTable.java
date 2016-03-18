/*
 * Created by Daniel Marell 13-01-03 3:55 PM
 */
package se.marell.jmdt.runnerui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import se.marell.jmdt.commons.JmdtCompetitorRunResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * openjpa                 hibernate               eclipse
 * mysql   db2     h2      mysql   db2     h2      mysql   db2     h2
 * JpaWriter        123     123     123     123     123     123     123     123     123
 * JdbcWriter       123     123     123     123     123     123     123     123     123
 * JdbcBatchWriter  123     123     123     123     123     123     123     123     123
 * DbUtilsWriter    123     123     123     123     123     123     123     123     123
 * MarellWriter     123     123     123     123     123     123     123     123     123
 * SpringWriter     123     123     123     123     123     123     123     123     123
 */
public class CompetitorTable extends TableView<CompetitorResult> {
    public interface SelectionListener {
        void resultsSelected(List<CompetitorRunResultWrapper> runResult);
    }

    public CompetitorTable(List<CompetitorResult> data, final SelectionListener resultsSelectionListener) {
        TableColumn<CompetitorResult, String> competitorNameCol = new TableColumn<>("Competitor");
        competitorNameCol.setMinWidth(100);
        {
            Callback<TableColumn.CellDataFeatures<CompetitorResult, String>, ObservableValue<String>> factory =
                    new Callback<TableColumn.CellDataFeatures<CompetitorResult, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<CompetitorResult, String> p) {
                            return new SimpleStringProperty(p.getValue().getCompetitor());
                        }
                    };
            competitorNameCol.setCellValueFactory(factory);
        }
        getColumns().add(competitorNameCol);

        Set<String/*emName*/> emSet = createEntityManagerSet(data);

        for (String emName : emSet) {
            TableColumn<CompetitorResult, String> col = new TableColumn<>(breakNameToMultiLine(emName));
            final String finalEmName = emName;
            Callback<TableColumn.CellDataFeatures<CompetitorResult, String>, ObservableValue<String>> factory =
                    new Callback<TableColumn.CellDataFeatures<CompetitorResult, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<CompetitorResult, String> p) {
                            CompetitorRunResultWrapper localRr = p.getValue().getResult(finalEmName);
                            if (localRr != null) {
                                return localRr.getExecTimeMsProperty();
                            }
                            return null;
                        }
                    };
            col.setCellValueFactory(factory);
            col.setMinWidth(70);
            getColumns().add(col);
        }

        getSelectionModel().setCellSelectionEnabled(true);
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        getSelectionModel().getSelectedCells().addListener(new ListChangeListener<TablePosition>() {
            @Override
            public void onChanged(Change<? extends TablePosition> change) {
                List<? extends TablePosition> list = change.getList();
                List<CompetitorRunResultWrapper> result = new ArrayList<>();
                for (TablePosition p : list) {
                    CompetitorRunResultWrapper rrw = getRunResultWrapper(p.getRow(), p.getColumn());
                    if (rrw != null) {
                        result.add(rrw);
                    }
                }
                resultsSelectionListener.resultsSelected(result);
            }
        });

        setItems(FXCollections.observableList(data));
    }

    private CompetitorRunResultWrapper getRunResultWrapper(int row, int column) {
        int r = 0;
        for (CompetitorResult cr : getItems()) {
            if (r++ == row) {
                String entityManagerName = findEntityManagerName(cr, column);
                if (entityManagerName == null) {
                    return null;
                }
                return cr.getResult(entityManagerName);
            }
        }
        return null;
    }

    private String findEntityManagerName(CompetitorResult cr, int column) {
        int col = 0;
        for (JmdtCompetitorRunResult rr : cr.getResults()) {
            if (++col == column) {
                return rr.getEntityManagerName();
            }
        }
        return null;
    }

    private Set<String/*emName*/> createEntityManagerSet(List<CompetitorResult> data) {
        Set<String> result = new TreeSet<>();
        for (CompetitorResult cr : data) {
            for (JmdtCompetitorRunResult rr : cr.getResults()) {
                result.add(rr.getEntityManagerName());
            }
        }
        return result;
    }

    private String breakNameToMultiLine(String name) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (c == '-') {
                sb.append('\n');
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
