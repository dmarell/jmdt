/*
 * Created by Daniel Marell 12-11-20 7:30 AM
 */
package se.marell.jmdt.jdbcway;

import entity.*;
import se.marell.jmdt.commons.AbstractJpaHierarchyReader;
import se.marell.jpatools.EntityManagerConnection;
import se.marell.jpatools.JpaUtils;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JdbcReader extends AbstractJpaHierarchyReader {
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
        conn.getConn();
        return customers;
    }

    private Set<Customer> readCustomers(Connection conn, String customerNoPattern) {
        Set<Customer> result = new HashSet<Customer>();
        String sql = "select cu.id,cu.customer_no from Customer cu where customer_no like ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, customerNoPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String customerNo = rs.getString("customer_no");
                Customer cu = new Customer(id, customerNo);
                result.add(cu);
            }
        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
        return result;
    }

    private Map<Integer, Set<CustomerOrder>> readCustomerOrderMap(Connection conn, String customerNoPattern) {
        Map<Integer, Set<CustomerOrder>> result = new HashMap<Integer, Set<CustomerOrder>>();
        String sql = "select co.id,co.order_no,co.customer_id,cu.id from CustomerOrder co,Customer cu where co.customer_id = cu.id and cu.customer_no like ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, customerNoPattern);
            ResultSet rs = ps.executeQuery();
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
        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
        return result;
    }

    private Map<Integer, Set<OrderLine>> readOrderLineMap(Connection conn, String customerIdPattern, Map<Integer, Product> productMap) {
        Map<Integer, Set<OrderLine>> result = new HashMap<Integer, Set<OrderLine>>();
        String sql =
                "select ol.id,ol.product_id,ol.num_items,ol.customer_order_id,co.id,co.customer_id,cu.id,cu.customer_no " +
                        "from OrderLine ol,CustomerOrder co,Customer cu " +
                        "where ol.customer_order_id = co.id and " +
                        "co.customer_id = cu.id and " +
                        "cu.customer_no like ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, customerIdPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int product_id = rs.getInt("product_id");
                int num_items = rs.getInt("num_items");
                int customer_order_id = rs.getInt("customer_order_id");
                OrderLine ol = new OrderLine(id, productMap.get(product_id), num_items);
                Set<OrderLine> set = result.get(customer_order_id);
                if (set == null) {
                    set = new HashSet<OrderLine>();
                    result.put(customer_order_id, set);
                }
                set.add(ol);
            }
        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
        return result;
    }

    private Map<Integer, Product> readProductMap(Connection conn) {
        Map<Integer, Product> result = new HashMap<Integer, Product>();
        String sql = "select p.id,p.product_no,p.description from Product p";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String product_no = rs.getString("product_no");
                String description = rs.getString("description");
                Product p = new Product(id, product_no, description);
                result.put(id, p);
            }
        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
        return result;
    }
}
