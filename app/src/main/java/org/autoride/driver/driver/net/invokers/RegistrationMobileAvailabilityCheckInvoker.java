package org.autoride.driver.driver.net.invokers;

import org.autoride.driver.model.BasicBean;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.driver.net.WSConstants;
import org.autoride.driver.driver.net.WebConnector;
import org.autoride.driver.driver.net.parsers.RegistrationMobileCheckParser;
import org.json.JSONObject;

import java.util.HashMap;

public class RegistrationMobileAvailabilityCheckInvoker extends BaseInvoker implements DriverApiUrl {

    public RegistrationMobileAvailabilityCheckInvoker() {
        super();
    }

    public RegistrationMobileAvailabilityCheckInvoker(HashMap<String, String> urlParams, JSONObject postData) {
        super(urlParams, postData);
    }

    public BasicBean invokeRegistrationMobileAvailabilityCheckWS() {

        System.out.println("POSTDATA>>>>>>>" + postData);

        WebConnector webConnector;

        webConnector = new WebConnector(new StringBuilder(PHONE_NUMBER_EXIST_URL), WSConstants.PROTOCOL_HTTP, null, postData);

        //		webConnector= new WebConnector(new StringBuilder(ServiceNames.AUTH_EMAIL), WSConstants.PROTOCOL_HTTP, postData,null);
        //webConnector= new WebConnector(new StringBuilder(ServiceNames.MODELS), WSConstants.PROTOCOL_HTTP, null);
        String wsResponseString = webConnector.connectToPOST_service();
        //	String wsResponseString=webConnector.connectToGET_service(true);
        System.out.println(">>>>>>>>>>> response: " + wsResponseString);
        BasicBean basicBean = null;
        if (wsResponseString.equals("")) {
            /*registerBean=new RegisterBean();
            registerBean.setWebError(true);*/
            return basicBean = null;
        } else {
            basicBean = new BasicBean();
            RegistrationMobileCheckParser registrationMobileCheckParser = new RegistrationMobileCheckParser();
            basicBean = registrationMobileCheckParser.parseRegistrationMobileCheckResponse(wsResponseString);
            return basicBean;
        }
    }
}