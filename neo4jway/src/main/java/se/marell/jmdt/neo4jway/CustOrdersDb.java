/*
 * Created by Daniel Marell 12-12-23 11:35 AM
 */
package se.marell.jmdt.neo4jway;

import entity.Customer;
import entity.CustomerOrder;
import entity.OrderLine;
import entity.Product;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.tooling.GlobalGraphOperations;

import java.util.HashMap;
import java.util.Map;

public class CustOrdersDb {
    public static enum RootRelTypes implements RelationshipType {
        CUSTOMERS,
        PRODUCTS
    }

    public static enum CustomerRelTypes implements RelationshipType {
        CUSTOMER_ORDERS
    }

    public static enum CustomerOrderRelTypes implements RelationshipType {
        ORDER_LINES
    }

    public static enum OrderLineRelTypes implements RelationshipType {
        PRODUCT
    }

    private Logger logger = Logger.getLogger(CustOrdersDb.class);

    public static final String DB_PATH = "custorders.neo4jdb";

    public static final String CUSTOMER_NO_KEY = "customerNo";

    public static final String ORDER_NO_KEY = "orderNo";

    public static final String NUM_ITEMS_KEY = "numItems";
    public static final String PRODUCT_RELATION_KEY = "products";

    public static final String PRODUCT_NO_KEY = "productNo";
    public static final String DESCRIPTION_KEY = "description";

    private GraphDatabaseService graphDb;
    private Index<Node> customerNoIndex;
    private Index<Node> orderNoIndex;
    private Index<Node> productNoIndex;

    private Map<Object, Node> entityMap = new HashMap<Object, Node>();

    public CustOrdersDb() {
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase(DB_PATH);
        customerNoIndex = graphDb.index().forNodes(CUSTOMER_NO_KEY);
        orderNoIndex = graphDb.index().forNodes(ORDER_NO_KEY);
        productNoIndex = graphDb.index().forNodes(PRODUCT_NO_KEY);
        registerShutdownHook(graphDb);
    }

    public void deleteAll() {
        logger.debug("deleteAll");
        int count = 0;
        Transaction tx = graphDb.beginTx();
        long start = System.currentTimeMillis();
        try {
            Node root = graphDb.getReferenceNode();
            for (Node n : GlobalGraphOperations.at(graphDb).getAllNodes()) {
                for (Relationship r : n.getRelationships()) {
                    r.delete();
                }
                if (!n.equals(root)) {
                    n.delete();
                }

                ++count;
                if (start - System.currentTimeMillis() >= 5000) {
                    logger.debug("Deleting, count=" + count);
                    start = System.currentTimeMillis();
                }
                tx.success();
                tx.finish();
                tx = graphDb.beginTx();
            }
        } finally {
            logger.debug("deleteAll finishing");
            tx.finish();
        }
    }

    public GraphDatabaseService getGraphDb() {
        return graphDb;
    }

    public Node getNodeForEntity(Object entity) {
        return entityMap.get(entity);
    }

    private Node findCustomerNode(String customerNo) {
        return customerNoIndex.get(CUSTOMER_NO_KEY, customerNo).getSingle();
    }

    public Node createCustomerNode(String customerNo) {
        Node node = graphDb.createNode();
        node.setProperty(CUSTOMER_NO_KEY, customerNo);
        customerNoIndex.add(node, CUSTOMER_NO_KEY, customerNo);
        graphDb.getReferenceNode().createRelationshipTo(node, RootRelTypes.CUSTOMERS);
        return node;
    }

    public Customer createCustomer(Node node) {
        Customer entity = new Customer((String) node.getProperty(CUSTOMER_NO_KEY));
        entityMap.put(entity, node);
        return entity;
    }

    public Node createCustomerOrderNode(String orderNo, Node customerNode) {
        Node node = graphDb.createNode();
        node.setProperty(ORDER_NO_KEY, orderNo);
        orderNoIndex.add(node, ORDER_NO_KEY, orderNo);
        customerNode.createRelationshipTo(node, CustomerRelTypes.CUSTOMER_ORDERS);
        return node;
    }

    public CustomerOrder createCustomerOrder(Node node) {
        CustomerOrder entity = new CustomerOrder((String) node.getProperty(ORDER_NO_KEY));
        entityMap.put(entity, node);
        return entity;
    }

    public Node createOrderLineNode(int numItems, Node productNode, Node customerOrderNode) {
        Node node = graphDb.createNode();
        node.setProperty(NUM_ITEMS_KEY, numItems);
        node.createRelationshipTo(productNode, OrderLineRelTypes.PRODUCT);
        customerOrderNode.createRelationshipTo(node, CustomerOrderRelTypes.ORDER_LINES);
        return node;
    }

    public OrderLine createOrderLine(Product product, Node node) {
        OrderLine entity = new OrderLine(product, (Integer) node.getProperty(NUM_ITEMS_KEY));
        entityMap.put(entity, node);
        return entity;
    }

    public Node createProductNode(String productNo, String description) {
        Node node = graphDb.createNode();
        node.setProperty(PRODUCT_NO_KEY, productNo);
        node.setProperty(DESCRIPTION_KEY, description);
        graphDb.getReferenceNode().createRelationshipTo(node, RootRelTypes.PRODUCTS);
        productNoIndex.add(node, PRODUCT_NO_KEY, productNo);
        return node;
    }

    public Product createProduct(Node node) {
        Product entity = new Product((String) node.getProperty(PRODUCT_NO_KEY), (String) node.getProperty(DESCRIPTION_KEY));
        entityMap.put(entity, node);
        return entity;
    }

    public void close() {
        graphDb.shutdown();
    }

    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }
}
