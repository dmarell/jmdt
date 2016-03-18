/*
 * Created by Daniel Marell 12-12-28 11:21 PM
 */
package se.marell.jmdt.hibernate;

import se.marell.jmdt.AbstractEntityManagerWrapper;
import se.marell.jmdt.DbType;
import se.marell.jmdt.JpaImplementation;
import se.marell.jmdt.SqlLogFetcherAppender;

import java.util.List;

public class HibernateEntityManagerWrapper extends AbstractEntityManagerWrapper {
    public HibernateEntityManagerWrapper(String caption, String puName, String url, String driver,
                                         String hibernateDialect, DbType dbType,
                                         String username, String password) {
        super(caption, puName, JpaImplementation.hibernate, dbType,
                "^insert .*",
                "^select .*");
        map.put("hibernate.connection.url", url);
        map.put("hibernate.connection.driver_class", driver);
        map.put("hibernate.dialect", hibernateDialect);
        map.put("hibernate.connection.username", username);
        map.put("hibernate.connection.password", password);
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
