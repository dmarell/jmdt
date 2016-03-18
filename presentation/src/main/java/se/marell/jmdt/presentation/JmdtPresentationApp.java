/*
 * Created by Daniel Marell 13-02-14 8:21 AM
 */
package se.marell.jmdt.presentation;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import se.marell.jmdt.*;
import se.marell.jmdt.runnerui.CompetitorsSetupPane;
import se.marell.jmdt.runnerui.CompetitorsTopPane;
import se.marell.jmdt.runnerui.CompetitorsTopReadPane;
import se.marell.jmdt.runnerui.CompetitorsTopWritePane;

import java.util.Arrays;
import java.util.List;

public class JmdtPresentationApp extends Application {
    private BorderPane topPane = new BorderPane();
    private int pageNumber;
    private List<Parent> pages;
    private Stage stage;

    private String schemaName = "CUSTORDERS";
    private String username = "afuser";
    private String password = "iphone";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        String dbHost = getParameters().getNamed().get("dbhost");
        String clean = getParameters().getNamed().get("clean");
        int numReadCustomers = 5000;
        int numWriteCustomers = 500;
        JmdtDbConfiguration config = new JmdtExampleDbConfiguration(dbHost, 3307, 50000, schemaName, username, password);
        final JmdtRunner runner = new JmdtRunner(config, numReadCustomers, numWriteCustomers);
        if (clean != null) {
            runner.deleteAllDatabases();
        }

        pages = Arrays.asList(
                createHtmlPage("page0.html"),
                adaptPage(new JpaLazyEagerLoadPage1(createEntityManagerWrappers(runner))),
                adaptPage(new JpaLazyEagerLoadPage2(createEntityManagerWrappers(runner))),
                createHtmlPage("infomodel.html"),
                createHtmlPage("mapping.html"),
                createTextPage("Text page 2"),
                createHtmlPage("page1.html"),
                adaptPage(createCompetitorsTabPane(runner)));

        Parent page = pages.get(0);
        topPane.setCenter(page);

        Scene scene = new Scene(topPane, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
//        scene.getStylesheets().add("file:///Users/daniel/swc/jmdt/presentation/style.css");

        primaryStage.setTitle("JMarellDbTest");
        primaryStage.setScene(scene);
        primaryStage.show();
        page.requestFocus();
    }

    private List<EntityManagerWrapper> createEntityManagerWrappers(JmdtRunner runner) {
        return Arrays.asList(
                runner.createLocalMysqlEntityManagerWrapper("openjpa-localmysql-lazy", "openjpa-lazy", JpaImplementation.openjpa, schemaName, username, password),
                runner.createLocalMysqlEntityManagerWrapper("hibernate-localmysql-lazy", "hibernate-lazy", JpaImplementation.hibernate, schemaName, username, password),
                runner.createLocalMysqlEntityManagerWrapper("eclipse-localmysql-lazy", "eclipse-lazy", JpaImplementation.eclipse, schemaName, username, password),
                runner.createLocalMysqlEntityManagerWrapper("openjpa-localmysql-eager", "openjpa-eager", JpaImplementation.openjpa, schemaName, username, password),
                runner.createLocalMysqlEntityManagerWrapper("hibernate-localmysql-eager", "hibernate-eager", JpaImplementation.hibernate, schemaName, username, password),
                runner.createLocalMysqlEntityManagerWrapper("eclipse-localmysql-eager", "eclipse-eager", JpaImplementation.eclipse, schemaName, username, password)
        );
    }

    private Parent adaptPage(Parent pane) {
        installEventHandlers(pane);
        return pane;
    }

    private Parent createHtmlPage(final String filename) {
        BorderPane pane = new BorderPane();
        WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();
        String cwd = System.getProperty("user.dir");
        webEngine.load("file:///" + cwd + "/presentation/" + filename);
        pane.setCenter(browser);
        installEventHandlers(pane);
        return pane;
    }

