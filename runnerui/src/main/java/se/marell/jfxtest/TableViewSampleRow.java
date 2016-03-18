/*
 * Created by Daniel Marell 13-01-03 10:57 PM
 */
package se.marell.jfxtest;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;

import java.util.Arrays;

public class TableViewSampleRow {
    private String col1;
    private String col2;
    private Button btn;
    private ObservableList<? extends StringProperty> strings;

    public TableViewSampleRow(String col1, String col2) {
        this.col1 = col1;
        this.col2 = col2;
        btn = new Button("Btn");
        strings = FXCollections.observableArrayList(Arrays.asList(
                new SimpleStringProperty("s1"), new SimpleStringProperty("s3"), new SimpleStringProperty("s3")
        ));
    }

    public String getCol1() {
        return col1;
    }

    public String getCol2() {
        return col2;
    }

    public Button getBtn() {
        return btn;
    }

    public ObservableList<? extends StringProperty> getStrings() {
        return strings;
    }
}
