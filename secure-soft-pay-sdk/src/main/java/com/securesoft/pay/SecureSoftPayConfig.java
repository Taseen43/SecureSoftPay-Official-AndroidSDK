package com.securesoft.pay;

/**
 * Holds the necessary configuration for the Secure Soft Pay SDK.
 */
public final class SecureSoftPayConfig {
    public final String apiKey;
    public final String baseUrl;

    /**
     * @param apiKey The secret API Key obtained from your dashboard.
     * @param baseUrl The Base URL of your API endpoint (e.g., "http://192.168.0.101/dashboard_project").
     */
    public SecureSoftPayConfig(String apiKey, String baseUrl) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }
}