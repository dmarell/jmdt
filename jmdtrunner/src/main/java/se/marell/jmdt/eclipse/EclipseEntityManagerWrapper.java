/*
 * Created by Daniel Marell 12-12-28 11:20 PM
 */
package se.marell.jmdt.eclipse;

import se.marell.jmdt.AbstractEntityManagerWrapper;
import se.marell.jmdt.DbType;
import se.marell.jmdt.JpaImplementation;

import java.util.List;

public class EclipseEntityManagerWrapper extends AbstractEntityManagerWrapper {
    public EclipseEntityManagerWrapper(String caption, String puName, String url, String driver, DbType dbType,
                                       String username, String password) {
        super(caption, puName, JpaImplementation.eclipse, dbType,
                "^insert .*",
                "^select .*");
        map.put("javax.persistence.jdbc.url", url);
        map.put("javax.persistence.jdbc.driver", driver);
        map.put("javax.persistence.jdbc.user", username);
        map.put("javax.persistence.jdbc.password", password);
        initEntityManager();
    }

    @Override
    public void clearSqlLog() {
        CustomSessionLog.clearSqlLog();
    }

    @Override
    public List<String> fetchSqlLog() {
        return CustomSessionLog.fetchSqlLog();
    }
}
