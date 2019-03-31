package org.autoride.driver.driver.net.invokers;

import org.autoride.driver.model.BasicBean;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.driver.net.WSConstants;
import org.autoride.driver.driver.net.WebConnector;
import org.autoride.driver.driver.net.parsers.BasicParser;
import org.json.JSONObject;

import java.util.HashMap;

public class PromoCodeAvailabilityCheckInvoker extends BaseInvoker implements DriverApiUrl {

    public PromoCodeAvailabilityCheckInvoker() {
        super();
    }

    public PromoCodeAvailabilityCheckInvoker(HashMap<String, String> urlParams, JSONObject postData) {
        super(urlParams, postData);
    }

    public BasicBean invokePromoCodeAvailabilityCheckWS() {

        System.out.println("POSTDATA>>>>>>>" + postData);

        WebConnector webConnector;

        webConnector = new WebConnector(new StringBuilder(PROMO_CODE_EXIST_URL), WSConstants.PROTOCOL_HTTP, null, postData);

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
            BasicParser basicParser = new BasicParser();
            basicBean = basicParser.parseBasicResponse(wsResponseString);
            return basicBean;
        }
    }
}