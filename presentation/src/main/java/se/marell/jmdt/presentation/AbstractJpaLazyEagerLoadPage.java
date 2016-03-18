/*
 * Created by Daniel Marell 13-02-17 12:37 PM
 */
package se.marell.jmdt.presentation;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import se.marell.jmdt.EntityManagerWrapper;

import javax.persistence.NoResultException;
import java.util.List;

public class AbstractJpaLazyEagerLoadPage extends PresentationPane {
    protected DeepReadResult createDeepReadResult(final EntityManagerWrapper emw, final boolean lazy) {
        final DeepReadResult data = new DeepReadResult(emw.getCaption());

        data.setButtonListener(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                //data.clearResult();
                long start = System.currentTimeMillis();
                final int numReads = 1000;
                int readCount = 0;
                emw.clearSqlLog();
                emw.getEm().getTransaction().begin();
                for (int i = 0; i < numReads; ++i) {
                    readCount += deepRead(emw, lazy);
                }
                emw.getEm().getTransaction().commit();
                emw.getEm().clear();
                List<String> sqlLog = emw.fetchSqlLog();
                int numSelects = countCommands("select", sqlLog);
                data.setNumReads("" + readCount);
                data.setNumSelects("" + numSelects);
                data.setTime("" + (System.currentTimeMillis() - start));
            }
        });
        return data;
    }

    protected int countCommands(String text, List<String> strings) {
        text = text.toLowerCase();
        int count = 0;
        for (String s : strings) {
            if (s.toLowerCase().contains(text)) {
                ++count;
            }
        }
        return count;
    }

    protected int deepRead(EntityManagerWrapper emw, boolean lazy) {
        int count = 0;
        try {
            String customerNo = "R-00000012";
            if (lazy) {
                LazyCustomer cu = emw.getEm().createQuery("select cu from LazyCustomer cu where cu.customerNo = :customerNo", LazyCustomer.class)
                        .setParameter("customerNo", customerNo)
                        .getSingleResult();
                for (LazyCustomerOrder co : cu.getCustomerOrders()) {
                    count += co.getOrderLines().size();
                }
            } else {
                EagerCustomer cu = emw.getEm().createQuery("select cu from EagerCustomer cu where cu.customerNo = :customerNo", EagerCustomer.class)
                        .setParameter("customerNo", customerNo)
                        .getSingleResult();
                for (EagerCustomerOrder co : cu.getCustomerOrders()) {
                    count += co.getOrderLines().size();
                }
            }
        } catch (NoResultException e) {
            return 0;
        }
        return count;
    }
}
