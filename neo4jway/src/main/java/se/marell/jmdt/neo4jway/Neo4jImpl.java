/*
 * Created by Daniel Marell 12-12-21 10:44 PM
 */
package se.marell.jmdt.neo4jway;

import entity.Customer;
import entity.CustomerOrder;
import entity.OrderLine;
import entity.Product;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.Traversal;
import se.marell.jmdt.commons.AbstractHierarchyReaderWriter;

import java.util.*;

public class Neo4jImpl extends AbstractHierarchyReaderWriter {
    private CustOrdersDb db;
    private Map<Node, Product> productNodeMap = new HashMap<Node, Product>();
    private Map<String, Node> productNoMap = new HashMap<String, Node>();

    @Override
    public Set<Customer> read(String customerNoPattern) {
        db = new CustOrdersDb();
        productNodeMap.clear();
        productNoMap.clear();
        startIntervalLogging(5000, "Reading neo4j");
        Set<Customer> customers = findCustomersUsingTraverser(customerNoPattern);
        for (Customer c : customers) {
            readCustomerChildren(c);
            intervalLogging();
        }
        db.close();
        return customers;
    }

    private void readCustomerChildren(Customer c) {
        Set<CustomerOrder> orders = new HashSet<CustomerOrder>();
        Node customerNode = db.getNodeForEntity(c);
        for (Relationship cor : customerNode.getRelationships(Direction.OUTGOING)) {
            CustomerOrder customerOrder = db.createCustomerOrder(cor.getEndNode());
            Set<OrderLine> orderLines = new HashSet<OrderLine>();
            Node customerOrderNode = db.getNodeForEntity(customerOrder);
            for (Relationship olr : customerOrderNode.getRelationships(Direction.OUTGOING)) {
                Node orderLineNode = olr.getEndNode();
                Node productNode = orderLineNode.getSingleRelationship(
                        CustOrdersDb.OrderLineRelTypes.PRODUCT, Direction.OUTGOING).getEndNode();
                Product product = productNodeMap.get(productNode);
                if (product == null) {
                    product = db.createProduct(productNode);
                    productNodeMap.put(productNode, product);
                }
                OrderLine orderLine = db.createOrderLine(product, orderLineNode);
                orderLines.add(orderLine);
            }
            customerOrder.setOrderLines(orderLines);
            orders.add(customerOrder);
        }
        c.setCustomerOrders(orders);
    }

    private Set<Customer> findCustomersByIterating(String customerNoPattern) {
        Set<Customer> result = new HashSet<Customer>();
        Node rootNode = db.getGraphDb().getReferenceNode();
        for (Relationship r : rootNode.getRelationships(CustOrdersDb.RootRelTypes.CUSTOMERS, Direction.OUTGOING)) {
            if (isPatternMatching((String) r.getEndNode().getProperty(CustOrdersDb.CUSTOMER_NO_KEY), customerNoPattern)) {
                result.add(db.createCustomer(r.getEndNode()));
            }
        }
        return result;
    }

    private Set<Customer> findCustomersUsingTraverser(String customerNoPattern) {
        Set<Customer> result = new HashSet<Customer>();
        Traverser traverser = getCustomers(db.getGraphDb().getReferenceNode());
        for (Path path : traverser) {
            if (isPatternMatching((String) path.endNode().getProperty(CustOrdersDb.CUSTOMER_NO_KEY), customerNoPattern)) {
                result.add(db.createCustomer(path.endNode()));
            }
        }
        return result;
    }

    private static Traverser getCustomers(Node rootNode) {
        TraversalDescription td = Traversal.description()
                .breadthFirst()
                .relationships(CustOrdersDb.RootRelTypes.CUSTOMERS, Direction.OUTGOING)
                .evaluator(Evaluators.excludeStartPosition());
        return td.traverse(rootNode);
    }

    private boolean isPatternMatching(String customerNo, String pattern) {
        return customerNo.matches(pattern.replace("%", ".*"));
    }

    @Override
    public void prepareForWrite() {
        db = new CustOrdersDb();
        logger.debug("Deleting all in neo4j db");
        db.deleteAll();
        productNodeMap.clear();
        productNoMap.clear();
        logger.debug("Delete ready");
        createProducts();
        db.close();
    }

    @Override
    public void write(List<Customer> customers) {
        db = new CustOrdersDb();
        startIntervalLogging(5000, "Writing neo4j");
        for (Customer c : customers) {
            Transaction tx = db.getGraphDb().beginTx();
            try {
                intervalLogging();
                Node cNode = db.createCustomerNode(c.getCustomerNo());
                for (CustomerOrder o : c.getCustomerOrders()) {
                    Node oNode = db.createCustomerOrderNode(o.getOrderNo(), cNode);
                    for (OrderLine ol : o.getOrderLines()) {
                        Node productNode = productNoMap.get(ol.getProduct().getProductNo());
                        db.createOrderLineNode(ol.getNumItems(), productNode, oNode);
                    }
                }
                tx.success();
            } finally {
                tx.finish();
            }
        }
        logger.info("Ready writing neo4j");
        db.close();
    }

    private void createProducts() {
        final int numProducts = 42;
        Transaction tx = db.getGraphDb().beginTx();
        try {
            for (int i = 0; i < numProducts; ++i) {
                String productNo = String.format("PN-%04d", (i + 1));
                Node p = productNoMap.get(productNo);
                if (p == null) {
                    Node pNode = db.createProductNode(productNo, String.format("Product #%d", (i + 1)));
                    productNoMap.put(productNo, pNode);
                }
            }
            tx.success();
        } finally {
            tx.finish();
        }
    }
}
