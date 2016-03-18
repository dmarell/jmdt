/*
 * Created by Daniel Marell 12-12-28 11:21 PM
 */
package se.marell.jmdt.openjpa;

import se.marell.jmdt.AbstractEntityManagerWrapper;
import se.marell.jmdt.DbType;
import se.marell.jmdt.JpaImplementation;
import se.marell.jmdt.SqlLogFetcherAppender;

import java.util.List;

public class OpenJpaEntityManagerWrapper extends AbstractEntityManagerWrapper {
    public OpenJpaEntityManagerWrapper(String caption, String puName, String url, String driver, DbType dbType,
                                       String username, String password) {
        super(caption, puName, JpaImplementation.openjpa, dbType,
                "^executing query\\: insert .*",
                "^executing query\\: select .*");
        map.put("openjpa.ConnectionURL", url);
        map.put("openjpa.ConnectionDriverName", driver);
        map.put("openjpa.ConnectionUserName", username);
        map.put("openjpa.ConnectionPassword", password);
        initEntityManager();
    }

    @Override
    public void clearSqlLog() {
        SqlLogFetcherAppender.clearSqlLog();
    }

    @Override
    public List<String> fetchSqlLog() {
        return SqlLogFetcherAppender.fetchSqlLog();
    }
}
