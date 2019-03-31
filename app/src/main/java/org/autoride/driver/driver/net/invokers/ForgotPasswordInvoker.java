package org.autoride.driver.driver.net.invokers;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.networks.DriverApiUrl;
import org.autoride.driver.driver.net.WSConstants;
import org.autoride.driver.driver.net.WebConnector;
import org.autoride.driver.driver.net.parsers.ForgotPasswordParser;
import org.json.JSONObject;

import java.util.HashMap;

public class ForgotPasswordInvoker extends BaseInvoker implements DriverApiUrl {

    public ForgotPasswordInvoker() {
        super();
    }

    public ForgotPasswordInvoker(HashMap<String, String> urlParams, JSONObject postData) {
        super(urlParams, postData);
    }

    public AuthBean invokeForgotPasswordWS() {

        System.out.println("POSTDATA>>>>>>>" + postData);

        WebConnector webConnector;

        webConnector = new WebConnector(new StringBuilder(FORGOT_PASSWORD_URL), WSConstants.PROTOCOL_HTTP, null, postData);

        String wsResponseString = webConnector.connectToPOST_service();

        System.out.println(">>>>>>>>>>> response: " + wsResponseString);
        AuthBean authBean = null;
        if (wsResponseString.equals("")) {
            return authBean = null;
        } else {
            authBean = new AuthBean();
            ForgotPasswordParser forgotPasswordParser = new ForgotPasswordParser();
            authBean = forgotPasswordParser.parseForgotPasswordResponse(wsResponseString);
            return authBean;
        }
    }
}