    private Parent createTextPage(String str) {
        BorderPane pane = new BorderPane();
        Text text = new Text(str);
        text.setFill(Color.BLUE);
        text.setFont(new Font("Arial", 50));
        HBox box = new HBox();
        box.getChildren().add(text);
        box.setAlignment(Pos.CENTER_RIGHT);
        pane.setCenter(text);
        installEventHandlers(pane);
        return pane;
    }

    private void installEventHandlers(final Node node) {
        final EventHandler<KeyEvent> keyEventHandler =
                new EventHandler<KeyEvent>() {
                    public void handle(final KeyEvent keyEvent) {
                        System.out.println("key=" + keyEvent);
                        int newPage = pageNumber;
                        if (keyEvent.getCode() == KeyCode.PAGE_DOWN || keyEvent.getCode() == KeyCode.SPACE) {
                            if (pageNumber < pages.size() - 1) {
                                newPage = pageNumber + 1;
                            }
                        } else if (keyEvent.getCode() == KeyCode.PAGE_UP) {
                            if (pageNumber > 0) {
                                newPage = pageNumber - 1;
                            }
//                        } else if (keyEvent.getCode() == KeyCode.E) {
//                            showEditDialog(pageNumber);
                        }
                        System.out.println("newPage=" + newPage + ",pageNumber=" + pageNumber);
                        if (newPage != pageNumber) {
                            showPage(newPage);
                            pageNumber = newPage;
                        }
                        keyEvent.consume();
                    }
                };
        node.setOnKeyPressed(keyEventHandler);

        final EventHandler<MouseEvent> mouseEventHandler =
                new EventHandler<MouseEvent>() {
                    public void handle(final MouseEvent mouseEvent) {
                        System.out.println("mouse click=" + mouseEvent);
                        if (mouseEvent.getClickCount() == 2) {
                            stage.setFullScreen(!stage.isFullScreen());
                        } else {
                            node.requestFocus();
                        }
                        mouseEvent.consume();
                    }
                };
        node.setOnMouseClicked(mouseEventHandler);
    }

//    private void showEditDialog(int pageNumber) {
//        final File file = new File("presentation", "page0.html");
//        final HTMLEditor editor = new HTMLEditor();
//        try {
//            editor.setHtmlText(JmdtUtil.readFile(file));
//            Stage dialogStage = new Stage();
//            dialogStage.setTitle("Edit page0.html");
//            dialogStage.initModality(Modality.WINDOW_MODAL);
//            dialogStage.show();
//            dialogStage.setScene(new Scene(VBoxBuilder.create()
//                    .children(editor)
//                    .alignment(Pos.CENTER)
//                    .padding(new Insets(5))
//                    .build()));
//            dialogStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
//                @Override
//                public void handle(WindowEvent windowEvent) {
//                    try {
//                        JmdtUtil.writeFile(file, editor.getHtmlText());
//                    } catch (IOException e) {
//                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                    }
//                }
//            });
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//    }

    private void showPage(int newPage) {
        Node page = pages.get(newPage);
        topPane.setCenter(page);
        page.requestFocus();
    }

    private TabPane createCompetitorsTabPane(JmdtRunner runner) {
        CompetitorsTopPane writeTablePane = new CompetitorsTopWritePane(runner, runner.getEntityManagerWrappers(), runner.getHierarchyWriters());
        CompetitorsTopPane readTablePane = new CompetitorsTopReadPane(runner, runner.getEntityManagerWrappers(), runner.getHierarchyReaders());
        CompetitorsSetupPane setupPane = new CompetitorsSetupPane(runner);

        Tab jpaWriteTab = new Tab();
        jpaWriteTab.setText("JPA Hierarchy Write");
        jpaWriteTab.setContent(writeTablePane);

        Tab jpaReadTab = new Tab();
        jpaReadTab.setText("JPA Hierarchy Read");
        jpaReadTab.setContent(readTablePane);

        Tab setupTab = new Tab();
        setupTab.setText("Setup");
        setupTab.setContent(setupPane);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(jpaWriteTab, jpaReadTab, setupTab);
        return tabPane;
    }
}