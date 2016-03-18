/*
 * Created by Daniel Marell 12-11-09 8:32 AM
 */
package se.marell.jmdt.presentation;

import entity.Product;

import javax.persistence.*;

@Entity
@Table(name = "LAZYORDERLINE")
public class LazyOrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "num_items", nullable = false)
    private int numItems;

    @ManyToOne
    @JoinColumn(name = "customer_order_id")
    private LazyCustomerOrder customerOrder;

    public LazyOrderLine() {
    }

    public LazyOrderLine(int numItems) {
        this.numItems = numItems;
    }

    public LazyOrderLine(Integer id, int numItems) {
        this.id = id;
        this.numItems = numItems;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNumItems() {
        return numItems;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    public LazyCustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(LazyCustomerOrder order) {
        this.customerOrder = order;
    }

    @Override
    public String toString() {
        return "LazyOrderLine{" +
                "id=" + id +
                ", numItems=" + numItems +
                '}';
    }
}
