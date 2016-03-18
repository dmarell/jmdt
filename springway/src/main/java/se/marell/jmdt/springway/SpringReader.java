/*
 * Created by Daniel Marell 12-12-08 3:59 PM
 */
package se.marell.jmdt.springway;

import entity.Customer;
import entity.CustomerOrder;
import entity.OrderLine;
import entity.Product;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import se.marell.jmdt.commons.AbstractJpaHierarchyReader;
import se.marell.jpatools.EntityManagerConnection;
import se.marell.jpatools.JpaUtils;

import javax.persistence.EntityManager;
import java.util.*;

public class SpringReader extends AbstractJpaHierarchyReader {
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
        SimpleJdbcTemplate simpleJdbcTemplate = new SimpleJdbcTemplate(new ConnectionWrapperDataSource(conn.getConn()));
        Map<Integer, Product> productMap = readProductMap(simpleJdbcTemplate);
        Map<Integer, Set<OrderLine>> orderLineMap = readOrderLineMap(simpleJdbcTemplate, customerNoPattern, productMap);
        Map<Integer, Set<CustomerOrder>> customerOrderMap = readCustomerOrderMap(simpleJdbcTemplate, customerNoPattern);
        Set<Customer> customers = readCustomers(simpleJdbcTemplate, customerNoPattern);
        for (Customer c : customers) {
            c.setCustomerOrders(customerOrderMap.get(c.getId()));
            for (CustomerOrder co : c.getCustomerOrders()) {
                Set<OrderLine> set = orderLineMap.get(co.getId());
                assert set != null;
                co.setOrderLines(set);
            }
        }
        conn.close();
        return customers;
    }

    private Set<Customer> readCustomers(SimpleJdbcTemplate simpleJdbcTemplate, String customerNoPattern) {
        Set<Customer> customers = new HashSet<Customer>();
        List<Map<String, Object>> rsMap = simpleJdbcTemplate.queryForList(
                "select * from Customer cu where customer_no like ?",
                customerNoPattern);
        for (Map<String, Object> objectMap : rsMap) {
            Integer id = (Integer) objectMap.get("id");
            String customerNo = (String) objectMap.get("customer_no");
            Customer cu = new Customer(id, customerNo);
            customers.add(cu);
        }
        return customers;
    }

    private Map<Integer, Set<CustomerOrder>> readCustomerOrderMap(SimpleJdbcTemplate simpleJdbcTemplate, String customerNoPattern) {
        Map<Integer, Set<CustomerOrder>> result = new HashMap<Integer, Set<CustomerOrder>>();
        List<Map<String, Object>> rsMap = simpleJdbcTemplate.queryForList(
                "select co.id,co.order_no,co.customer_id from CustomerOrder co,Customer cu " +
                        "where co.customer_id = cu.id and cu.customer_no like ?",
                customerNoPattern);
        for (Map<String, Object> objectMap : rsMap) {
            int id = (Integer) objectMap.get("id");
            String orderNo = (String) objectMap.get("order_no");
            int customerId = (Integer) objectMap.get("customer_id");
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

    private Map<Integer, Set<OrderLine>> readOrderLineMap(SimpleJdbcTemplate simpleJdbcTemplate,
                                                          String customerNoPattern,
                                                          final Map<Integer, Product> productMap) {
        Map<Integer, Set<OrderLine>> result = new HashMap<Integer, Set<OrderLine>>();
        List<Map<String, Object>> rsMap = simpleJdbcTemplate.queryForList(
                "select ol.id,ol.product_id,ol.num_items,ol.customer_order_id,co.customer_id,cu.id,cu.customer_no " +
                        "from OrderLine ol,CustomerOrder co,Customer cu " +
                        "where ol.customer_order_id = co.id and " +
                        "co.customer_id = cu.id and " +
                        "cu.customer_no like ?",
                customerNoPattern);
        for (Map<String, Object> objectMap : rsMap) {
            int id = (Integer) objectMap.get("id");
            int productId = (Integer) objectMap.get("product_id");
            int numItems = (Integer) objectMap.get("num_items");
            int customerOrderId = (Integer) objectMap.get("customer_order_id");
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

    private Map<Integer, Product> readProductMap(SimpleJdbcTemplate simpleJdbcTemplate) {
        Map<Integer, Product> result = new HashMap<Integer, Product>();
        List<Map<String, Object>> rsMap = simpleJdbcTemplate.queryForList(
                "select p.id,p.product_no,p.description from Product p");
        for (Map<String, Object> objectMap : rsMap) {
            int id = (Integer) objectMap.get("id");
            String productNo = (String) objectMap.get("product_no");
            String description = (String) objectMap.get("description");
            Product p = new Product(id, productNo, description);
            result.put(id, p);
        }
        return result;
    }
}
