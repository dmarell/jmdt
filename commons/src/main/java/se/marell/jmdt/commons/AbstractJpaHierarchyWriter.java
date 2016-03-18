/*
 * Created by Daniel Marell 12-11-11 9:58 PM
 */
package se.marell.jmdt.commons;

import entity.Customer;
import entity.Product;
import se.marell.dcommons.progress.ProgressTracker;

import javax.persistence.EntityManager;
import java.util.List;

public abstract class AbstractJpaHierarchyWriter extends AbstractJmdtCompetitor {
    protected List<Product> products;

    protected AbstractJpaHierarchyWriter() {
        super(JmdtSuite.HIERARCHY_WRITE);
    }

    protected void writeInit() {
        products = null;
    }

    public abstract void write(EntityManager em, List<Customer> customers, ProgressTracker pt);

    protected Product findProduct(EntityManager em, String productNo) {
        for (Product p : getProducts(em)) {
            if (p.getProductNo().equals(productNo)) {
                return p;
            }
        }
        return null;
    }

    protected List<Product> getProducts(EntityManager em) {
        if (products == null) {
            products = em.createQuery("select p from Product p", Product.class).getResultList();
        }
        return products;
    }
}
