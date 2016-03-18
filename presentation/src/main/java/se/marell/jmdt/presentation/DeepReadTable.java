/*
 * Created by Daniel Marell 13-02-19 4:54 PM
 */
package se.marell.jmdt.presentation;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class DeepReadTable extends TableView<DeepReadResult> {
    public DeepReadTable(ObservableList<DeepReadResult> data) {
        setEditable(true);

        TableColumn<DeepReadResult, String> buttonCol = new TableColumn<>();
        buttonCol.setMinWidth(100);
        buttonCol.setCellValueFactory(
                new PropertyValueFactory<DeepReadResult, String>("runButton")
        );

        TableColumn<DeepReadResult, String> readsCol = new TableColumn<>("Row reads");
        readsCol.setMinWidth(100);
        readsCol.setCellValueFactory(
                new PropertyValueFactory<DeepReadResult, String>("numReads")
        );

        TableColumn<DeepReadResult, String> selectsCol = new TableColumn<>("Num selects");
        selectsCol.setMinWidth(100);
        selectsCol.setCellValueFactory(
                new PropertyValueFactory<DeepReadResult, String>("numSelects")
        );

        TableColumn<DeepReadResult, String> timeCol = new TableColumn<>("Time (ms)");
        timeCol.setMinWidth(100);
        timeCol.setCellValueFactory(
                new PropertyValueFactory<DeepReadResult, String>("time")
        );

        getColumns().addAll(buttonCol, readsCol, selectsCol, timeCol);

        setItems(data);
    }
}
