/*
 * Created by Daniel Marell 13-02-19 3:37 PM
 */
package se.marell.jmdt.presentation;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class DeepReadResult {
    private Button runButton;
    private SimpleStringProperty numReads = new SimpleStringProperty();
    private SimpleStringProperty time = new SimpleStringProperty();
    private SimpleStringProperty numSelects = new SimpleStringProperty();

    public DeepReadResult(String buttonText) {
        runButton = new Button(buttonText);
        runButton.setMaxWidth(Double.MAX_VALUE);
    }

    public void setButtonListener(EventHandler<ActionEvent> buttonListener) {
        runButton.setOnAction(buttonListener);
    }

    public Button getRunButton() {
        return runButton;
    }

    public void setNumReads(String numReads) {
        this.numReads.set(numReads);
    }

    public String getNumReads() {
        return numReads.get();
    }

    public Property numReadsProperty() {
        return numReads;
    }

    public void setTime(String time) {
        this.time.set(time);
    }

    public String getTime() {
        return time.get();
    }

    public Property timeProperty() {
        return time;
    }

    public void setNumSelects(String numSelects) {
        this.numSelects.set(numSelects);
    }

    public String getNumSelects() {
        return numSelects.get();
    }

    public Property numSelectsProperty() {
        return numSelects;
    }

    public void clearResult() {
        setNumReads("");
        setNumSelects("");
        setTime("");
    }
}
