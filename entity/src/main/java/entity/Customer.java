/*
 * Created by Daniel Marell 12-11-09 8:32 AM
 */
package entity;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NamedQueries({
        @NamedQuery(
                name = "Customer.readAll",
                query = "select c from Customer c"),
        @NamedQuery(
                name = "Customer.readByCustomerNoPattern",
                query = "select distinct c from Customer c where c.customerNo like :customerNoPattern"),
        @NamedQuery(
                name = "Customer.readSubtree",
                query = "select distinct c from Customer c join fetch c.customerOrders where c.customerNo like :customerNoPattern"),
        @NamedQuery(
                name = "Customer.readSubtree2JoinFetch",
                query = "select distinct c from Customer c " +
                        "join fetch c.customerOrders as co " +
                        "join fetch co.orderLines " +
                        "where c.customerNo like :customerNoPattern")
})
@Entity
@Table(name = "CUSTOMER")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "customer_no", nullable = false, length = 12, unique = true)
    private String customerNo;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "customer")
    private Set<CustomerOrder> customerOrders = new HashSet<CustomerOrder>();

    public Customer() {
    }

    public Customer(String customerNo) {
        this.customerNo = customerNo;
    }

    public Customer(Integer id, String customerNo) {
        this.id = id;
        this.customerNo = customerNo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }


    public Collection<CustomerOrder> getCustomerOrders() {
        return customerOrders;
    }

    public void setCustomerOrders(Set<CustomerOrder> orders) {
        this.customerOrders = orders;
    }

    /**
     * Add an aggregate entity CustomerOrder.
     *
     * @param order Entity to add
     */
    public void addCustomerOrder(CustomerOrder order) {
        customerOrders.add(order);
        order.setCustomer(this);
    }

    @Override
    public String toString() {
        return "BaseCustomer{" +
                "id=" + id +
                ", customerNo='" + customerNo + '\'' +
                '}';
    }

    public static List<Customer> readAll(EntityManager em) {
        return em.createNamedQuery("Customer.readAll", Customer.class).getResultList();
    }

    public static List<Customer> readByCustomerNoPattern(EntityManager em, String customerNoPattern) {
        return em.createNamedQuery("Customer.readByCustomerNoPattern", Customer.class)
                .setParameter("customerNoPattern", customerNoPattern).getResultList();
    }

    public static List<Customer> readSubTreeByCustomerNoPattern(EntityManager em, String customerNoPattern) {
        return em.createNamedQuery("Customer.readSubtree", Customer.class)
                .setParameter("customerNoPattern", customerNoPattern).getResultList();
    }

    public static List<Customer> readSubTree2JoinFetchByCustomerNoPattern(EntityManager em, String customerNoPattern) {
        return em.createNamedQuery("Customer.readSubtree2JoinFetch", Customer.class)
                .setParameter("customerNoPattern", customerNoPattern).getResultList();
    }
}
