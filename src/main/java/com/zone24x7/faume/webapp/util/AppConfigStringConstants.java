package com.zone24x7.faume.webapp.util;

/**
 * Publicly used String constants values are stored here.
 */
public final class AppConfigStringConstants {
    public static final String CONFIG_FACE_DATA_VERIFICATION_URL = "${face.verification.url}";
    public static final String CONFIG_PATTERN_ID = "${face.verification.pattern-id}";
    public static final String CONFIG_FACE_DATA_SAVE_TO_FILE = "${face.data.save-to-file}";
    public static final String CONFIG_REST_TEMPLATE_CONN_TIMEOUT_IN_MILLIS = "${spring.web-client.connection.timeout-in-millis}";
    public static final String CONFIG_REST_TEMPLATE_READ_TIMEOUT_IN_MILLIS = "${spring.web-client.read.timeout-in-millis}";
    public static final String CONFIG_INTEGRATION_APP_URL = "${face.integration-app.url}";
    public static final String CONFIG_INTEGRATION_APP_REQUEST_INFO_URL = "${face.integration-app.request-info-url}";
    public static final String CONFIG_INTEGRATION_APP_REQUEST_VERIFICATION_URL = "${face.integration-app.request-verification-url}";
    public static final String CONFIG_INTEGRATION_APP_DEVICE_BROWSER_INFO_URL = "${face.integration-app.device-browser-info-url}";
    public static final String CONFIG_INTEGRATION_APP_FACE_MATCH_RESULT_URL = "${face.integration-app.face-match-result-url}";
    public static final String CONFIG_CORS_ALLOWED_URLS = "${app.cross-origin.allowed-origins}";
    public static final String INTEGRATION_APP_API_KEYS = "${face.integration-app.api.key}";

    /**
     * Private constructor to stop initiation
     */
    private AppConfigStringConstants() {
        throw new IllegalStateException("AppConfigStringConstants is a utility class");
    }
}
