/*
 * Created by Daniel Marell 13-03-14 10:36 PM
 */
package se.marell.jmdt;

import java.util.List;

public interface JmdtDbConfiguration {
    List<EntityManagerWrapper> getEntityManagerWrappers();

    List<EntityManagerWrapper> getEntityManagerWrappersPrepareForReaders();
}
