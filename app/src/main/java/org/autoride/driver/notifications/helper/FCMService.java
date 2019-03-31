package org.autoride.driver.notifications.helper;

import org.autoride.driver.model.DataMessage;
import org.autoride.driver.model.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface FCMService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAiFp49eU:APA91bF7_KtvWHzHp96sWcZXLsXoAsetWBIj1HR-1_aNgM3NF6DzDIk_5Rai8oXHrpPGNLiBabQSPkvjQWn_Dl5x2gtoG3RxMbOoc1ZMDvqbXcmRxjBpJ_NHwLcsqNON04Q7uhtKStfc"
    })
    @POST("fcm/send")
    Call<FCMResponse> sendMessage(@Body DataMessage dody);
}