package com.securesoft.pay;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import com.securesoft.pay.internal.ApiClient;
import com.securesoft.pay.internal.ApiService;
import com.securesoft.pay.internal.InitiatePaymentRequestBody;
import com.securesoft.pay.internal.InitiatePaymentResponse;
import com.securesoft.pay.internal.PaymentActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Main entry point for the Secure Soft Pay SDK.
 */
public final class SecureSoftPay {

    private static SecureSoftPayConfig config;
    private static PaymentResultListener paymentCallback;

    private static final String CALLBACK_SCHEME = "com.securesoft.pay.callback";
    private static final String CALLBACK_HOST = "payment-result";

    private SecureSoftPay() {}

    /**
     * Initializes the SDK with your configuration.
     * This must be called once before any other SDK methods are used.
     */
    public static void initialize(@NonNull SecureSoftPayConfig config) {
        SecureSoftPay.config = config;
    }

    /**
     * Starts the payment process using the developer's custom user interface.
     */
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
                            launchPaymentActivity(context, body.paymentUrl);
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

    /**
     * Internal method to handle the successful payment result.
     */
    public static void onPaymentSuccess(String transactionId) {
        if (paymentCallback != null) {
            paymentCallback.onSuccess(transactionId);
            paymentCallback = null;
        }
    }

    /**
     * Internal method to handle the failed or cancelled payment result.
     */
    public static void onPaymentFailure(String errorMessage) {
        if (paymentCallback != null) {
            paymentCallback.onFailure(errorMessage);
            paymentCallback = null;
        }
    }

    private static void launchPaymentActivity(Context context, String url) {
        try {
            Intent intent = new Intent(context, PaymentActivity.class);
            intent.putExtra(PaymentActivity.EXTRA_URL, url);
            context.startActivity(intent);
        } catch (Exception e) {
            onPaymentFailure("Could not open the payment page. Error: " + e.getMessage());
        }
    }
}