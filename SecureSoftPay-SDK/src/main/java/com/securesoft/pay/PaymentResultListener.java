package com.securesoft.pay;

/**
 * A listener interface to receive the result of a payment operation.
 */
public interface PaymentResultListener {
    /**
     * Called when the payment is successful.
     * @param transactionId The unique transaction ID from the payment gateway.
     */
    void onSuccess(String transactionId);

    /**
     * Called when the payment fails, is cancelled, or an error occurs.
     * @param errorMessage A descriptive error message.
     */
    void onFailure(String errorMessage);
}