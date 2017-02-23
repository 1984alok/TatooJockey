package services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import model.APIError;
import model.ResponseModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utills.CommonUtill;
import utills.Constants;
import utills.ErrorUtils;
import webconnectionhandler.ApiClient;
import webconnectionhandler.ApiInterface;

/**
 * Created by Alok on 26-11-2016.
 */
public class FirebaseIDService extends FirebaseInstanceIdService {
    private static final String TAG = "FirebaseIDService";
    private ApiInterface apiService;

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        apiService =
                ApiClient.getClient().create(ApiInterface.class);
        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(CommonUtill.getDeviceId(this),refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String deviceId,String token) {
        // Add custom implementation, as needed.

        Log.i(TAG, "sendRegistrationToServer: "+deviceId +"::"+token);
        Call<ResponseModel> response = apiService.sendToken(deviceId,token,Constants.NOTIOFICATION_ON);
        response.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                if(response.isSuccessful()){
                    ResponseModel responseModel = response.body();
                    if(responseModel.getStatus().equalsIgnoreCase(Constants.RESPONSE_STATUS_TRUE)){
                        //do next
                        Log.i(TAG,response.message());
                    }else{
                    }
                }else{
                    Log.i("onFailure Error",response.message());

                    APIError error = ErrorUtils.parseError(response);
                    if(error!=null) {
                        //  Log.d("error message", error.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                Log.i("onFailure Error",t.toString());
            }
        });
    }
}
