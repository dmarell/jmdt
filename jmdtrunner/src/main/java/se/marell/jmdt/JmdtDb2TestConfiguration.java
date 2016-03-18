/*
 * Created by Daniel Marell 13-03-14 10:40 PM
 */
package se.marell.jmdt;

import java.util.Arrays;
import java.util.List;

public class JmdtDb2TestConfiguration implements JmdtDbConfiguration {
    private List<EntityManagerWrapper> emwList;
    private List<EntityManagerWrapper> emwPrepareForReadersList;

    public JmdtDb2TestConfiguration() {
        EntityManagerWrapperFactory ef = new EntityManagerWrapperFactory();
        emwList = Arrays.asList(
                ef.createEntityManagerWrapper("db2-vm", "172.16.230.136", 50000, "CUSTORDERS", "CUSTORDERS", JpaImplementation.openjpa, DbType.db2, "afuser", "iphone")
//                ef.createEntityManagerWrapper("db2-remote", "bcdnnnnn", 50000, "test", JpaImplementation.openjpa, DbType.db2, "afuser", "iphone"),
//                ef.createEntityManagerWrapper("db2-u270t0", "u270t0", 60004, "SY35BAS2", JpaImplementation.openjpa, DbType.db2, "afuser", "iphone")
        );
        emwPrepareForReadersList = emwList;
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
