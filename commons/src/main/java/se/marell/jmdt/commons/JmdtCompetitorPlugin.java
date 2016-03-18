/*
 * Created by Daniel Marell 13-01-03 1:10 PM
 */
package se.marell.jmdt.commons;

import java.util.List;

public interface JmdtCompetitorPlugin {
    List<? extends AbstractJpaHierarchyWriter> getHierarchyWriterCompetitors();

    List<? extends AbstractJpaHierarchyReader> getHierarchyReaderCompetitors();

    List<? extends AbstractHierarchyReaderWriter> getNonJpaCompetitors();
}
