package com.csc301.students.BookBarter;

import android.graphics.Bitmap;

import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Multipart;
import retrofit2.http.Part;
import okhttp3.MultipartBody;

import java.util.List;

public interface Api {
    @FormUrlEncoded
    @POST("activeUser")
    Call<ResponseBody> register(
            @Header("Authorization") String token,
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("active") String active


    );

    @FormUrlEncoded
    @POST("sendMsg")
    Call<ResponseBody> sendmsg(
            @Header("Authorization") String token,
            @Field("Seller_em") String s_em,
            @Field("Buyer_em") String buyer_em,
            @Field ("Msg") String msg


    );



    @FormUrlEncoded
    @POST("login")
    Call<JsonResult> login(
            @Field("password") String password,
            @Field("email") String username
    );

    @FormUrlEncoded
    @POST("post")
    Call<ResponseBody> post(
            @Header("Authorization") String token,
            @Field("title") String title,
            @Field("description") String description,
            @Field("price") String price,
            @Field("image") Bitmap image


    );

    @Multipart
    @POST("updatePost")
    Call<JsonResult> updatePost(
            @Header("Authorization") String token,
            @Part List<MultipartBody.Part> body

    );


    @POST("myposts")
    Call<JsonResult> myposts(
            @Header("Authorization") String token
    );
//    @FormUrlEncoded
//    @POST("Verify")
//    Call<>


    @POST("getTextbook")
    Call<JsonResult> getTextbook(
            @Header ("Authorization") String token
    );


    @FormUrlEncoded
    @POST("getAd")
    Call<JsonResult>getAd(
            @Header("Authorization") String token,
            @Field("id") String buttonId
    );

    @FormUrlEncoded
    @POST("deleteAd")
    Call<JsonResult>deleteAd(
            @Header("Authorization") String token,
            @Field("id") String buttonId
    );

    @POST("getUserInfo")
    Call<JsonResult> getUserInfo(
            @Header("Authorization") String token
    );


    @FormUrlEncoded
    @POST("getActive")
    Call<ResponseBody> getActive(
      @Field("email") String email
    );


    @POST("editUserProfile")
    Call<JsonResult> editUserInfo(
            @Header("Authorization") String token,
            @Body JsonObject body
    );
    @POST("getTextbook")
    Call<JsonResult> getTextbooks(
            @Header("Authorization") String token
    );

    @POST("getAllPosts")
    Call<JsonResult> getAllposts(
            @Header("Authorization") String token
    );
    @FormUrlEncoded
    @POST("checkUser")
    Call<JsonResult> checkUser(
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("getSellerInfo")
    Call<JsonResult> getSellerInfo(
            @Header("Authorization") String token,
            @Field("email") String email
    );

    @POST("resetPassword")
    Call<JsonResult> resetPassword(
            @Header("Authorization") String token,
            @Body JsonObject body
    );
    @Multipart
    @POST("CreatePost")
    Call<JsonResult> createPost(
            @Header("Authorization") String token,
            @Part List<MultipartBody.Part> body

    );

    @POST("putInterested")
    Call<JsonResult> putInterested(
            @Header("Authorization") String token,
            @Body JsonObject body
    );
    @POST("getInterested")
    Call<JsonResult> getInterested(
            @Header("Authorization") String token
    );

}
