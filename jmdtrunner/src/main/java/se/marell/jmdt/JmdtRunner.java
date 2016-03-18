/*
 * Created by Daniel Marell 12-11-11 9:52 PM
 */
package se.marell.jmdt;

import entity.Customer;
import entity.CustomerOrder;
import entity.OrderLine;
import entity.Product;
import org.apache.log4j.Logger;
import se.marell.dcommons.progress.EmptyProgressTracker;
import se.marell.dcommons.progress.ProgressCombiner;
import se.marell.dcommons.progress.ProgressTracker;
import se.marell.jmdt.commons.AbstractHierarchyReaderWriter;
import se.marell.jmdt.commons.AbstractJpaHierarchyReader;
import se.marell.jmdt.commons.AbstractJpaHierarchyWriter;
import se.marell.jmdt.commons.JmdtCompetitorPlugin;
import se.marell.jmdt.jdbcway.JdbcWayPlugin;
import se.marell.jmdt.jdbcway.JdbcWriter;
import se.marell.socketproxy.ProxySocketServer;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.RollbackException;
import java.io.IOException;
import java.util.*;

public class JmdtRunner {
    private final static Logger logger = Logger.getLogger(JmdtRunner.class);

    private static ServiceLoader<JmdtCompetitorPlugin> pluginLoader = ServiceLoader.load(JmdtCompetitorPlugin.class);

    private int numWriteCustomers = 500;
    private int numReadCustomers = 5000;
    private static final int NUM_CUSTOMER_ORDERS = 10;
    private static final int NUM_ORDER_LINES = 10;
    private static final int NUM_PRODUCTS = 42;

    private List<? extends JmdtCompetitorPlugin> competitorPlugins;
    private JmdtDbConfiguration dbConfig;

    public static void main(String[] args) {
        logger.info("Started");

        JmdtDbConfiguration config = new JmdtDb2TestConfiguration();
        JmdtRunner runner = new JmdtRunner(config, 10000, 300);
        runner.deleteAllDatabases(config.getEntityManagerWrappersPrepareForReaders());
        runner.runJpaTests();
//        runner.runNonJpaTests();
    }

    private List<? extends JmdtCompetitorPlugin> createCompetitorPlugins2() {
        return Arrays.asList(
//                new JpaWayPlugin(),
                new JdbcWayPlugin()
        );
    }

