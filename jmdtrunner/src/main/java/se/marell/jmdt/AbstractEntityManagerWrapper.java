/*
 * Created by Daniel Marell 12-12-27 11:27 AM
 */
package se.marell.jmdt;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractEntityManagerWrapper implements EntityManagerWrapper {
    private String caption;
    private EntityManager em;
    private String puName;
    protected Map<String, String> map = new HashMap<String, String>();
    private JpaImplementation jpaImplementation;
    private DbType dbType;
    private String sqlInsertsPattern;
    private String sqlSelectsPattern;

    protected AbstractEntityManagerWrapper(String caption, String puName, JpaImplementation jpaImplementation,
                                           DbType dbType, String sqlInsertsPattern, String sqlSelectsPattern) {
        this.caption = caption;
        this.puName = puName;
        this.jpaImplementation = jpaImplementation;
        this.dbType = dbType;
        this.sqlInsertsPattern = sqlInsertsPattern;
        this.sqlSelectsPattern = sqlSelectsPattern;
    }

    public void initEntityManager() {
        em = Persistence.createEntityManagerFactory(puName, map).createEntityManager();
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public EntityManager getEm() {
        return em;
    }

    @Override
    public JpaImplementation getJpaImplementation() {
        return jpaImplementation;
    }

    @Override
    public DbType getDbType() {
        return dbType;
    }

    @Override
    public int numInsertsInSqlLog(List<String> sqlLog) {
        return countCommands(sqlInsertsPattern, sqlLog);
    }

    @Override
    public int numSelectsInSqlLog(List<String> sqlLog) {
        return countCommands(sqlSelectsPattern, sqlLog);
    }

    protected int countCommands(String pattern, List<String> strings) {
        int count = 0;
        for (String s : strings) {
            if (s.toLowerCase().matches(pattern)) {
                ++count;
            }
        }
        return count;
    }
}
