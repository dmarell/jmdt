/*
 * Created by Daniel Marell 12-12-29 10:24 AM
 */
package se.marell.jmdt.runnerui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se.marell.jmdt.*;
import se.marell.jmdt.commons.JmdtCompetitorRunResult;
import se.marell.jmdt.jpaway.JpaWriter;

import java.util.List;

/*
* Kontroller för en enskild test:
*
* Test category:       Hierarchy writing
* Test implementation: JpaWriter
* Entity manager:      openjpa-mysql
* Last result (ms):    330
* SQL inserts:         12
* SQL selects:         12
*
* Button Run test
* Button Show SQL
* Button Show Source
*/
public class JpaWriterApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private CompetitorPane competitorsPane;

    @Override
    public void start(Stage primaryStage) {
        String dbhost = getParameters().getNamed().get("dbhost");

        final EntityManagerWrapper emp = new EntityManagerWrapperFactory().createEntityManagerWrapper(
                "openjpa-mysql", // Caption for this parameter combination
                dbhost, // Database hostname
                3306, // Database port
                "custorders", // DB2 database name
                "custorders", // Schema name
                JpaImplementation.openjpa, // JPA implementation
                DbType.mysql, // Database type
                "root",
                "iphone");

        int numReadCustomers = 5000;
        int numWriteCustomers = 500;
        final JmdtRunner runner = new JmdtRunner(numReadCustomers, numWriteCustomers);
        final JpaWriter writer = new JpaWriter();

        competitorsPane = new CompetitorPane(writer, emp.getCaption(), new CompetitorPane.TestRunner() {
            @Override
            public JmdtCompetitorRunResult runTest() {
                runner.runJpaHierarchyWriteTest(emp, writer, new CompetitorPane.ProgressAdapter(competitorsPane));
                List<String> sqlLog = emp.fetchSqlLog();
                return new JmdtCompetitorRunResult(
                        writer,
                        writer.getMeasuredTime(),
                        emp.getCaption(),
                        emp.getJpaImplementation().name(),
                        "mysql",
                        sqlLog,
                        emp.numInsertsInSqlLog(sqlLog),
                        emp.numSelectsInSqlLog(sqlLog),
                        "");
            }
        });

        Scene scene = new Scene(competitorsPane, 350, 320);
        primaryStage.setTitle("JMarellDbTest");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
