/*
 * Created by Daniel Marell 13-01-03 3:55 PM
 */
package se.marell.jmdt.runnerui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import se.marell.jmdt.JmdtDbConfiguration;
import se.marell.jmdt.JmdtExampleDbConfiguration;
import se.marell.jmdt.JmdtRunner;

/*
* Testmatris:
*
* Hierarchy write (5000,10,10)
*
*                  openjpa                 hibernate               eclipse
*                  mysql   db2     h2      mysql   db2     h2      mysql   db2     h2
* JpaWriter        123     123     123     123     123     123     123     123     123
* JdbcWriter       123     123     123     123     123     123     123     123     123
* JdbcBatchWriter  123     123     123     123     123     123     123     123     123
* DbUtilsWriter    123     123     123     123     123     123     123     123     123
* MarellWriter     123     123     123     123     123     123     123     123     123
* SpringWriter     123     123     123     123     123     123     123     123     123
*
*
* Hierarchy read (500,10,10)
*
*                  openjpa                 hibernate               eclipse
*                  mysql   db2     h2      mysql   db2     h2      mysql   db2     h2
* JpaReader        123     123     123     123     123     123     123     123     123
* JdbcReader       123     123     123     123     123     123     123     123     123
* DbUtilsReader    123     123     123     123     123     123     123     123     123
* SpringReader     123     123     123     123     123     123     123     123     123
*
*
* Partial delete (50,10,10)
*
*                  openjpa                 hibernate               eclipse
*                  mysql   db2     h2      mysql   db2     h2      mysql   db2     h2
* JpaDeleter       123     123     123     123     123     123     123     123     123
*
*
* NoSQL variants
*
* Neo4jWriter      123     (50,10,10)
* Neo4jReader      123     (5000,10,10)
* MongoDbWriter    123     (50,10,10)
* MongoDbReader    123     (5000,10,10)
*/
public class JmdtApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        String dbHost = getParameters().getNamed().get("dbhost");
        String clean = getParameters().getNamed().get("clean");
        int numReadCustomers = 5000;
        int numWriteCustomers = 500;
        JmdtDbConfiguration dbConfig = new JmdtExampleDbConfiguration(dbHost, 3307, 50000, "CUSTORDERS", "afuser", "iphone");
        final JmdtRunner runner = new JmdtRunner(dbConfig, numReadCustomers, numWriteCustomers);
        if (clean != null) {
            runner.deleteAllDatabases();
        }

        CompetitorsTopPane writeTablePane = new CompetitorsTopWritePane(runner, runner.getEntityManagerWrappers(), runner.getHierarchyWriters());
        CompetitorsTopPane readTablePane = new CompetitorsTopReadPane(runner, runner.getEntityManagerWrappers(), runner.getHierarchyReaders());

        Tab jpaWriteTab = new Tab();
        jpaWriteTab.setText("JPA Hierarchy Write");
        jpaWriteTab.setContent(writeTablePane);

        Tab jpaReadTab = new Tab();
        jpaReadTab.setText("JPA Hierarchy Read");
        jpaReadTab.setContent(readTablePane);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(jpaWriteTab, jpaReadTab);

        Scene scene = new Scene(tabPane, 1000, 800);
        primaryStage.setTitle("JMarellDbTest");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
