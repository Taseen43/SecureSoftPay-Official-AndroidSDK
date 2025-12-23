package com.example.securesoftpaysdkproject;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.securesoft.pay.PaymentRequest;
import com.securesoft.pay.PaymentResultListener;
import com.securesoft.pay.SecureSoftPay;
import com.securesoft.pay.SecureSoftPayConfig;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText nameInput, emailInput, amountInput;
    private Button payButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI উপাদানগুলো অ্যাক্সেস করা
        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        amountInput = findViewById(R.id.amountInput);
        payButton = findViewById(R.id.payButton);
        resultTextView = findViewById(R.id.resultTextView);

        // SDK চালু করা (Initialize)
        // গুরুত্বপূর্ণ: এই কাজটি সাধারণত Application ক্লাসে একবার করা হয়।
        // পরীক্ষার জন্য আমরা এটি এখানেই করছি।
        initializeSdk();

        // Pay Now বাটনে ক্লিক করলে পেমেন্ট প্রক্রিয়া শুরু হবে
        payButton.setOnClickListener(v -> initiatePayment());
    }

    private void initializeSdk() {
        // !!! গুরুত্বপূর্ণ !!!
        // নিচের apiKey এবং baseUrl আপনার নিজের ড্যাশবোর্ডের সঠিক তথ্য দিয়ে পরিবর্তন করুন।
        SecureSoftPayConfig config = new SecureSoftPayConfig(
                "dd04af6fe51aec9b971ac371c67ac9ca3b2926a27d74f40404dbc5e05ba23225",
                "https://pay.settings.top/api" // যেমন: "http://192.168.0.101/dashboard_project"
        );
        SecureSoftPay.initialize(config);
    }

    private void initiatePayment() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String amountStr = amountInput.getText().toString().trim();

        // ইনপুট ভ্যালিডেশন
        if (amountStr.isEmpty()) {
            amountInput.setError("Amount cannot be empty");
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            amountInput.setError("Invalid amount");
            return;
        }

        // ফলাফল দেখানোর জন্য TextView রিসেট করা
        resultTextView.setText("Initiating payment...");
        resultTextView.setTextColor(Color.BLACK);

        // 1. SDK-এর জন্য একটি পেমেন্ট রিকোয়েস্ট তৈরি করা
        PaymentRequest paymentRequest = new PaymentRequest(amount, name, email);

        // 2. পেমেন্ট শুরু করা এবং ফলাফল পাওয়ার জন্য listener সেট করা
        SecureSoftPay.startPayment(this, paymentRequest, new PaymentResultListener() {
            @Override
            public void onSuccess(String transactionId) {
                // পেমেন্ট সফল হলে এই মেথডটি কল হবে
                String successMessage = "Payment Successful!\nTransaction ID: " + transactionId;
                resultTextView.setText(successMessage);
                resultTextView.setTextColor(Color.parseColor("#28a745")); // Green color
                Toast.makeText(MainActivity.this, "Payment Successful!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                // পেমেন্ট ব্যর্থ বা বাতিল হলে এই মেথডটি কল হবে
                String failureMessage = "Payment Failed:\n" + errorMessage;
                resultTextView.setText(failureMessage);
                resultTextView.setTextColor(Color.parseColor("#dc3545")); // Red color
                Toast.makeText(MainActivity.this, "Payment Failed!", Toast.LENGTH_LONG).show();
            }
        });
    }
}