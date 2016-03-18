/*
 * Created by Daniel Marell 12-11-28 8:13 AM
 */
package se.marell.jmdt;

import entity.Product;

import java.util.Set;

public class ReadResult {
    private Set<Product> products;
    private int numCustomers;
    private int numCustomerOrders;
    private int numOrderLines;

    public ReadResult(Set<Product> products, int numCustomers, int numCustomerOrders, int numOrderLines) {
        this.products = products;
        this.numCustomers = numCustomers;
        this.numCustomerOrders = numCustomerOrders;
        this.numOrderLines = numOrderLines;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
    }

    public int getNumCustomers() {
        return numCustomers;
    }

    public void setNumCustomers(int numCustomers) {
        this.numCustomers = numCustomers;
    }

    public int getNumCustomerOrders() {
        return numCustomerOrders;
    }

    public void setNumCustomerOrders(int numCustomerOrders) {
        this.numCustomerOrders = numCustomerOrders;
    }

    public int getNumOrderLines() {
        return numOrderLines;
    }

    public void setNumOrderLines(int numOrderLines) {
        this.numOrderLines = numOrderLines;
    }

    @Override
    public String toString() {
        return "ReadResult{" +
                "numProducts=" + products.size() +
                ",numCustomers=" + numCustomers +
                ",numCustomerOrders=" + numCustomerOrders +
                ",numOrderLines=" + numOrderLines +
                '}';
    }
}
