package org.autoride.driver.driver.net.invokers;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.driver.net.WSConstants;
import org.autoride.driver.driver.net.WebConnector;
import org.autoride.driver.driver.net.parsers.DriverStatusParser;

public class DriverStatusInvoker extends BaseInvoker implements DriverApiUrl {

    public DriverStatusInvoker() {
        super();
    }

    public AuthBean invokeDriverStatusWS() {

        WebConnector webConnector;

        webConnector = new WebConnector(new StringBuilder(DRIVER_STATUS_URL), WSConstants.PROTOCOL_HTTP, null, null);

        String wsResponseString = webConnector.connectToGET_service(true);

        System.out.println(">>>>>>>>>>> response: " + wsResponseString);
        AuthBean authBean = null;
        if (wsResponseString.equals("")) {
            return authBean = null;
        } else {
            authBean = new AuthBean();
            DriverStatusParser driverStatusParser = new DriverStatusParser();
            authBean = driverStatusParser.parseDriverStatus(wsResponseString);
            return authBean;
        }
    }
}