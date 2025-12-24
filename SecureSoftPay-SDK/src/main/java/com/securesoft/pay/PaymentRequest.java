package com.securesoft.pay;

/**
 * Represents a payment request to be initiated.
 */
public final class PaymentRequest {
    public final double amount;
    public final String customerName;
    public final String customerEmail;

    /**
     * @param amount The total amount to be paid.
     * @param customerName The full name of the customer (optional).
     * @param customerEmail The email address of the customer (optional).
     */
    public PaymentRequest(double amount, String customerName, String customerEmail) {
        this.amount = amount;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
    }

    public PaymentRequest(double amount) {
        this(amount, null, null);
    }
}