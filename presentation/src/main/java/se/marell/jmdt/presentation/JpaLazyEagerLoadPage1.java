/*
 * Created by Daniel Marell 13-02-20 8:02 AM
 */
package se.marell.jmdt.presentation;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import se.marell.jmdt.EntityManagerWrapper;

import java.util.List;

public class JpaLazyEagerLoadPage1 extends AbstractJpaLazyEagerLoadPage {
    private List<EntityManagerWrapper> emwList;

    public JpaLazyEagerLoadPage1(List<EntityManagerWrapper> emwList) {
        this.emwList = emwList;

        final Text hpqlText = new Text("select cu from LazyCustomer cu where cu.customerNo = :customerNo");
        hpqlText.setFill(Color.valueOf("#f5f5f5"));
        hpqlText.setFont(new Font("Arial", 20));
        hpqlText.setTextAlignment(TextAlignment.CENTER);

        FlowPane hpqlPane = new FlowPane();
        hpqlPane.setStyle("-fx-background-color: #0000cb; -fx-padding: 20; -fx-spacing: 20;");
        hpqlPane.setAlignment(Pos.CENTER);
        hpqlPane.getChildren().add(hpqlText);

        final Text javaCodeText = new Text(
                "for (LazyCustomerOrder co : cu.getCustomerOrders())\n" +
                        "    count += co.getOrderLines().size();");
        javaCodeText.setFill(Color.valueOf("#f5f5f5"));
        javaCodeText.setFont(new Font("Courier", 16));
//        javaCodeText.setId("code");
//        javaCodeText.setTextAlignment(TextAlignment.CENTER);
        FlowPane javaCodePane = new FlowPane();
//        javaCodePane.getStyleClass().add("code");
        javaCodePane.setStyle("-fx-background-color: #0000cb; -fx-padding: 20; -fx-spacing: 20;");
        javaCodePane.setAlignment(Pos.CENTER);
        javaCodePane.getChildren().add(javaCodeText);

        final EntityManagerWrapper emw = lookupEmw("openjpa-localmysql-lazy");

        final Text numReadsText = createResultText();
        final Text numSelectsText = createResultText();
        final Text timeText = createResultText();

        Button btn = new Button("Lazy Customer openjpa");
        btn.setAlignment(Pos.CENTER);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                numReadsText.setText("");
                numSelectsText.setText("");
                timeText.setText("");
                long start = System.currentTimeMillis();
                final int numReads = 1;
                int numRowReads = 0;
                for (int i = 0; i < numReads; ++i) {
                    numRowReads += deepRead(emw, true);
                }
                List<String> sqlLog = emw.fetchSqlLog();
                int numSelects = countCommands("select", sqlLog);
                numReadsText.setText("OrderLine reads: " + numRowReads);
                numSelectsText.setText("Number of selects: " + numSelects);
                timeText.setText("Time (ms): " + (System.currentTimeMillis() - start));
            }
        });

        FlowPane pane = new FlowPane();
//        pane.getStyleClass().add("presentation-pane");
        pane.setStyle("-fx-background-color: #00008b;");
        pane.setOrientation(Orientation.VERTICAL);
        pane.setAlignment(Pos.CENTER);
        pane.setVgap(40);
        setCenter(pane);
        pane.getChildren().addAll(hpqlPane, javaCodePane, btn, numReadsText, numSelectsText, timeText);
    }

    private Text createResultText() {
        final Text text = new Text();
        text.setFill(Color.valueOf("#f5f5f5"));
        text.setFont(new Font("Arial", 20));
        return text;
    }

    private EntityManagerWrapper lookupEmw(String s) {
        for (EntityManagerWrapper w : emwList) {
            if (w.getCaption().equals(s)) {
                return w;
            }
        }
        return null;
    }
}
