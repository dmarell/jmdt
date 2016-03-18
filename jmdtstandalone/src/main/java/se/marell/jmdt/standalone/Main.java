/*
 * Created by Daniel Marell 13-03-05 5:31 AM
 */
package se.marell.jmdt.standalone;

import org.apache.log4j.Logger;
import se.marell.jmdt.*;
import se.marell.jmdt.commons.AbstractJpaHierarchyReader;
import se.marell.jmdt.commons.AbstractJpaHierarchyWriter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Main {
    private final static Logger logger = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Started");
        int argNumber = 0;
        DbType dbType = DbType.valueOf(args[argNumber++]);
        JpaImplementation jpaImpl = JpaImplementation.valueOf(args[argNumber++]);
        String dbHost = args[argNumber++];
        int dbPort = Integer.parseInt(args[argNumber++]);
        String db2Database = args[argNumber++];
        String schemaName = args[argNumber++];
        String readerName = args[argNumber++];
        int numReadCustomers = Integer.parseInt(args[argNumber++]);
        String writerName = args[argNumber++];
        int numWriteCustomers = Integer.parseInt(args[argNumber++]);
        String username = args[argNumber++];
        String password = args[argNumber++];

        JmdtDbConfiguration dbConfig = new JmdtExampleDbConfiguration(dbHost, 3307, 50000, schemaName, "afuser", "iphone");
        JmdtRunner runner = new JmdtRunner(dbConfig, numReadCustomers, numWriteCustomers);
        EntityManagerWrapperFactory ef = new EntityManagerWrapperFactory();
        EntityManagerWrapper emw = ef.createEntityManagerWrapper(
                "standalone-" + jpaImpl.name() + "-" + dbType.name(),
                jpaImpl.name(), dbHost, dbPort,
                db2Database,
                schemaName, jpaImpl, dbType,
                username, password);
        List<EntityManagerWrapper> emwList = Arrays.asList(emw);


        runner.prepareDatabasesForReadTests(emwList);

        Map<String, List<Integer>> writeMeasurementResult = runner.runHierarchyWriteTests(emwList, getHierarchyWriters(runner, writerName));
        final String customerNoPattern = "%0";
        Map<String, List<Integer>> readMeasurementResult = new TreeMap<String, List<Integer>>();
        runner.runHierarchyReadTests(emwList, getHierarchyReaders(runner, readerName), customerNoPattern, readMeasurementResult);

        runner.printMeasurementResult(writeMeasurementResult, emwList, numWriteCustomers);
        runner.printMeasurementResult(readMeasurementResult, emwList, numReadCustomers);

        runner.closeEntityManagers(emwList);
        logger.info("Ready");
    }

    private static List<? extends AbstractJpaHierarchyReader> getHierarchyReaders(JmdtRunner runner, String readerName) {
        return Arrays.asList(runner.getHierarchyReader(readerName));
    }

    private static List<? extends AbstractJpaHierarchyWriter> getHierarchyWriters(JmdtRunner runner, String writerName) {
        return Arrays.asList(runner.getHierarchyWriter(writerName));
    }
}
