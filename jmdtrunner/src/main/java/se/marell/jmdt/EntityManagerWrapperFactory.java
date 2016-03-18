/*
 * Created by Daniel Marell 12-12-27 11:27 AM
 */
package se.marell.jmdt;

import se.marell.jmdt.eclipse.EclipseEntityManagerWrapper;
import se.marell.jmdt.hibernate.HibernateEntityManagerWrapper;
import se.marell.jmdt.openjpa.OpenJpaEntityManagerWrapper;

public class EntityManagerWrapperFactory {
    public EntityManagerWrapper createEntityManagerWrapper(String caption, String hostname, int port,
                                                           String db2Database, String schemaName,
                                                           JpaImplementation jpaImpl, DbType dbType,
                                                           String username, String password) {
        return createEntityManagerWrapper(caption, null, hostname, port, db2Database, schemaName, jpaImpl, dbType, username, password);
    }

    public EntityManagerWrapper createEntityManagerWrapper(String caption, String puName, String hostname, int port,
                                                           String db2Database,
                                                           String schemaName,
                                                           JpaImplementation jpaImpl, DbType dbType,
                                                           String username, String password) {
        final String mysqlUrl = "jdbc:mysql://" + hostname + ":" + port + "/" + schemaName;
        final String mySqlDriver = "com.mysql.jdbc.Driver";
        final String hibernateMysqlDialect = "org.hibernate.dialect.MySQLDialect";

//        final String db2Url = "jdbc:db2://" + hostname + ":" + port + "/jpatest:currentSchema=" + schemaName + ";";
//        final String db2Url = "jdbc:db2://" + hostname + ":" + port + "/QDTY35:currentSchema=" + schemaName + ";";
        final String db2Url = "jdbc:db2://" + hostname + ":" + port + "/" + db2Database + ":currentSchema=" + schemaName + ";";
        final String db2Driver = "com.ibm.db2.jcc.DB2Driver";
        final String hibernateDb2Dialect = "org.hibernate.dialect.DB2Dialect";

        final String h2Url = "jdbc:h2:tcp://" + hostname + (port != 0 ? (":" + port) : "") + "/h2/data/custorders";
        final String h2Driver = "org.h2.Driver";
        final String hibernateH2Dialect = "org.hibernate.dialect.H2Dialect";

        String url;
        String driver;
        String hibernateDialect;

        switch (dbType) {
            case mysql:
                url = mysqlUrl;
                driver = mySqlDriver;
                hibernateDialect = hibernateMysqlDialect;
                break;
            case h2:
                url = h2Url;
                driver = h2Driver;
                hibernateDialect = hibernateH2Dialect;
                break;
            case db2:
                url = db2Url;
                driver = db2Driver;
                hibernateDialect = hibernateDb2Dialect;
                break;
            default:
                throw new IllegalArgumentException(dbType.name());
        }

        switch (jpaImpl) {
            case openjpa:
                return new OpenJpaEntityManagerWrapper(caption, puName == null ? "openjpa" : puName, url, driver,
                        dbType, username, password);
            case hibernate:
                return new HibernateEntityManagerWrapper(caption, puName == null ? "hibernate" : puName, url, driver,
                        hibernateDialect, dbType, username, password);
            case eclipse:
                return new EclipseEntityManagerWrapper(caption, puName == null ? "eclipse" : puName, url, driver,
                        dbType, username, password);
            default:
                throw new IllegalArgumentException(jpaImpl.name());
        }
    }
}
