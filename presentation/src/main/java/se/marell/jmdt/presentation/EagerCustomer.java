/*
 * Created by Daniel Marell 13-02-17 12:25 PM
 */
package se.marell.jmdt.presentation;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "EAGERCUSTOMER")
public class EagerCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "customer_no", nullable = false, length = 12, unique = true)
    private String customerNo;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer")
    private Set<EagerCustomerOrder> customerOrders = new HashSet<>();

    public EagerCustomer() {
    }

    public EagerCustomer(String customerNo) {
        this.customerNo = customerNo;
    }

    public EagerCustomer(Integer id, String customerNo) {
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


    public Collection<EagerCustomerOrder> getCustomerOrders() {
        return customerOrders;
    }

    public void setCustomerOrders(Set<EagerCustomerOrder> orders) {
        this.customerOrders = orders;
    }

    @Override
    public String toString() {
        return "EagerCustomer{" +
                "id=" + id +
                ", customerNo='" + customerNo + '\'' +
                '}';
    }
}
