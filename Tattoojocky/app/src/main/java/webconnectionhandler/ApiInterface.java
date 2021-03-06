package webconnectionhandler;

import java.util.Map;

import model.ResponseModel;
import model.TattoCatagory;
import model.TattooInfo;
import model.UserModel;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;

/**
 * Created by Alok on 26-09-2016.
 */
public interface ApiInterface {

    //get all catagory
    @POST("category/list")
    Call<TattoCatagory> getTattoCatgory(@Query("page_number") String pageNumber);

    //signup
    //"name","email","password"
    @FormUrlEncoded
    @POST("user/add")
    Call<UserModel> createUser(@Field("name") String name,
                               @Field("email") String email,
                               @Field("password") String password,
                               @Field("status") String status);

    //Log In
    @FormUrlEncoded
    @POST("user/login")
    Call<UserModel> logIn( @Field("email") String email,
                           @Field("password") String password,
                           @Field("submit") String submit);


    //Get tattoos info
    @FormUrlEncoded
    @POST("tattoos/tattoo")
    Call<TattooInfo> getTattoInfo(
            @Field("tattoo_cate_id") String tattoo_cate_id,
            @Field("page_number") String page_number,
            @Field("user_id") String user_id
    );

    @FormUrlEncoded
    @POST("tattoos/rating/")
    Call<ResponseModel> callTatooLikeDislike(
            @Field("tattoo_id") String tattoo_id,
            @Field("user_id") String user_id,
            @Field("star_count") String star_count
    );


    @FormUrlEncoded
    @POST("tattoos/share/")
    Call<ResponseModel> callTatooShare(
            @Field("tattoo_id") String tattoo_id,
            @Field("user_id") String user_id,
            @Field("social_media_type_id") String star_count
    );



    @FormUrlEncoded
    @POST("user/forgot/")
    Call<ResponseModel> callForgotPwd(
            @Field("email") String email
    );




    @Multipart
    @POST("user/update/")
    Call<UserModel> callUpdateProfile(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part file);


    @Multipart
    @POST("tattoos/add/")
    Call<ResponseModel> callUploadTattoo(
            @PartMap() Map<String, RequestBody> partMap,
            @Part MultipartBody.Part file);


    @FormUrlEncoded
    @POST("user/notistatus/")
    Call<ResponseModel> callNotificationOnOff(
            @Field("user_id") String user_id,
            @Field("is_notification") String is_notification
    );


    @FormUrlEncoded
    @POST("notification/device_token/")
    Call<ResponseModel> sendToken(
            @Field("device_id") String device_id,
            @Field("token_id") String token_id,
            @Field("status") String status
    );






}
