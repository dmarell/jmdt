/*
 * Created by Daniel Marell 13-03-14 10:40 PM
 */
package se.marell.jmdt;

import java.util.Arrays;
import java.util.List;

public class JmdtExampleDbConfiguration implements JmdtDbConfiguration {
    private static final String LOCALHOST = "localhost";

    private final String localMysqlHost = "localhost";
    private String dbHost;

//    private final String mysqlHost = dbHost;
//    private final String db2Host = dbHost;
//    private final String h2Host = dbHost;
//    private final String piHost = "192.168.0.6";
//
//    private final String proxyHost = "localhost";
//    private final String schemaName = "CUSTORDERS";
//    private final int mysqlPort = 3306;
//    private final int mysqlPort0ms = 42000;
//    private final int mysqlPort5ms = 42001;
//    private final int db2Port = 50000;
//    private final int db2Port0ms = 42004;
//    private final int db2Port5ms = 42005;
//    private final int h2Port = 0;
//    private final String username = "afuser";
//    private final String password = "iphone";

    private List<EntityManagerWrapper> emwList;
    private List<EntityManagerWrapper> emwPrepareForReadersList;

    public JmdtExampleDbConfiguration(String dbHost, int mysqlPort, int db2Port, String schemaName, String username, String password) {
//        startProxySocketServer(mysqlPort0ms, mysqlHost, mysqlPort, 0);
//        startProxySocketServer(mysqlPort5ms, mysqlHost, mysqlPort, 5);
//        startProxySocketServer(db2Port0ms, db2Host, db2Port, 0);
//        startProxySocketServer(db2Port5ms, db2Host, db2Port, 5);

        EntityManagerWrapperFactory ef = new EntityManagerWrapperFactory();
        emwList = Arrays.asList(
//                ef.createEntityManagerWrapper("openjpa-localmysql-lazy", "openjpa-lazy", localMysqlHost, mysqlPort, schemaName, JpaImplementation.openjpa, DbType.mysql),
//                ef.createEntityManagerWrapper("hibernate-localmysql-lazy", "hibernate-lazy", localMysqlHost, mysqlPort, schemaName, JpaImplementation.hibernate, DbType.mysql),
//                ef.createEntityManagerWrapper("eclipse-localmysql-lazy", "eclipse-lazy", localMysqlHost, mysqlPort, schemaName, JpaImplementation.eclipse, DbType.mysql),
//
//                ef.createEntityManagerWrapper("openjpa-localmysql-eager", "openjpa-eager", localMysqlHost, mysqlPort, schemaName, JpaImplementation.openjpa, DbType.mysql),
//                ef.createEntityManagerWrapper("hibernate-localmysql-eager", "hibernate-eager", localMysqlHost, mysqlPort, schemaName, JpaImplementation.hibernate, DbType.mysql),
//                ef.createEntityManagerWrapper("eclipse-localmysql-eager", "eclipse-eager", localMysqlHost, mysqlPort, schemaName, JpaImplementation.eclipse, DbType.mysql)

                ef.createEntityManagerWrapper("openjpa-localmysql", "openjpa", LOCALHOST, mysqlPort, null, schemaName, JpaImplementation.openjpa, DbType.mysql, username, password),
                ef.createEntityManagerWrapper("hibernate-localmysql", "hibernate", LOCALHOST, mysqlPort, null, schemaName, JpaImplementation.hibernate, DbType.mysql, username, password),
                ef.createEntityManagerWrapper("eclipse-localmysql", "eclipse", LOCALHOST, mysqlPort, null, schemaName, JpaImplementation.eclipse, DbType.mysql, username, password)

//                ef.createEntityManagerWrapper("openjpa-mysql-pi", piHost, mysqlPort, schemaName, JpaImplementation.openjpa, DbType.mysql),
//                ef.createEntityManagerWrapper("hibernate-pi", piHost, mysqlPort, schemaName, JpaImplementation.hibernate, DbType.mysql),
//                ef.createEntityManagerWrapper("eclipse-pi", piHost, mysqlPort, schemaName, JpaImplementation.eclipse, DbType.mysql),

//                ef.createEntityManagerWrapper("openjpa-h2", h2Host, h2Port, schemaName, JpaImplementation.openjpa, DbType.h2),
//                ef.createEntityManagerWrapper("hibernate-h2", h2Host, h2Port, schemaName, JpaImplementation.hibernate, DbType.h2),
//                ef.createEntityManagerWrapper("eclipse-h2", h2Host, h2Port, schemaName, JpaImplementation.eclipse, DbType.h2)
//
//                ef.createEntityManagerWrapper("openjpa-mysql", mysqlHost, mysqlPort, schemaName, JpaImplementation.openjpa, DbType.mysql),
//                ef.createEntityManagerWrapper("hibernate-mysql", mysqlHost, mysqlPort, schemaName, JpaImplementation.hibernate, DbType.mysql),
//                ef.createEntityManagerWrapper("eclipse-mysql", mysqlHost, mysqlPort, schemaName, JpaImplementation.eclipse, DbType.mysql)
//
//                ef.createEntityManagerWrapper("openjpa-db2", db2Host, db2Port, schemaName, JpaImplementation.openjpa, DbType.db2)
//                ef.createEntityManagerWrapper("hibernate-db2", db2Host, db2Port, schemaName, JpaImplementation.hibernate, DbType.db2),
//                ef.createEntityManagerWrapper("eclipse-db2", db2Host, db2Port, schemaName, JpaImplementation.eclipse, DbType.db2)

                // Latency tests
//                ef.createEntityManagerWrapper("openjpa-mysql-0", proxyHost, mysqlPort0ms, schemaName, JpaImplementation.openjpa, DbType.mysql)
//                ef.createEntityManagerWrapper("hibernate-mysql-0", proxyHost, mysqlPort0ms, schemaName, JpaImplementation.hibernate, DbType.mysql),
//                ef.createEntityManagerWrapper("eclipse-mysql-0", proxyHost, mysqlPort0ms, schemaName, JpaImplementation.eclipse, DbType.mysql),
//
//                ef.createEntityManagerWrapper("openjpa-mysql-5", proxyHost, mysqlPort5ms, schemaName, JpaImplementation.openjpa, DbType.mysql),
//                ef.createEntityManagerWrapper("hibernate-mysql-5", proxyHost, mysqlPort5ms, schemaName, JpaImplementation.hibernate, DbType.mysql),
//                ef.createEntityManagerWrapper("eclipse-mysql-5", proxyHost, mysqlPort5ms, schemaName, JpaImplementation.eclipse, DbType.mysql),

//                ef.createEntityManagerWrapper("openjpa-db2-0", proxyHost, db2Port0ms, schemaName, JpaImplementation.openjpa, DbType.db2),
//                ef.createEntityManagerWrapper("hibernate-db2-0", proxyHost, db2Port0ms, schemaName, JpaImplementation.hibernate, DbType.db2),
//                ef.createEntityManagerWrapper("eclipse-db2-0", proxyHost, db2Port0ms, schemaName, JpaImplementation.eclipse, DbType.db2),
//
//                ef.createEntityManagerWrapper("openjpa-db2-5", proxyHost, db2Port5ms, schemaName, JpaImplementation.openjpa, DbType.db2),
//                ef.createEntityManagerWrapper("hibernate-db2-5", proxyHost, db2Port5ms, schemaName, JpaImplementation.hibernate, DbType.db2),
//                ef.createEntityManagerWrapper("eclipse-db2-5", proxyHost, db2Port5ms, schemaName, JpaImplementation.eclipse, DbType.db2)
        );

        emwPrepareForReadersList = Arrays.asList(
                ef.createEntityManagerWrapper("openjpa-localmysql", LOCALHOST, mysqlPort, null, schemaName, JpaImplementation.openjpa, DbType.mysql, username, password)
//                ef.createEntityManagerWrapper("eclipse-mysql", mysqlHost, mysqlPort, schemaName, JpaImplementation.eclipse, DbType.mysql),
//                ef.createEntityManagerWrapper("eclipse-db2", db2Host, db2Port, schemaName, JpaImplementation.eclipse, DbType.db2),
//                ef.createEntityManagerWrapper("eclipse-h2", h2Host, h2Port, schemaName, JpaImplementation.eclipse, DbType.h2)
        );
    }

    @Override
    public List<EntityManagerWrapper> getEntityManagerWrappers() {
        return emwList;
    }

    @Override
    public List<EntityManagerWrapper> getEntityManagerWrappersPrepareForReaders() {
        return emwPrepareForReadersList;
    }
}
