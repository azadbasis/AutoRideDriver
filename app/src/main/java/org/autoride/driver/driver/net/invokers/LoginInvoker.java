package org.autoride.driver.driver.net.invokers;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.driver.net.WSConstants;
import org.autoride.driver.driver.net.WebConnector;
import org.autoride.driver.driver.net.parsers.LoginParser;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginInvoker extends BaseInvoker implements DriverApiUrl {

    public LoginInvoker() {
        super();
    }

    public LoginInvoker(HashMap<String, String> urlParams, JSONObject postData) {
        super(urlParams, postData);
    }

    public AuthBean invokeLoginWS() {

        System.out.println("POSTDATA>>>>>>>" + postData);

        WebConnector webConnector;

        webConnector = new WebConnector(new StringBuilder(USER_LOGIN_URL), WSConstants.PROTOCOL_HTTP, null, postData);

        //		webConnector= new WebConnector(new StringBuilder(ServiceNames.AUTH_EMAIL), WSConstants.PROTOCOL_HTTP, postData,null);
        //webConnector= new WebConnector(new StringBuilder(ServiceNames.MODELS), WSConstants.PROTOCOL_HTTP, null);
        String wsResponseString = webConnector.connectToPOST_service();
        //	String wsResponseString=webConnector.connectToGET_service(true);
        System.out.println(">>>>>>>>>>> response: " + wsResponseString);
        AuthBean authBean = null;
        if (wsResponseString.equals("")) {
            return authBean = null;
        } else {
            authBean = new AuthBean();
            LoginParser loginParser = new LoginParser();
            authBean = loginParser.parseLoginResponse(wsResponseString);
            return authBean;
        }
    }
}