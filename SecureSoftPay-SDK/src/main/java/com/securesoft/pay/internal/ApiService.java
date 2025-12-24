package com.securesoft.pay.internal;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {
    // ★★★ পরিবর্তন এখানে ★★★
    // এখান থেকে "api/" অংশটি সরিয়ে দেওয়া হয়েছে।
    // এখন এটি শুধু ফাইলের নামটি ব্যবহার করবে।
    @POST("initiate_payment.php")
    Call<InitiatePaymentResponse> initiatePayment(
            @Header("Authorization") String authHeader,
            @Body InitiatePaymentRequestBody requestBody
    );
}