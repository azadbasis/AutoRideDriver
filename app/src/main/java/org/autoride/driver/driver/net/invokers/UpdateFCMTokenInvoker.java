package org.autoride.driver.driver.net.invokers;

import org.autoride.driver.model.BasicBean;
import org.autoride.driver.driver.net.ServiceNames;
import org.autoride.driver.driver.net.WSConstants;
import org.autoride.driver.driver.net.WebConnector;
import org.autoride.driver.driver.net.parsers.BasicParser;
import org.json.JSONObject;

import java.util.HashMap;

public class UpdateFCMTokenInvoker extends BaseInvoker {

    public UpdateFCMTokenInvoker() {
        super();
    }

    public UpdateFCMTokenInvoker(HashMap<String, String> urlParams,
                                 JSONObject postData) {
        super(urlParams, postData);
    }

    public BasicBean invokeUpdateFCMTokenWS() {

        System.out.println("POSTDATA>>>>>>>" + postData);

        WebConnector webConnector;

        webConnector = new WebConnector(new StringBuilder(ServiceNames.UPDATE_FCM_TOKEN), WSConstants.PROTOCOL_HTTP, null, postData);

        //		webConnector= new WebConnector(new StringBuilder(ServiceNames.AUTH_EMAIL), WSConstants.PROTOCOL_HTTP, postData,null);
        //webConnector= new WebConnector(new StringBuilder(ServiceNames.MODELS), WSConstants.PROTOCOL_HTTP, null);
        String wsResponseString = webConnector.connectToPOST_service();
        //	String wsResponseString=webConnector.connectToGET_service(true);
        System.out.println(">>>>>>>>>>> response: " + wsResponseString);
        BasicBean updateFCMTokenBean = null;
        if (wsResponseString.equals("")) {
            /*registerBean=new RegisterBean();
            registerBean.setWebError(true);*/
            return updateFCMTokenBean = null;
        } else {
            updateFCMTokenBean = new BasicBean();
            BasicParser basicParser = new BasicParser();
            updateFCMTokenBean = basicParser.parseBasicResponse(wsResponseString);
            return updateFCMTokenBean;
        }
    }
}