/*
 * Created by Daniel Marell 13-01-04 10:18 PM
 */
package se.marell.jmdt.dbutilsway;

import se.marell.jmdt.commons.AbstractHierarchyReaderWriter;
import se.marell.jmdt.commons.AbstractJpaHierarchyReader;
import se.marell.jmdt.commons.AbstractJpaHierarchyWriter;
import se.marell.jmdt.commons.JmdtCompetitorPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DbUtilsWayPlugin implements JmdtCompetitorPlugin {
    private List<? extends AbstractJpaHierarchyWriter> hierarchyWriterCompetitors = Arrays.asList(new DbUtilsWriter());
    private List<? extends AbstractJpaHierarchyReader> hierarchyReaderCompetitors = Arrays.asList(new DbUtilsReader());
    private List<? extends AbstractHierarchyReaderWriter> nonJpaCompetitors = Collections.emptyList();

    public List<? extends AbstractJpaHierarchyWriter> getHierarchyWriterCompetitors() {
        return hierarchyWriterCompetitors;
    }

    public List<? extends AbstractJpaHierarchyReader> getHierarchyReaderCompetitors() {
        return hierarchyReaderCompetitors;
    }

    public List<? extends AbstractHierarchyReaderWriter> getNonJpaCompetitors() {
        return nonJpaCompetitors;
    }
}
