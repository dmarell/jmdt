/*
 * Created by Daniel Marell 13-02-17 12:25 PM
 */
package se.marell.jmdt.presentation;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "LAZYCUSTOMER")
public class LazyCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "customer_no", nullable = false, length = 12, unique = true)
    private String customerNo;

    @OneToMany(mappedBy = "customer")
    private Set<LazyCustomerOrder> customerOrders = new HashSet<LazyCustomerOrder>();

    public LazyCustomer() {
    }

    public LazyCustomer(String customerNo) {
        this.customerNo = customerNo;
    }

    public LazyCustomer(Integer id, String customerNo) {
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


    public Collection<LazyCustomerOrder> getCustomerOrders() {
        return customerOrders;
    }

    public void setCustomerOrders(Set<LazyCustomerOrder> orders) {
        this.customerOrders = orders;
    }

    /**
     * Add an aggregate entity CustomerOrder.
     *
     * @param order Entity to add
     */
    public void addCustomerOrder(LazyCustomerOrder order) {
        customerOrders.add(order);
        order.setCustomer(this);
    }

    @Override
    public String toString() {
        return "LazyCustomer{" +
                "id=" + id +
                ", customerNo='" + customerNo + '\'' +
                '}';
    }
}
