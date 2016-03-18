/*
 * Created by Daniel Marell 13-02-20 8:03 AM
 */
package se.marell.jmdt.presentation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import se.marell.jmdt.EntityManagerWrapper;

import java.util.List;

public class JpaLazyEagerLoadPage2 extends AbstractJpaLazyEagerLoadPage {
    public JpaLazyEagerLoadPage2(List<EntityManagerWrapper> emwList) {
        AnchorPane pane = new AnchorPane();
        setCenter(pane);

        final Text text = new Text("select cu from Customer1 cu where cu.customerNo = :customerNo");
        text.setFill(Color.BLUE);
        text.setFont(new Font("Arial", 20));

        ObservableList<DeepReadResult> data = FXCollections.observableArrayList();
        for (final EntityManagerWrapper emw : emwList) {
            data.add(createDeepReadResult(emw, emw.getCaption().endsWith("lazy")));
        }

        double width = 800;
        double height = 600;

        DeepReadTable deepReadTable = new DeepReadTable(data);
        deepReadTable.setPrefWidth(600);
        deepReadTable.setMaxWidth(600);
        deepReadTable.setMaxHeight(200);

        AnchorPane.setTopAnchor(text, height / 4);
        AnchorPane.setLeftAnchor(text, width / 2 - 300);
        AnchorPane.setBottomAnchor(deepReadTable, height / 4);
        AnchorPane.setLeftAnchor(deepReadTable, width / 2 - 300);

        pane.getChildren().addAll(text, deepReadTable);
    }
}
