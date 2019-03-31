package org.autoride.driver.driver.net.invokers;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.driver.net.WSConstants;
import org.autoride.driver.driver.net.WebConnector;
import org.autoride.driver.driver.net.parsers.DriverStatusParser;
import org.json.JSONObject;

import java.util.HashMap;

public class DriverStatusUpdateInvoker extends BaseInvoker implements DriverApiUrl {

    public DriverStatusUpdateInvoker() {
        super();
    }

    public DriverStatusUpdateInvoker(HashMap<String, String> urlParams, JSONObject postData) {
        super(urlParams, postData);
    }

    public AuthBean invokeDriverStatusUpdateWS() {

        System.out.println("POSTDATA>>>>>>>" + postData);

        WebConnector webConnector;

        webConnector = new WebConnector(new StringBuilder(UPDATE_DRIVER_STATUS_URL), WSConstants.PROTOCOL_HTTP, null, postData);

        String wsResponseString = webConnector.connectToPOST_service();

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