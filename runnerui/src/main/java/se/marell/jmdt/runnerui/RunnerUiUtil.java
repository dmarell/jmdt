/*
 * Created by Daniel Marell 13-01-06 1:29 PM
 */
package se.marell.jmdt.runnerui;

import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;

public final class RunnerUiUtil {
    private RunnerUiUtil() {
    }

    public static void addDropShadow(final Button button) {
        final DropShadow shadow = new DropShadow();
        button.addEventHandler(MouseEvent.MOUSE_ENTERED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        button.setEffect(shadow);
                    }
                });
        button.addEventHandler(MouseEvent.MOUSE_EXITED,
                new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent e) {
                        button.setEffect(null);
                    }
                });
    }
}
