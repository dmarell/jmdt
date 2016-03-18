/*
 * Created by Daniel Marell 12-11-29 9:15 AM
 */
package se.marell.jmdt.dbutilsway;

import entity.Customer;
import entity.CustomerOrder;
import entity.OrderLine;
import entity.Product;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import se.marell.jmdt.commons.AbstractJpaHierarchyReader;
import se.marell.jpatools.EntityManagerConnection;
import se.marell.jpatools.JpaUtils;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DbUtilsReader extends AbstractJpaHierarchyReader {
    public static class DbException extends RuntimeException {
        public DbException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private QueryRunner run = new QueryRunner();

    @Override
    public Set<Customer> read(EntityManager em, String customerNoPattern) {
        em.getTransaction().begin();
        try {
            Set<Customer> customers = getCustomers(em, customerNoPattern);
            em.getTransaction().commit();
            return customers;
        } catch (RuntimeException e) {
            em.getTransaction().rollback();
            throw e;
        }
    }

    private Set<Customer> getCustomers(EntityManager em, String customerNoPattern) {
        EntityManagerConnection conn = JpaUtils.getConnectionFromEntityManager(em);
        Map<Integer, Product> productMap = readProductMap(conn.getConn());
        Map<Integer, Set<OrderLine>> orderLineMap = readOrderLineMap(conn.getConn(), customerNoPattern, productMap);
        Map<Integer, Set<CustomerOrder>> customerOrderMap = readCustomerOrderMap(conn.getConn(), customerNoPattern);
        Set<Customer> customers = readCustomers(conn.getConn(), customerNoPattern);
        for (Customer c : customers) {
            c.setCustomerOrders(customerOrderMap.get(c.getId()));
            for (CustomerOrder co : c.getCustomerOrders()) {
                co.setOrderLines(orderLineMap.get(co.getId()));
            }
        }
        conn.close();
        return customers;
    }

    private Set<Customer> readCustomers(Connection conn, String customerNoPattern) {
        ResultSetHandler<Set<Customer>> rh = new ResultSetHandler<Set<Customer>>() {
            public Set<Customer> handle(ResultSet rs) throws SQLException {
                Set<Customer> customers = new HashSet<Customer>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String customerNo = rs.getString("customer_no");
                    Customer cu = new Customer(id, customerNo);
                    customers.add(cu);
                }
                return customers;
            }
        };
        return runQuery(conn, rh,
                "select cu.id,cu.customer_no from Customer cu where customer_no like ?",
                customerNoPattern);
    }

    private Map<Integer, Set<CustomerOrder>> readCustomerOrderMap(Connection conn, String customerNoPattern) {
        ResultSetHandler<Map<Integer, Set<CustomerOrder>>> rh = new ResultSetHandler<Map<Integer, Set<CustomerOrder>>>() {
            public Map<Integer, Set<CustomerOrder>> handle(ResultSet rs) throws SQLException {
                Map<Integer, Set<CustomerOrder>> result = new HashMap<Integer, Set<CustomerOrder>>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String orderNo = rs.getString("order_no");
                    int customerId = rs.getInt("customer_id");
                    CustomerOrder co = new CustomerOrder(id, orderNo);
                    Set<CustomerOrder> set = result.get(customerId);
                    if (set == null) {
                        set = new HashSet<CustomerOrder>();
                        result.put(customerId, set);
                    }
                    set.add(co);
                }
                return result;
            }
        };
        return runQuery(conn, rh,
                "select co.id,co.order_no,co.customer_id from CustomerOrder co,Customer cu " +
                        "where co.customer_id = cu.id and cu.customer_no like ?",
                customerNoPattern);
    }

    private Map<Integer, Set<OrderLine>> readOrderLineMap(Connection conn,
                                                          String customerNoPattern,
                                                          final Map<Integer, Product> productMap) {
        ResultSetHandler<Map<Integer, Set<OrderLine>>> rh = new ResultSetHandler<Map<Integer, Set<OrderLine>>>() {
            public Map<Integer, Set<OrderLine>> handle(ResultSet rs) throws SQLException {
                Map<Integer, Set<OrderLine>> result = new HashMap<Integer, Set<OrderLine>>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    int productId = rs.getInt("product_id");
                    int numItems = rs.getInt("num_items");
                    int customerOrderId = rs.getInt("customer_order_id");
                    OrderLine ol = new OrderLine(id, productMap.get(productId), numItems);
                    Set<OrderLine> set = result.get(customerOrderId);
                    if (set == null) {
                        set = new HashSet<OrderLine>();
                        result.put(customerOrderId, set);
                    }
                    set.add(ol);
                }
                return result;
            }
        };
        return runQuery(conn, rh,
                "select ol.id,ol.product_id,ol.num_items,ol.customer_order_id,co.customer_id,cu.id,cu.customer_no " +
                        "from OrderLine ol,CustomerOrder co,Customer cu " +
                        "where ol.customer_order_id = co.id and " +
                        "co.customer_id = cu.id and " +
                        "cu.customer_no like ?",
                customerNoPattern);
    }

    private Map<Integer, Product> readProductMap(Connection conn) {
        ResultSetHandler<Map<Integer, Product>> rh = new ResultSetHandler<Map<Integer, Product>>() {
            public Map<Integer, Product> handle(ResultSet rs) throws SQLException {
                Map<Integer, Product> result = new HashMap<Integer, Product>();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String productNo = rs.getString("product_no");
                    String description = rs.getString("description");
                    Product p = new Product(id, productNo, description);
                    result.put(id, p);
                }
                return result;
            }
        };
        return runQuery(conn, rh, "select p.id,p.product_no,p.description from Product p");
    }

    private <T> T runQuery(Connection conn,
                           ResultSetHandler<T> rh,
                           String sql,
                           Object... args) throws DbException {
        try {
            return run.query(conn, sql, rh, args);
        } catch (SQLException e) {
            throw new DbException("runQuery failed", e);
        }
    }
}
