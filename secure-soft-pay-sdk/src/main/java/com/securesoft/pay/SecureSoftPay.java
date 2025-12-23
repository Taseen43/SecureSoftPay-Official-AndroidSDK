package com.securesoft.pay;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;

import com.securesoft.pay.internal.ApiClient;
import com.securesoft.pay.internal.ApiService;
import com.securesoft.pay.internal.InitiatePaymentRequestBody;
import com.securesoft.pay.internal.InitiatePaymentResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public final class SecureSoftPay {

    private static SecureSoftPayConfig config;
    private static PaymentResultListener paymentCallback;

    private static final String CALLBACK_SCHEME = "com.securesoft.pay.callback";
    private static final String CALLBACK_HOST = "payment-result";

    private SecureSoftPay() {}

    public static void initialize(SecureSoftPayConfig config) {
        SecureSoftPay.config = config;
    }

    public static void startPayment(@NonNull Context context, @NonNull PaymentRequest request, @NonNull PaymentResultListener callback) {
        if (config == null) {
            callback.onFailure("SDK not initialized. Please call SecureSoftPay.initialize() first.");
            return;
        }
        paymentCallback = callback;

        ApiService apiService = ApiClient.create(config.baseUrl);

        String successUrl = CALLBACK_SCHEME + "://" + CALLBACK_HOST + "/success";
        String cancelUrl = CALLBACK_SCHEME + "://" + CALLBACK_HOST + "/cancel";

        InitiatePaymentRequestBody requestBody = new InitiatePaymentRequestBody(
                request.amount,
                config.baseUrl,
                request.customerName,
                request.customerEmail,
                successUrl,
                cancelUrl
        );

        apiService.initiatePayment("Bearer " + config.apiKey, requestBody).enqueue(new Callback<InitiatePaymentResponse>() {
            @Override
            public void onResponse(@NonNull Call<InitiatePaymentResponse> call, @NonNull Response<InitiatePaymentResponse> response) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    InitiatePaymentResponse body = response.body();
                    if (response.isSuccessful() && body != null && "success".equals(body.status)) {
                        if (body.paymentUrl != null && !body.paymentUrl.isEmpty()) {
                            launchChromeCustomTab(context, body.paymentUrl);
                        } else {
                            onPaymentFailure("API did not return a valid payment_url.");
                        }
                    } else {
                        String errorMsg = (body != null && body.message != null) ? body.message : "Failed to initiate payment. Code: " + response.code();
                        onPaymentFailure(errorMsg);
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<InitiatePaymentResponse> call, @NonNull Throwable t) {
                new Handler(Looper.getMainLooper()).post(() -> onPaymentFailure("A network error occurred: " + t.getMessage()));
            }
        });
    }

    // ★★★ পরিবর্তন এখানে ★★★
    // মেথডগুলোকে 'public' করা হয়েছে
    public static void onPaymentSuccess(String transactionId) {
        if (paymentCallback != null) {
            paymentCallback.onSuccess(transactionId);
            paymentCallback = null;
        }
    }

    // ★★★ পরিবর্তন এখানে ★★★
    // মেথডগুলোকে 'public' করা হয়েছে
    public static void onPaymentFailure(String errorMessage) {
        if (paymentCallback != null) {
            paymentCallback.onFailure(errorMessage);
            paymentCallback = null;
        }
    }

    private static void launchChromeCustomTab(Context context, String url) {
        try {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
            customTabsIntent.launchUrl(context, Uri.parse(url));
        } catch (Exception e) {
            onPaymentFailure("Could not open checkout page. A web browser is required.");
        }
    }
}