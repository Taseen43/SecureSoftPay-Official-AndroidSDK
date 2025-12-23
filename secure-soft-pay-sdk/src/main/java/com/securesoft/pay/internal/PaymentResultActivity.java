package com.securesoft.pay.internal;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.securesoft.pay.SecureSoftPay;

public final class PaymentResultActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri data = getIntent().getData();
        if (data != null && data.getPath() != null) {
            String path = data.getPath();
            if ("/success".equals(path)) {
                String transactionId = data.getQueryParameter("transaction_id");
                if (transactionId != null && !transactionId.isEmpty()) {
                    SecureSoftPay.onPaymentSuccess(transactionId);
                } else {
                    SecureSoftPay.onPaymentFailure("Payment successful, but transaction ID was not returned.");
                }
            } else if ("/cancel".equals(path)) {
                SecureSoftPay.onPaymentFailure("Payment was cancelled by the user.");
            } else {
                SecureSoftPay.onPaymentFailure("An unknown payment redirect error occurred.");
            }
        } else {
            SecureSoftPay.onPaymentFailure("Invalid result data received from payment gateway.");
        }
        finish();
    }
}