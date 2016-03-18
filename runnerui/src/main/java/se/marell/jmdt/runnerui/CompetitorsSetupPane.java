/*
 * Created by Daniel Marell 13-02-17 10:51 PM
 */
package se.marell.jmdt.runnerui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import se.marell.jmdt.JmdtRunner;

public class CompetitorsSetupPane extends BorderPane {
    public CompetitorsSetupPane(final JmdtRunner runner) {
        Button btn = new Button("Setup DBs for write tests");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                runner.prepareDatabasesForReadTests();
            }
        });
        setCenter(btn);
    }
}
