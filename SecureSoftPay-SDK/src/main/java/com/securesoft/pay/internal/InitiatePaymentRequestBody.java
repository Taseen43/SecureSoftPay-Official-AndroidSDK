package com.securesoft.pay.internal;

import com.google.gson.annotations.SerializedName;

// এই ক্লাসটি এখন তার নিজের ফাইলে আছে
public final class InitiatePaymentRequestBody {
    final double amount;
    @SerializedName("client_base_url")
    final String clientBaseUrl;
    @SerializedName("customer_name")
    final String customerName;
    @SerializedName("customer_email")
    final String customerEmail;
    @SerializedName("success_url")
    final String successUrl;
    @SerializedName("cancel_url")
    final String cancelUrl;

    public InitiatePaymentRequestBody(double amount, String clientBaseUrl, String customerName, String customerEmail, String successUrl, String cancelUrl) {
        this.amount = amount;
        this.clientBaseUrl = clientBaseUrl;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
    }
}