    public JmdtRunner(JmdtDbConfiguration dbConfig, int numReadCustomers, int numWriteCustomers) {
        this.dbConfig = dbConfig;
        this.numReadCustomers = numReadCustomers;
        this.numWriteCustomers = numWriteCustomers;
        competitorPlugins = createCompetitorPlugins();
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignore) {
        }
    }

    public JmdtRunner(int numReadCustomers, int numWriteCustomers) {
        this(null, numReadCustomers, numWriteCustomers);
    }

    public EntityManagerWrapper createLocalMysqlEntityManagerWrapper(String caption, String puName,
                                                                     JpaImplementation jpaImpl,
                                                                     String schemaName,
                                                                     String username,
                                                                     String password) {
        EntityManagerWrapperFactory ef = new EntityManagerWrapperFactory();
        return ef.createEntityManagerWrapper(caption, puName, "localhost", 3307, null, schemaName, jpaImpl,
                DbType.mysql, username, password);
    }

    public List<? extends AbstractJpaHierarchyWriter> getHierarchyWriters() {
        List<AbstractJpaHierarchyWriter> result = new ArrayList<AbstractJpaHierarchyWriter>();
        for (JmdtCompetitorPlugin p : competitorPlugins) {
            result.addAll(p.getHierarchyWriterCompetitors());
        }
        return result;
    }

    public List<? extends AbstractJpaHierarchyReader> getHierarchyReaders() {
        List<AbstractJpaHierarchyReader> result = new ArrayList<AbstractJpaHierarchyReader>();
        for (JmdtCompetitorPlugin p : competitorPlugins) {
            result.addAll(p.getHierarchyReaderCompetitors());
        }
        return result;
    }

    public List<? extends AbstractHierarchyReaderWriter> getReaderWriters() {
        List<AbstractHierarchyReaderWriter> result = new ArrayList<AbstractHierarchyReaderWriter>();
        for (JmdtCompetitorPlugin p : competitorPlugins) {
            result.addAll(p.getNonJpaCompetitors());
        }
        return result;
    }

    private List<JmdtCompetitorPlugin> createCompetitorPlugins() {
        List<JmdtCompetitorPlugin> result = new ArrayList<JmdtCompetitorPlugin>();
        for (JmdtCompetitorPlugin plugin : pluginLoader) {
            result.add(plugin);
        }
        return result;
    }

    public void runJpaTests() {

    }

    public void runJpaTests(List<EntityManagerWrapper> el) {
        prepareDatabasesForReadTests(el);

        Map<String, List<Integer>> writeMeasurementResult = runHierarchyWriteTests(el, getHierarchyWriters());

        final String customerNoPattern = "%0";
        Map<String, List<Integer>> readMeasurementResult = new TreeMap<String, List<Integer>>();

        runHierarchyReadTests(el, getHierarchyReaders(), customerNoPattern, readMeasurementResult);

        printMeasurementResult(writeMeasurementResult, el, numWriteCustomers);
        printMeasurementResult(readMeasurementResult, el, numReadCustomers);

        runNonJpaWriteTests(getReaderWriters(), writeMeasurementResult);
        runNonJpaReadTests(getReaderWriters(), customerNoPattern, readMeasurementResult);

        closeEntityManagers(el);

        logger.debug("Ready");
    }

    public void runNonJpaTests(List<EntityManagerWrapper> el) {
        Map<String, List<Integer>> writeMeasurementResult = runHierarchyWriteTests(el, getHierarchyWriters());
        final String customerNoPattern = "%0";
        Map<String, List<Integer>> readMeasurementResult = new TreeMap<String, List<Integer>>();
        runNonJpaWriteTests(getReaderWriters(), writeMeasurementResult);
        runNonJpaReadTests(getReaderWriters(), customerNoPattern, readMeasurementResult);
        logger.debug("Ready");
    }

    public void closeEntityManagers(List<EntityManagerWrapper> empList) {
        logger.debug("Closing entity managers");
        for (EntityManagerWrapper emp : empList) {
            if (emp.getEm().getTransaction().isActive()) {
                emp.getEm().getTransaction().rollback();
            }
            emp.getEm().close();
        }
    }

    public List<EntityManagerWrapper> getEntityManagerWrappers() {
        return dbConfig.getEntityManagerWrappers();
    }

    public Map<String, List<Integer>> runHierarchyWriteTests(List<EntityManagerWrapper> empList,
                                                             List<? extends AbstractJpaHierarchyWriter> jpaWriters) {
        Map<String, List<Integer>> writeMeasurementResult = new TreeMap<String, List<Integer>>();
        for (EntityManagerWrapper emp : empList) {
            for (AbstractJpaHierarchyWriter w : jpaWriters) {
                runJpaHierarchyWriteTest(emp, w, new EmptyProgressTracker());
                addMeasurementResult(writeMeasurementResult, w.getName(), w.getMeasuredTime());
            }
        }
        logger.info("----------------------------------------------------------------------------------------");
        return writeMeasurementResult;
    }

    public void runJpaHierarchyWriteTest(EntityManagerWrapper emp, AbstractJpaHierarchyWriter w, ProgressTracker pt) {
        ProgressCombiner combiner = new ProgressCombiner(pt, 1.0f);

        logger.info(w.getName() + " starting using EntityManager " + emp.getCaption());
        pt.setProgressLabel("Deleting customers");
        List<Product> productList = createProductList();
        writeProducts(emp.getEm(), productList);
        pt.setProgressLabel("Deleting customers");
        deleteWriteTestCustomers(emp.getEm(), combiner.createSubProgress(0.2f));
        emp.getEm().clear();
        logger.info("----------------------------------------------------------------------------------------");
        List<Customer> customers = createCustomers("W", numWriteCustomers, NUM_CUSTOMER_ORDERS, NUM_ORDER_LINES,
                getHighestOrderNo(emp.getEm()) + 1, productList);
        emp.clearSqlLog();
        pt.setProgressLabel("Writing customers");
        w.startMeasureTime();
        w.write(emp.getEm(), customers, combiner.createSubProgress(0.8f));
        w.stopMeasureTime();
        for (String s : emp.fetchSqlLog()) {
            logger.debug("    ###SQL###: " + s);
        }
        logger.info(w.getName() + " ready in " + w.getMeasuredTime() + " ms using EntityManager " + emp.getCaption());
        logger.info("Customer count=" + countCustomers(emp.getEm()) + ", OrderLines count=" + countOrderLines(emp.getEm()));
        emp.getEm().clear();
    }

    public void deleteAllDatabases() {
        deleteAllDatabases(dbConfig.getEntityManagerWrappers());
    }

    public void deleteAllDatabases(List<EntityManagerWrapper> el) {
        for (EntityManagerWrapper emp : el) {
            deleteAllCustomers(emp.getEm());
        }
    }

    public void prepareDatabasesForReadTests() {
        prepareDatabasesForReadTests(dbConfig.getEntityManagerWrappersPrepareForReaders());
    }

    public void prepareDatabasesForReadTests(List<EntityManagerWrapper> el) {
        for (EntityManagerWrapper emp : el) {
            if (!readTestDatasetIsPresent(emp)) {
                createReadTestDataset(emp);
            } else {
                logger.info("Reusing existing read test dataset for entity manager " + emp.getCaption());
            }
        }
    }

    private boolean readTestDatasetIsPresent(EntityManagerWrapper emp) {
        emp.getEm().getTransaction().begin();
        long count;
        try {
            count = (Long) emp.getEm().createQuery("select count(cu) from Customer cu where cu.customerNo like 'R%'").getSingleResult();
            if (count != numReadCustomers) {
                return false;
            }
            count = (Long) emp.getEm().createQuery("select count(co) from Customer cu,CustomerOrder co where co.customer=cu and cu.customerNo like 'R%'").getSingleResult();
            if (count != numReadCustomers * NUM_CUSTOMER_ORDERS) {
                return false;
            }
            count = (Long) emp.getEm().createQuery("select count(ol) from Customer cu,CustomerOrder co,OrderLine ol where co.customer=cu and ol.customerOrder=co and cu.customerNo like 'R%'").getSingleResult();
            if (count != numReadCustomers * NUM_CUSTOMER_ORDERS * NUM_ORDER_LINES) {
                return false;
            }
            count = (Long) emp.getEm().createQuery("select count(p) from Product p").getSingleResult();
            if (count != NUM_PRODUCTS) {
                return false;
            }
        } catch (RollbackException e) {
            logger.error(e);
        } finally {
            emp.getEm().getTransaction().rollback();
        }
        return true;
    }

    public void createReadTestDataset(EntityManagerWrapper emp) {
        logger.info("Creating read test dataset for EntityManager " + emp.getCaption());
        logger.debug("Deleting all");
        deleteAllCustomers(emp.getEm());
        List<Product> productList = createProductList();
        writeProducts(emp.getEm(), productList);
        JdbcWriter writer = new JdbcWriter();
        writer.write(emp.getEm(), createCustomers("R", numReadCustomers, NUM_CUSTOMER_ORDERS, NUM_ORDER_LINES, 1, productList), new EmptyProgressTracker());
        logger.info("Created read test dataset for EntityManager " + emp.getCaption());
    }

    public void runHierarchyReadTests(
            List<EntityManagerWrapper> empList,
            List<? extends AbstractJpaHierarchyReader> jpaReaders,
            String customerNoPattern, Map<String,
            List<Integer>> readMeasurementResult) {
        for (EntityManagerWrapper emp : empList) {
            for (AbstractJpaHierarchyReader r : jpaReaders) {
                boolean error = runJpaHierarchyReadTest(customerNoPattern, emp, r, new EmptyProgressTracker()); // todo pt
                addMeasurementResult(readMeasurementResult, r.getName(), error ? -1 : r.getMeasuredTime());
            }
        }
        logger.info("----------------------------------------------------------------------------------------");
    }

    public boolean runJpaHierarchyReadTest(String customerNoPattern,
                                           EntityManagerWrapper emp,
                                           AbstractJpaHierarchyReader r,
                                           ProgressTracker pt) {
        if (!readTestDatasetIsPresent(emp)) {
            createReadTestDataset(emp);
        } else {
            logger.info("Reusing existing read test dataset for entity manager " + emp.getCaption());
        }
        logger.info("----------------------------------------------------------------------------------------");
        r.startMeasureTime();
        Set<Customer> customers;
        ReadResult result = null;
        boolean error = false;
        try {
            customers = r.read(emp.getEm(), customerNoPattern);
            result = getReadResult(customers);
        } catch (RuntimeException e) {
            logger.info("Reader failed", e);
            error = true;
        }
        r.stopMeasureTime();
        logger.info(r.getName() + " ready in " + r.getMeasuredTime() + " ms using EntityManager " + emp.getCaption());
        logger.info("Result=" + result);
        return error;
    }

    public int getHighestOrderNo(EntityManager em) {
        em.getTransaction().begin();
        long n = (Long) em.createQuery("select count(co) from CustomerOrder co").getSingleResult();
        em.getTransaction().commit();
        return (int) n;
    }

    public void printMeasurementResult(Map<String, List<Integer>> measurementResult, List<EntityManagerWrapper> empList, int numEntities) {
        System.out.println("-----------------------------------------------------------------------------------------");
        StringBuilder sbTitle = new StringBuilder();
        for (EntityManagerWrapper emp : empList) {
            sbTitle.append(String.format("%20s", emp.getCaption()));
        }
        System.out.println(String.format("%-20s %s", "", sbTitle.toString()));

        for (Map.Entry<String, List<Integer>> e : measurementResult.entrySet()) {
            String moduleName = e.getKey();
            List<Integer> list = e.getValue();
            StringBuilder sb = new StringBuilder();
            for (int measuredTime : list) {
                sb.append(String.format("%20s", String.format("%d ms (%.0f/s)", measuredTime, (numEntities / (measuredTime / 1000.0)))));
            }
            System.out.println(String.format("%-20s %s", moduleName, sb.toString()));
        }
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println();
    }

    private static void addMeasurementResult(Map<String, List<Integer>> measurementResult,
                                             String name,
                                             int accumulatedWriteTime) {
        List<Integer> list = measurementResult.get(name);
        if (list == null) {
            list = new ArrayList<Integer>();
            measurementResult.put(name, list);
        }
        list.add(accumulatedWriteTime);
    }

    private static ReadResult getReadResult(Set<Customer> customers) {
        int numCustomerOrders = 0;
        int numOrderLines = 0;
        Set<Product> products = new HashSet<Product>();
        for (Customer c : customers) {
            for (CustomerOrder order : c.getCustomerOrders()) {
                ++numCustomerOrders;
                for (OrderLine line : order.getOrderLines()) {
                    ++numOrderLines;
                    products.add(line.getProduct());
                }
            }
        }
        return new ReadResult(products, customers.size(), numCustomerOrders, numOrderLines);
    }

    public static void deleteAllCustomers(EntityManager em) {
        logger.debug("deleteAllCustomers,enter");
        em.getTransaction().begin();
        em.createQuery("delete from OrderLine").executeUpdate();
        em.createQuery("delete from CustomerOrder").executeUpdate();
        em.createQuery("delete from Customer").executeUpdate();
        em.getTransaction().commit();
        logger.debug("deleteAllCustomers,leave");
    }

    public static void deleteWriteTestCustomers(EntityManager em, ProgressTracker pt) {
        logger.debug("Deleting write test customers");
        em.getTransaction().begin();
        deleteJpaCascade(em, pt);
//        deleteJpql(em, pt);
        try {
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.info("deleteJpaCascade failed, trying deep delete: " + e.getMessage());
            em.getTransaction().begin();
            deleteJpaRecurseChildren(em, pt);
            try {
                em.getTransaction().commit();
            } catch (Exception e1) {
                logger.info("deleteJpaRecurseChildren failed, trying multiple step deep delete: " + e.getMessage());
                deleteJpaRecurseChildrenMultipleTransactions(em);
                try {
                    em.getTransaction().commit();
                } catch (Exception e2) {
                    logger.info("deleteJpaRecurseChildrenMultipleTransactions also failed, trying delete with JPQL: " + e.getMessage());
                    em.getTransaction().begin();
                    deleteJpql(em, pt);
                    em.getTransaction().commit();
                }
                logger.info("Deleted customers");
            }
        }
        long n = countWriteCustomers(em);
        if (n > 0) {
            logger.error("Delete silently failed, still customer objects left in DB:" + n);
            throw new RuntimeException("Delete silently failed, still customer objects left in DB:" + n);
        }
        logger.info("Deleted write test customers");
    }

    private static long countWriteCustomers(EntityManager em) {
        try {
            return em.createQuery("select count(cu) from Customer cu where cu.customerNo like 'W%'", Long.class).getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }

    private static void deleteJpql(EntityManager em, ProgressTracker pt) {
        List<Customer> cuList = em.createQuery("select cu from Customer cu where cu.customerNo like 'W%'", Customer.class)
                .getResultList();
        if (!cuList.isEmpty()) {
            List<CustomerOrder> coList = em.createQuery(
                    "select co from CustomerOrder co where co.customer in :cuList", CustomerOrder.class)
                    .setParameter("cuList", cuList)
                    .getResultList();
            em.createQuery("delete from OrderLine ol where ol.customerOrder in :coList")
                    .setParameter("coList", coList)
                    .executeUpdate();
            em.createQuery("delete from CustomerOrder co where co.customer in :cuList")
                    .setParameter("cuList", cuList)
                    .executeUpdate();
//            em.createQuery("delete from Customer cu where cu.customerNo like 'W%'")
//                    .executeUpdate();
            deleteJpaCascade(em, pt);
        }
    }

    private static void deleteJpaRecurseChildrenMultipleTransactions(EntityManager em) {
        int count = 0;
        em.getTransaction().begin();
        List<Customer> customers = Customer.readSubTreeByCustomerNoPattern(em, "W%");
        for (Customer cu : customers) {
            for (CustomerOrder co : cu.getCustomerOrders()) {
                for (OrderLine ol : co.getOrderLines()) {
                    em.remove(ol);
                    ++count;
                }
            }
        }
        em.getTransaction().commit();
        logger.info("Deleted orderLines: " + count);

        count = 0;
        em.getTransaction().begin();
        customers = Customer.readSubTreeByCustomerNoPattern(em, "W%");
        for (Customer cu : customers) {
            for (CustomerOrder co : cu.getCustomerOrders()) {
                em.remove(co);
                ++count;
            }
        }
        em.getTransaction().commit();
        logger.info("Deleted customerOrders: " + count);

        count = 0;
        em.getTransaction().begin();
        customers = Customer.readSubTreeByCustomerNoPattern(em, "W%");
        for (Customer cu : customers) {
            em.remove(cu);
            ++count;
        }
        logger.info("Deleted customers: " + count);
    }

    private static void deleteJpaRecurseChildren(EntityManager em, ProgressTracker pt) {
        List<Customer> customers;
        int count = 0;
        customers = Customer.readSubTreeByCustomerNoPattern(em, "W%");
        for (Customer cu : customers) {
            for (CustomerOrder co : cu.getCustomerOrders()) {
                for (OrderLine ol : co.getOrderLines()) {
                    em.remove(ol);
                }
                em.remove(co);
            }
            em.remove(cu);
            pt.setTotalProgress(++count / (float) customers.size());
        }
    }

    private static void deleteJpaCascade(EntityManager em, ProgressTracker pt) {
        List<Customer> customers = Customer.readByCustomerNoPattern(em, "W%");
        int count = 0;
        for (Customer cu : customers) {
            em.remove(cu);
            pt.setTotalProgress(++count / (float) customers.size());
        }
        logger.debug("deleteJpaCascade,deleted:" + count);
    }

    public List<Customer> createCustomers(String customerNoPrefix, int numCustomers, int numOrders, int numOrderLines,
                                          int startOrderNo, List<Product> productList) {
        int orderLineCount = 0;
        int orderNo = startOrderNo;
        List<Customer> result = new ArrayList<Customer>();
        for (int custNo = 0; custNo < numCustomers; ++custNo) {
            Customer c = new Customer(String.format("%s-%08d", customerNoPrefix, custNo));
            for (int i = 0; i < numOrders; ++i) {
                CustomerOrder order = new CustomerOrder(String.format("%08d", orderNo++));
                c.addCustomerOrder(order);
                for (int orderLineNo = 0; orderLineNo < numOrderLines; ++orderLineNo) {
                    Product p = productList.get(orderLineNo % productList.size());
                    OrderLine line = new OrderLine(p, 1);
                    order.addOrderLine(line);
                    ++orderLineCount;
                }
            }
            result.add(c);
        }
        logger.info("created Customers: orderLineCount=" + orderLineCount);
        return result;
    }

    private void writeProducts(EntityManager em, List<Product> productList) {
        em.getTransaction().begin();
        for (Product p : productList) {
            if (findProduct(em, p.getProductNo()) == null) {
                em.persist(p);
            }
        }
        em.getTransaction().commit();
        em.clear();
    }

    private List<Product> createProductList() {
        List<Product> result = new ArrayList<Product>();
        for (int i = 0; i < NUM_PRODUCTS; ++i) {
            String productNo = String.format("PN-%04d", (i + 1));
            String description = String.format("Product #%d", (i + 1));
            Product p = new Product(productNo, description);
            result.add(p);
        }
        return result;
    }

    public void runNonJpaWriteTests(List<? extends AbstractHierarchyReaderWriter> nonJpaReadersWriters, Map<String, List<Integer>> writeMeasurementResult) {
        List<Product> productList = createProductList();
        for (AbstractHierarchyReaderWriter rw : nonJpaReadersWriters) {
            logger.info("----------------------------------------------------------------------------------------");
            rw.prepareForWrite();
            rw.startMeasureTime();
            rw.write(createCustomers("NJ", numWriteCustomers, NUM_CUSTOMER_ORDERS, NUM_ORDER_LINES, 1, productList));
            rw.stopMeasureTime();
            addMeasurementResult(writeMeasurementResult, rw.getName(), rw.getMeasuredTime());
            logger.info(rw.getName() + " ready writing in " + rw.getMeasuredTime() + " ms");
        }
        logger.info("----------------------------------------------------------------------------------------");
    }

    public void runNonJpaReadTests(List<? extends AbstractHierarchyReaderWriter> nonJpaReadersWriters,
                                   String customerNoPattern, Map<String, List<Integer>> readMeasurementResult) {
        List<Product> productList = createProductList();
        for (AbstractHierarchyReaderWriter rw : nonJpaReadersWriters) {
            logger.info("----------------------------------------------------------------------------------------");
            rw.prepareForWrite();
            rw.write(createCustomers("NJ", numReadCustomers, NUM_CUSTOMER_ORDERS, NUM_ORDER_LINES, 1, productList));
            rw.startMeasureTime();
            Set<Customer> customers;
            ReadResult result = null;
            boolean error = false;
            try {
                customers = rw.read(customerNoPattern);
                result = getReadResult(customers);
            } catch (RuntimeException e) {
                logger.info("Reader failed", e);
                error = true;
            }
            rw.stopMeasureTime();
            addMeasurementResult(readMeasurementResult, rw.getName(), error ? -1 : rw.getMeasuredTime());
            logger.info(rw.getName() + " ready reading in " + rw.getMeasuredTime() + " ms");
            logger.info("Result=" + result);
        }
        logger.info("----------------------------------------------------------------------------------------");
    }

    private static Product findProduct(EntityManager em, String productNo) {
        try {
            return em.createQuery("select p from Product p where p.productNo = :productNo", Product.class)
                    .setParameter("productNo", productNo)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    private static long countOrderLines(EntityManager em) {
        em.getTransaction().begin();
        long count = em.createQuery("select count(line) from OrderLine line", Long.class).getSingleResult();
        em.getTransaction().commit();
        return count;
    }

    private static long countCustomers(EntityManager em) {
        em.getTransaction().begin();
        long count = em.createQuery("select count(c) from Customer c", Long.class).getSingleResult();
        em.getTransaction().commit();
        return count;
    }

    private static void startProxySocketServer(final int serverPort, final String connectHost, final int connectPort, final int packetDelay) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                ProxySocketServer server = new ProxySocketServer(serverPort, connectHost, connectPort, packetDelay);
                try {
                    server.serve();
                } catch (IOException e) {
                    logger.error("Socket server failed with exception:" + e.getMessage());
                }
            }
        });
        t.setDaemon(true);
        t.start();
        logger.info("Started ProxySocketServer,serverPort=" + serverPort + ",connectHost=" + connectHost +
                ",connectPort=" + connectPort + ",packetDelay=" + packetDelay + " ms");
    }

    public AbstractJpaHierarchyReader getHierarchyReader(String readerName) {
        for (JmdtCompetitorPlugin p : competitorPlugins) {
            for (AbstractJpaHierarchyReader r : p.getHierarchyReaderCompetitors()) {
                if (r.getName().equals(readerName)) {
                    return r;
                }
            }
        }
        return null;
    }

    public AbstractJpaHierarchyWriter getHierarchyWriter(String writerName) {
        for (JmdtCompetitorPlugin p : competitorPlugins) {
            for (AbstractJpaHierarchyWriter w : p.getHierarchyWriterCompetitors()) {
                if (w.getName().equals(writerName)) {
                    return w;
                }
            }
        }
        return null;
    }
}
