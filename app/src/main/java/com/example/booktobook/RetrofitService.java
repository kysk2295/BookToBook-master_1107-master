package com.example.booktobook;

import com.google.gson.annotations.SerializedName;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitService {
    String serverKey="AAAAvwFFAB0:APA91bG6lEovbB98QdViQbUDjM3ZWP9OMngTlHnmOkR6wtW1Fw5QmUXOLHi4RTPywzIB7km34uvmq2K-9F3tB8pttOtjhLKQSjPn68EH9dhZ0beE5YaW6tvuw9j5VmCxn6qir7Nd4kQ9";
    @Headers({"Authorization: key=" + serverKey, "Content-Type:application/json"})
    @POST("fcm/send")
    Call<ResponseBody> sendNotification(@Body RootModel root);


}
