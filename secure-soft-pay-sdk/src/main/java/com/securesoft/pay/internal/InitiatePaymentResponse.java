package com.securesoft.pay.internal;

import com.google.gson.annotations.SerializedName;

// এই ক্লাসটিও এখন তার নিজের ফাইলে আছে
public final class InitiatePaymentResponse {
    public String status;
    @SerializedName("payment_url")
    public String paymentUrl;
    public String message;
}