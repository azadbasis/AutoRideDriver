package org.autoride.driver.driver.net;

import org.autoride.driver.model.AuthBean;
import org.autoride.driver.model.BasicBean;
import org.autoride.driver.model.DriverBean;
import org.autoride.driver.driver.net.tasks.DriverStatusTask;
import org.autoride.driver.driver.net.tasks.DriverStatusUpdateTask;
import org.autoride.driver.driver.net.tasks.ForgotPasswordTask;
import org.autoride.driver.driver.net.tasks.ImageUpdateTask;
import org.autoride.driver.driver.net.tasks.LoginTask;
import org.autoride.driver.driver.net.tasks.MobileAvailabilityCheckTask;
import org.autoride.driver.driver.net.tasks.PromoCodeAvailabilityCheckTask;
import org.autoride.driver.driver.net.tasks.RegistrationMobileAvailabilityCheckTask;
import org.autoride.driver.driver.net.tasks.RegistrationTask;
import org.autoride.driver.driver.net.tasks.RequestCancelTask;
import org.autoride.driver.driver.net.tasks.RequestTriggeringTask;
import org.autoride.driver.driver.net.tasks.UpdateFCMTokenTask;
import org.autoride.driver.driver.net.tasks.VehicleInfoTask;
import org.autoride.driver.listeners.BasicListener;
import org.autoride.driver.listeners.DriverListener;
import org.autoride.driver.listeners.ForgotPasswordListener;
import org.autoride.driver.listeners.ImageUpdateListener;
import org.autoride.driver.listeners.LoginListener;
import org.autoride.driver.listeners.RegistrationListener;
import org.autoride.driver.utils.AppConstants;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class DataManager {

    public static void performUpdateFCMToken(JSONObject postData, final BasicListener listener) {

        UpdateFCMTokenTask updateFCMTokenTask = new UpdateFCMTokenTask(postData);
        updateFCMTokenTask.setUpdateFCMTokenTaskListener(new UpdateFCMTokenTask.UpdateFCMTokenTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(BasicBean basicBean) {
                if (basicBean == null)
                    listener.onLoadFailed(basicBean);
                else {
                    if (basicBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(basicBean);
                    } else if (basicBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(basicBean);
                    } else {
                        listener.onLoadFailed(basicBean);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(BasicBean basicBean) {
                listener.onLoadFailed(basicBean);
            }
        });
        updateFCMTokenTask.execute();
    }

    public static void performRegistration(JSONObject postData, final RegistrationListener listener) {

        RegistrationTask registrationTask = new RegistrationTask(postData);
        registrationTask.setRegistrationTaskListener(new RegistrationTask.RegistrationTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(AuthBean authBean) {
                if (authBean == null) {
                    listener.onLoadFailed(authBean);
                } else {
                    if (authBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(authBean);
                    } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(authBean);
                    } else {
                        listener.onLoadFailed(authBean);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(AuthBean authBean) {
                listener.onLoadFailed(authBean);
            }
        });
        registrationTask.execute();
    }


    /*public static void performOTPSubmit(JSONObject postData, final OTPSubmitListener listener) {

        OTPSubmitTask otpSubmitTask = new OTPSubmitTask(postData);
        otpSubmitTask.setOtpSubmitTaskListener(new OTPSubmitTask.OTPSubmitTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(OTPBean otpBean) {
                if (otpBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (otpBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(otpBean);
                    } else if (otpBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(otpBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        otpSubmitTask.execute();
    }*/


    public static void performLogin(JSONObject postData, final LoginListener listener) {

        LoginTask loginTask = new LoginTask(postData);
        loginTask.setLoginTaskListener(new LoginTask.LoginTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(AuthBean authBean) {
                if (authBean == null)
                    listener.onLoadFailed(authBean);
                else {
                    if (authBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(authBean);
                    } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(authBean);
                    } else {
                        listener.onLoadFailed(authBean);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(AuthBean authBean) {
                listener.onLoadFailed(authBean);
            }
        });
        loginTask.execute();
    }

   /* public static void performEditProfile(JSONObject postData, List<String> fileList, final EditProfileListener listener) {

        EditProfileTask editProfileTask = new EditProfileTask(postData, fileList);
        editProfileTask.setEditProfileTaskListener(new EditProfileTask.EditProfileTaskListener() {

            @Override
            public void dataDownloadedSuccessfully(UserBean userBean) {
                if (userBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (userBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(userBean);
                    } else if (userBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(userBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        editProfileTask.execute();
    }*/


  /*  public static void fetchUserInfo(HashMap<String, String> urlParams, String userID, final UserInfoListener listener) {

        UserInfoTask userInfoTask = new UserInfoTask(urlParams);
        userInfoTask.setUserInfoTaskListener(new UserInfoTask.UserInfoTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(UserBean userBean) {
                if (userBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (userBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(userBean);
                    } else if (userBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(userBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        userInfoTask.execute();
    }

    public static void fetchPromoCode(HashMap<String, String> urlParams, final PromoCodeListener listener) {

        PromoCodeTask promoCodeTask = new PromoCodeTask(urlParams);
        promoCodeTask.setPromoCodeTaskListener(new PromoCodeTask.PromoCodeTaskListener() {

            @Override
            public void dataDownloadedSuccessfully(PromoCodeBean promoCodeBean) {
                if (promoCodeBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (promoCodeBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(promoCodeBean);
                    } else if (promoCodeBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(promoCodeBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        promoCodeTask.execute();
    }

    public static void fetchCarAvailability(HashMap<String, String> urlParams, final CarInfoListener listener) {

        CarInfoTask carInfoTask = new CarInfoTask(urlParams);
        carInfoTask.setCarInfoTaskListener(new CarInfoTask.CarInfoTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(CarBean carBean) {
                if (carBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (carBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(carBean);
                    } else if (carBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(carBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        carInfoTask.execute();
    }

    public static void fetchLandingPageDetails(HashMap<String, String> urlParams, final LandingPageListener landingPageListener) {

        LandingPageDetailsTask landingPageDetailsTask = new LandingPageDetailsTask(urlParams);
        landingPageDetailsTask.setLandingPageDetailsTaskListener(new LandingPageDetailsTask.LandingPageDetailsTaskListener() {

            @Override
            public void dataDownloadedSuccessfully(LandingPageBean landingPageListBean) {
                if (landingPageListBean == null)
                    landingPageListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (landingPageListBean.getStatus().equalsIgnoreCase("Success")) {
                        landingPageListener.onLoadCompleted(landingPageListBean);
                    } else if (landingPageListBean.getStatus().equalsIgnoreCase("Error")) {
                        landingPageListener.onLoadFailed(landingPageListBean.getErrorMsg());
                    } else {
                        landingPageListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                landingPageListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        landingPageDetailsTask.execute();
    }

    public static void fetchTripDetails(HashMap<String, String> urlParams, final TripDetailsListener listener) {

        TripDetailsTask tripDetailsTask = new TripDetailsTask(urlParams);
        tripDetailsTask.setTripDetailsTaskListener(new TripDetailsTask.TripDetailsTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(TripDetailsBean tripDetailsBean) {
                if (tripDetailsBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (tripDetailsBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(tripDetailsBean);
                    } else if (tripDetailsBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(tripDetailsBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        tripDetailsTask.execute();
    }

    public static void fetchTotalFare(HashMap<String, String> urlParams, final TotalFareListener listener) {

        TotalFareTask totalFareTask = new TotalFareTask(urlParams);
        totalFareTask.setTotalFareTaskListener(new TotalFareTask.TotalFareTaskListener() {

            @Override
            public void dataDownloadedSuccessfully(FareBean fareBean) {
                if (fareBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (fareBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(fareBean);
                    } else if (fareBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(fareBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        totalFareTask.execute();
    }*/


    /*public static void fetchRecentSearches(HashMap<String, String> urlParams, final RecentSearchListener listener) {

        RecentSearchTask recentSearchTask = new RecentSearchTask(urlParams);
        recentSearchTask.setRecentSearchListener(new RecentSearchTask.RecentSearchListener() {

            @Override
            public void dataDownloadedSuccessfully(RecentSearchBean recentSearchBean) {
                if (recentSearchBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (recentSearchBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(recentSearchBean);
                    } else if (recentSearchBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(recentSearchBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);

            }
        });

        recentSearchTask.execute();
    }*/

   /* public static void fetchTripList(HashMap<String, String> urlParams, final TripListListener listener) {

        TripListTask tripListTask = new TripListTask(urlParams);
        tripListTask.setTripListTaskListener(new TripListTask.TripListTaskListener() {

            @Override
            public void dataDownloadedSuccessfully(TripListBean tripListBean) {
                if (tripListBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (tripListBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(tripListBean);
                    } else if (tripListBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(tripListBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        tripListTask.execute();
    }

    public static void fetchSavedLocation(HashMap<String, String> urlParams, final SavedLocationListener listener) {

        SavedLocationTask savedLocationTask = new SavedLocationTask(urlParams);
        savedLocationTask.setSavedLocationTaskListener(new SavedLocationTask.SavedLocationTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(LocationBean locationBean) {
                if (locationBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (locationBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(locationBean);
                    } else if (locationBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(locationBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        savedLocationTask.execute();
    }*/

    /*public static void fetchLandingPageDetails(HashMap<String, String> urlParams, final LandingPageDetailsListener listener) {

        LandingPageDetailsTask landingPageDetailsTask = new LandingPageDetailsTask(urlParams);
        landingPageDetailsTask.setLandingPageDetailsTaskListener(new LandingPageDetailsTask.LandingPageDetailsTaskListener() {

            @Override
            public void dataDownloadedSuccessfully(LandingPageListBean landingPageListBean) {
                if (landingPageListBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (landingPageListBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(landingPageListBean);
                    } else if (landingPageListBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(landingPageListBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        landingPageDetailsTask.execute();
    }*/


 /*     public static void fetchRequestStatus(HashMap<String, String> urlParams, final RequestStatusListener listener) {

        RequestStatusTask requestStatusTask = new RequestStatusTask(urlParams);
        requestStatusTask.setRequestStatusTaskListener(new RequestStatusTask.RequestStatusTaskListener() {

            @Override
            public void dataDownloadedSuccessfully(RequestBean requestBean) {
                if (requestBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (requestBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(requestBean);
                    } else if (requestBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(requestBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });

        requestStatusTask.execute();
    }

    public static void fetchRequestStatus(HashMap<String, String> urlParams, final DriverDetailsListener listener) {

        DriverDetailsTask driverdetailsTask = new DriverDetailsTask(urlParams);
        driverdetailsTask.setDriverDetailsTaskListener(new DriverDetailsTask.DriverDetailsTaskListener() {

            @Override
            public void dataDownloadedSuccessfully(DriverBean driverBean) {
                if (driverBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (driverBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(driverBean);
                    } else if (driverBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(driverBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });

        driverdetailsTask.execute();
    }


    public static void fetchTripCompletionDetails(HashMap<String, String> urlParams, final SuccessListener listener) {

        SuccessDetailsTask successDetailsTask = new SuccessDetailsTask(urlParams);
        successDetailsTask.setSuccessDetailsTaskListener(new SuccessDetailsTask.SuccessDetailsTaskListener() {

            @Override
            public void dataDownloadedSuccessfully(SuccessBean successBean) {
                if (successBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (successBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(successBean);
                    } else if (successBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(successBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });

        successDetailsTask.execute();

    }

    public static void fetchPolyPoints(HashMap<String, String> urlParams, final PolyPointsListener polyPointsListener) {

        PolyPointsTask polyPointsTask = new PolyPointsTask(urlParams);
        polyPointsTask.setPolyPointsTaskListener(new PolyPointsTask.PolyPointsTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(PolyPointsBean polyPointsBean) {
                if (polyPointsBean == null)
                    polyPointsListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (polyPointsBean.getStatus().equalsIgnoreCase("Success")) {
                        polyPointsListener.onLoadCompleted(polyPointsBean);
                    } else if (polyPointsBean.getStatus().equalsIgnoreCase("Error")) {
                        polyPointsListener.onLoadFailed(polyPointsBean.getErrorMsg());
                    } else {
                        polyPointsListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                polyPointsListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        polyPointsTask.execute();
    }

    public static void performTripFeedback(JSONObject postData, final TripFeedbackListener tripFeedbackListener) {

        TripFeedbackTask tripFeedbackTask = new TripFeedbackTask(postData);
        tripFeedbackTask.setTripFeedbackTaskListener(new TripFeedbackTask.TripFeedbackTaskListener() {

            @Override
            public void dataDownloadedSuccessfully(TripFeedbackBean tripFeedbackBean) {
                if (tripFeedbackBean == null)
                    tripFeedbackListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (tripFeedbackBean.getStatus().equalsIgnoreCase("Success")) {
                        tripFeedbackListener.onLoadCompleted(tripFeedbackBean);
                    } else if (tripFeedbackBean.getStatus().equalsIgnoreCase("Error")) {
                        tripFeedbackListener.onLoadFailed(tripFeedbackBean.getErrorMsg());
                    } else {
                        tripFeedbackListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                tripFeedbackListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });

        tripFeedbackTask.execute();
    }

    public static void performTripCancellation(JSONObject postData, final TripCancellationListener tripCancellationListener) {

        TripCancellationTask tripCancellationTask = new TripCancellationTask(postData);
        tripCancellationTask.setTripCancellationTaskListener(new TripCancellationTask.TripCancellationTaskListener() {

            @Override
            public void dataDownloadedSuccessfully(TripCancellationBean tripCancellationBean) {
                if (tripCancellationBean == null)
                    tripCancellationListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (tripCancellationBean.getStatus().equalsIgnoreCase("Success")) {
                        tripCancellationListener.onLoadCompleted(tripCancellationBean);
                    } else if (tripCancellationBean.getStatus().equalsIgnoreCase("Error")) {
                        tripCancellationListener.onLoadFailed(tripCancellationBean.getErrorMsg());
                    } else {
                        tripCancellationListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                tripCancellationListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });

        tripCancellationTask.execute();
    }

    public static void performRequestRide(JSONObject postData, final RequestRideListener requestRideListener) {

        RequestRideTask requestRideTask = new RequestRideTask(postData);
        requestRideTask.setRequestRideTaskListener(new RequestRideTask.RequestRideTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(RequestBean requestBean) {
                if (requestBean == null)
                    requestRideListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (requestBean.getStatus().equalsIgnoreCase("Success")) {
                        requestRideListener.onLoadCompleted(requestBean);
                    } else if (requestBean.getStatus().equalsIgnoreCase("Error")) {
                        requestRideListener.onLoadFailed(requestBean.getErrorMsg());
                    } else {
                        requestRideListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                requestRideListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        requestRideTask.execute();
    }
*/
   /* public static void performNewPassword(JSONObject postData, final BasicListener basicListener) {

        NewPasswordTask newPasswordTask = new NewPasswordTask(postData);
        newPasswordTask.setNewPasswordTaskListener(new NewPasswordTask.NewPasswordTaskListener() {

            @Override
            public void dataDownloadedSuccessfully(BasicBean basicBean) {
                if (basicBean == null)
                    basicListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (basicBean.getStatus().equalsIgnoreCase("Success")) {
                        basicListener.onLoadCompleted(basicBean);
                    } else if (basicBean.getStatus().equalsIgnoreCase("Error")) {
                        basicListener.onLoadFailed(basicBean.getErrorMsg());
                    } else {
                        basicListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                basicListener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });

        newPasswordTask.execute();
    }*/

    public static void performRequestCancel(JSONObject postData, final BasicListener basicListener) {

        RequestCancelTask requestCancelTask = new RequestCancelTask(postData);
        requestCancelTask.setRequestCancelTaskListener(new RequestCancelTask.RequestCancelTaskListener() {

            @Override
            public void dataDownloadedSuccessfully(BasicBean basicBean) {
                if (basicBean == null)
                    basicListener.onLoadFailed(basicBean);
                else {
                    if (basicBean.getStatus().equalsIgnoreCase("Success")) {
                        basicListener.onLoadCompleted(basicBean);
                    } else if (basicBean.getStatus().equalsIgnoreCase("Error")) {
                        basicListener.onLoadFailed(basicBean);
                    } else {
                        basicListener.onLoadFailed(basicBean);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(BasicBean basicBean) {
                basicListener.onLoadFailed(basicBean);
            }
        });

        requestCancelTask.execute();
    }

    public static void performMobileAvailabilityCheck(JSONObject postData, final BasicListener basicListener) {

        MobileAvailabilityCheckTask mobileAvailabilityCheckTask = new MobileAvailabilityCheckTask(postData);
        mobileAvailabilityCheckTask.setMobileAvailabilityCheckTaskListener(
                new MobileAvailabilityCheckTask.MobileAvailabilityCheckTaskListener() {

                    @Override
                    public void dataDownloadedSuccessfully(BasicBean basicBean) {
                        if (basicBean == null) {
                            basicListener.onLoadFailed(basicBean);
                        } else {
                            if (basicBean.getStatus().equalsIgnoreCase("Success")) {
                                basicListener.onLoadCompleted(basicBean);
                            } else if (basicBean.getStatus().equalsIgnoreCase("Error")) {
                                basicListener.onLoadFailed(basicBean);
                            } else {
                                basicListener.onLoadFailed(basicBean);
                            }
                        }
                    }

                    @Override
                    public void dataDownloadFailed(BasicBean basicBean) {
                        basicListener.onLoadFailed(basicBean);
                    }
                });

        mobileAvailabilityCheckTask.execute();
    }

    public static void performRegistrationMobileAvailabilityCheck(JSONObject postData, final BasicListener basicListener) {

        RegistrationMobileAvailabilityCheckTask registrationMobileAvailabilityCheckTask = new RegistrationMobileAvailabilityCheckTask(postData);
        registrationMobileAvailabilityCheckTask.setRegistrationMobileAvailabilityCheckTaskListener(
                new RegistrationMobileAvailabilityCheckTask.RegistrationMobileAvailabilityCheckTaskListener() {

                    @Override
                    public void dataDownloadedSuccessfully(BasicBean basicBean) {
                        if (basicBean == null)
                            basicListener.onLoadFailed(basicBean);
                        else {
                            if (basicBean.getStatus().equalsIgnoreCase("Success")) {
                                basicListener.onLoadCompleted(basicBean);
                            } else if (basicBean.getStatus().equalsIgnoreCase("Error")) {
                                basicListener.onLoadFailed(basicBean);
                            } else {
                                basicListener.onLoadFailed(basicBean);
                            }
                        }
                    }

                    @Override
                    public void dataDownloadFailed(BasicBean basicBean) {
                        basicListener.onLoadFailed(basicBean);
                    }
                });

        registrationMobileAvailabilityCheckTask.execute();
    }

    public static void performPromoCodeAvailabilityCheck(JSONObject postData, final BasicListener basicListener) {

        PromoCodeAvailabilityCheckTask promoCodeAvailabilityCheckTask = new PromoCodeAvailabilityCheckTask(postData);
        promoCodeAvailabilityCheckTask.setPromoCodeAvailabilityCheckTaskListener(
                new PromoCodeAvailabilityCheckTask.PromoCodeAvailabilityCheckTaskListener() {

                    @Override
                    public void dataDownloadedSuccessfully(BasicBean basicBean) {
                        if (basicBean == null)
                            basicListener.onLoadFailed(basicBean);
                        else {
                            if (basicBean.getStatus().equalsIgnoreCase("Success")) {
                                basicListener.onLoadCompleted(basicBean);
                            } else if (basicBean.getStatus().equalsIgnoreCase("Error")) {
                                basicListener.onLoadFailed(basicBean);
                            } else {
                                basicListener.onLoadFailed(basicBean);
                            }
                        }
                    }

                    @Override
                    public void dataDownloadFailed(BasicBean basicBean) {
                        basicListener.onLoadFailed(basicBean);
                    }
                });

        promoCodeAvailabilityCheckTask.execute();
    }

    public static void performUserLogin(JSONObject postData, final LoginListener listener) {
        LoginTask loginTask = new LoginTask(postData);
        loginTask.setLoginTaskListener(new LoginTask.LoginTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(AuthBean authBean) {
                if (authBean == null) {
                    listener.onLoadFailed(authBean);
                } else {
                    if (authBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(authBean);
                    } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(authBean);
                    } else {
                        listener.onLoadFailed(authBean);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(AuthBean authBean) {
                listener.onLoadFailed(authBean);
            }
        });
        loginTask.execute();
    }

    public static void performForgotPassword(JSONObject postData, final ForgotPasswordListener listener) {

        ForgotPasswordTask forgotPasswordTask = new ForgotPasswordTask(postData);
        forgotPasswordTask.setForgotPasswordTaskListener(new ForgotPasswordTask.ForgotPasswordTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(AuthBean authBean) {
                if (authBean == null) {
                    listener.onLoadFailed(authBean);
                } else {
                    if (authBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(authBean);
                    } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(authBean);
                    } else {
                        listener.onLoadFailed(authBean);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(AuthBean authBean) {
                listener.onLoadFailed(authBean);
            }
        });
        forgotPasswordTask.execute();
    }

    public static void getDriverStatus(final DriverListener listener) {

        DriverStatusTask task = new DriverStatusTask();
        task.setDriverStatusTaskListener(new DriverStatusTask.DriverStatusTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(AuthBean authBean) {
                if (authBean == null) {
                    listener.onLoadFailed(authBean);
                } else {
                    if (authBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(authBean);
                    } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(authBean);
                    } else {
                        listener.onLoadFailed(authBean);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(AuthBean authBean) {
                listener.onLoadFailed(authBean);
            }
        });
        task.execute();
    }

    public static void driverStatusUpdate(JSONObject postData, final DriverListener listener) {

        DriverStatusUpdateTask task = new DriverStatusUpdateTask(postData);
        task.setDriverStatusUpdateTaskListener(new DriverStatusUpdateTask.DriverStatusUpdateTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(AuthBean authBean) {
                if (authBean == null) {
                    listener.onLoadFailed(authBean);
                } else {
                    if (authBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(authBean);
                    } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(authBean);
                    } else {
                        listener.onLoadFailed(authBean);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(AuthBean authBean) {
                listener.onLoadFailed(authBean);
            }
        });
        task.execute();
    }

    public static void getVehicleInfo(final DriverListener listener) {

        VehicleInfoTask task = new VehicleInfoTask();
        task.setVehicleInfoTaskListener(new VehicleInfoTask.VehicleInfoTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(AuthBean authBean) {
                if (authBean == null) {
                    listener.onLoadFailed(authBean);
                } else {
                    if (authBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(authBean);
                    } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(authBean);
                    } else {
                        listener.onLoadFailed(authBean);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(AuthBean authBean) {
                listener.onLoadFailed(authBean);
            }
        });
        task.execute();
    }

    public static void performImageUpdate(JSONObject postData, List<String> list, final ImageUpdateListener listener) {

        ImageUpdateTask imageUpdateTask = new ImageUpdateTask(postData, list);
        imageUpdateTask.setImageUpdateTaskListener(new ImageUpdateTask.ImageUpdateTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(AuthBean authBean) {
                if (authBean == null)
                    listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                else {
                    if (authBean.getStatus().equalsIgnoreCase("Success")) {
                        listener.onLoadCompleted(authBean);
                    } else if (authBean.getStatus().equalsIgnoreCase("Error")) {
                        listener.onLoadFailed(authBean.getErrorMsg());
                    } else {
                        listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
                    }
                }
            }

            @Override
            public void dataDownloadFailed() {
                listener.onLoadFailed(AppConstants.WEB_ERROR_MSG);
            }
        });
        imageUpdateTask.execute();
    }

    public static void performRequestTriggering(JSONObject postData, final BasicListener basicListener) {

        RequestTriggeringTask requestTriggeringTask = new RequestTriggeringTask(postData);
        requestTriggeringTask.setRequestTriggeringTaskListener(new RequestTriggeringTask.RequestTriggeringTaskListener() {
            @Override
            public void dataDownloadedSuccessfully(BasicBean basicBean) {
                if (basicBean == null)
                    basicListener.onLoadFailed(basicBean);
                else {
                    if (basicBean.getStatus().equalsIgnoreCase("Success")) {
                        basicListener.onLoadCompleted(basicBean);
                    } else if (basicBean.getStatus().equalsIgnoreCase("Error")) {
                        basicListener.onLoadFailed(basicBean);
                    } else {
                        basicListener.onLoadFailed(basicBean);
                    }
                }
            }

            @Override
            public void dataDownloadFailed(BasicBean basicBean) {
                basicListener.onLoadFailed(basicBean);
            }
        });

        requestTriggeringTask.execute();
    }
}