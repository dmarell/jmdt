/*
 * Created by Daniel Marell 13-01-03 9:27 PM
 */
package se.marell.jfxtest;

import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TableViewSample extends Application {
    private TableView table = new TableView();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        TableColumn col1 = new TableColumn("Col1");
        col1.setMinWidth(100);
        col1.setCellValueFactory(new PropertyValueFactory<TableViewSampleRow, String>("col1"));

        TableColumn col2 = new TableColumn("Col2");
        col2.setMinWidth(100);
        col2.setCellValueFactory(new PropertyValueFactory<TableViewSampleRow, String>("col2"));

        TableColumn col3 = new TableColumn("Col3");
        col3.setMinWidth(100);
        Callback<TableColumn.CellDataFeatures<TableViewSampleRow, Button>, ObservableValue<Button>> factory =
                new Callback<TableColumn.CellDataFeatures<TableViewSampleRow, Button>, ObservableValue<Button>>() {
                    @Override
                    public ObservableValue<Button> call(TableColumn.CellDataFeatures<TableViewSampleRow, Button> p) {
                        return new SimpleObjectProperty<>(p.getValue().getBtn());
                    }
                };
        col3.setCellValueFactory(factory);

        table.getColumns().addAll(col1, col2, col3);

        for (int i = 0; i < 3; ++i) {
            TableColumn col = new TableColumn("list" + i);
            col.setMinWidth(100);
            final int final_i = i;
            Callback<TableColumn.CellDataFeatures<TableViewSampleRow, String>, ObservableValue<String>> factory2 =
                    new Callback<TableColumn.CellDataFeatures<TableViewSampleRow, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<TableViewSampleRow, String> p) {
                            return p.getValue().getStrings().get(final_i);
                        }
                    };
            col.setCellValueFactory(factory2);
            table.getColumns().add(col);
        }

        final ObservableList<TableViewSampleRow> data = FXCollections.observableArrayList(
                new TableViewSampleRow("r0-col1", "r0-col2"),
                new TableViewSampleRow("r1-col1", "r1-col2"),
                new TableViewSampleRow("r2-col1", "r2-col2")
        );

        table.setItems(data);

        Button btn = new Button("Change");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println("btn");
                data.get(0).getStrings().get(0).set("kalle");
            }
        });

        VBox box = new VBox();
        box.setSpacing(10);
        box.getChildren().addAll(table, btn);

        Scene scene = new Scene(box, 700, 400);
        stage.setTitle("JMarellDbTest");
        stage.setScene(scene);
        stage.show();
    }
}