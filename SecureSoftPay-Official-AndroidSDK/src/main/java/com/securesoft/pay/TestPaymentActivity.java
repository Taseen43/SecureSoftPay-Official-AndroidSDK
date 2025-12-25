package com.securesoft.pay;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

/**
 * A built-in Activity within the SDK to provide a quick way for developers
 * to test their configuration (API Key, Base URL) and the payment flow.
 * This activity is launched via `SecureSoftPay.launchTestMode()`.
 */
public final class TestPaymentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the layout for this activity from the SDK's resources
        setContentView(R.layout.activity_test_payment);

        // Initialize UI components from the layout
        TextInputEditText nameInput = findViewById(R.id.nameInput);
        TextInputEditText emailInput = findViewById(R.id.emailInput);
        TextInputEditText amountInput = findViewById(R.id.amountInput);
        Button startTestPaymentButton = findViewById(R.id.startTestPaymentButton);

        // Set a click listener on the button
        startTestPaymentButton.setOnClickListener(v -> {
            String name = "";
            if (nameInput.getText() != null) {
                name = nameInput.getText().toString().trim();
            }

            String email = "";
            if (emailInput.getText() != null) {
                email = emailInput.getText().toString().trim();
            }

            String amountStr = "";
            if (amountInput.getText() != null) {
                amountStr = amountInput.getText().toString().trim();
            }

            // Basic validation for the amount
            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Amount cannot be empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Please enter a valid amount.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a PaymentRequest object with the data from the form
            PaymentRequest request = new PaymentRequest(amount, name, email);

            // Retrieve the original listener that the developer provided
            PaymentResultListener listener = SecureSoftPay.getPaymentCallback();

            if (listener == null) {
                // This case should ideally not happen if called from launchTestMode
                Toast.makeText(this, "Error: Payment listener is not available.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            // Call the main startPayment method to begin the actual payment process
            SecureSoftPay.startPayment(this, request, listener);

            // Close this test activity, as the payment flow will now be handled by PaymentActivity
            finish();
        });
    }
}