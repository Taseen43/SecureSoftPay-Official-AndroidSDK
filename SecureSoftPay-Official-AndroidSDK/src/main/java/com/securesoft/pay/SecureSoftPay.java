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
 * This class provides methods to initialize the SDK, start a payment from your custom UI,
 * or launch a built-in test UI for quick integration checks.
 */
public final class SecureSoftPay {

    private static SecureSoftPayConfig config;
    private static PaymentResultListener paymentCallback;

    private static final String CALLBACK_SCHEME = "com.securesoft.pay.callback";
    private static final String CALLBACK_HOST = "payment-result";

    // Private constructor to prevent instantiation of this utility class.
    private SecureSoftPay() {}

    /**
     * Initializes the SDK with your configuration.
     * This method must be called once, typically in your Application's onCreate(),
     * before any other SDK methods are used.
     *
     * @param config The configuration object containing your API Key and Base URL.
     */
    public static void initialize(@NonNull SecureSoftPayConfig config) {
        SecureSoftPay.config = config;
    }

    /**
     * Starts the payment process using your custom user interface.
     * This method initiates the payment with the provided details and opens the
     * in-app WebView for the user to complete the transaction.
     *
     * @param context The current Android context (e.g., your Activity).
     * @param request The payment request details, including amount and customer info.
     * @param callback A listener that will be invoked with the payment result (Success or Failure).
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
                // Ensure UI updates are on the main thread
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
                new Handler(Looper.getMainLooper()).post(() -> {
                    onPaymentFailure("A network error occurred: " + t.getMessage());
                });
            }
        });
    }

    /**
     * Launches a built-in test screen to quickly verify the SDK integration.
     * This method presents a pre-made form to input payment details and initiates the payment flow.
     * It's ideal for developers to check if their credentials and server connection are working correctly
     * before building a custom UI. The result is delivered to the same listener provided here.
     *
     * @param context The current Android context (e.g., your Activity).
     * @param callback A listener to receive the payment result (Success or Failure).
     */
    public static void launchTestMode(@NonNull Context context, @NonNull PaymentResultListener callback) {
        if (config == null) {
            callback.onFailure("SDK not initialized. Please call SecureSoftPay.initialize() first.");
            return;
        }
        paymentCallback = callback;
        Intent intent = new Intent(context, TestPaymentActivity.class);
        context.startActivity(intent);
    }

    /**
     * Internal method to be called by PaymentResultActivity when a payment is successful.
     * This should not be called directly by the developer.
     * @param transactionId The transaction ID from the payment gateway.
     */
    public static void onPaymentSuccess(String transactionId) {
        if (paymentCallback != null) {
            paymentCallback.onSuccess(transactionId);
            paymentCallback = null; // Clear callback to prevent memory leaks and multiple calls
        }
    }

    /**
     * Internal method to be called by PaymentResultActivity when a payment fails or is cancelled.
     * This should not be called directly by the developer.
     * @param errorMessage A descriptive error message.
     */
    public static void onPaymentFailure(String errorMessage) {
        if (paymentCallback != null) {
            paymentCallback.onFailure(errorMessage);
            paymentCallback = null; // Clear callback
        }
    }

    /**
     * Internal helper method for TestPaymentActivity to access the stored callback.
     * This should not be called directly by the developer.
     */
    static PaymentResultListener getPaymentCallback() {
        return paymentCallback;
    }

    /**
     * Private helper method to launch the internal WebView activity.
     */
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