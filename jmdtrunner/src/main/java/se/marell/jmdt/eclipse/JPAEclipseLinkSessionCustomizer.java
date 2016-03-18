/*
 * Created by Daniel Marell 12-12-29 1:09 AM
 */
package se.marell.jmdt.eclipse;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.persistence.sessions.Session;

public class JPAEclipseLinkSessionCustomizer implements SessionCustomizer {
    public void customize(Session aSession) throws Exception {

        // create a custom logger
        SessionLog aCustomLogger = new CustomSessionLog();
        aCustomLogger.setLevel(1); // Logging level finest
        aSession.setSessionLog(aCustomLogger);
    }
}