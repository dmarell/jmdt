/*
 * Created by Daniel Marell 13-02-17 12:42 PM
 */
package se.marell.jmdt;

import javax.persistence.EntityManager;
import java.util.List;

public interface EntityManagerWrapper {
    String getCaption();

    EntityManager getEm();

    JpaImplementation getJpaImplementation();

    DbType getDbType();

    void clearSqlLog();

    List<String> fetchSqlLog();

    int numInsertsInSqlLog(List<String> sqlLog);

    int numSelectsInSqlLog(List<String> sqlLog);
}
