package com.zone24x7.faume.webapp.util;

/**
 * Publicly used String constants values are stored here.
 */
public final class AppConfigStringConstants {
    public static final String CONFIG_FACE_DATA_VERIFICATION_URL = "${face.verification.url}";
    public static final String CONFIG_ACCOUNT_ID = "${face.verification.account-id}";
    public static final String CONFIG_PROFILE_COUNT = "${face.verification.profile-count}";
    public static final String CONFIG_PATTERN_ID = "${face.verification.pattern-id}";
    public static final String CONFIG_FACE_DATA_SAVE_TO_FILE = "${face.data.save-to-file}";
    public static final String CONFIG_REST_TEMPLATE_CONN_TIMEOUT_IN_MILLIS = "${spring.rest-template.connection.timeout-in-millis}";
    public static final String CONFIG_REST_TEMPLATE_READ_TIMEOUT_IN_MILLIS = "${spring.rest-template.read.timeout-in-millis}";
    public static final String CONFIG_INTEGRATION_APP_URL = "${face.integration-app.url}";
    public static final String CONFIG_CORS_ALLOWED_URLS = "${app.cross-origin.allowed-origins}";
    public static final String INTEGRATION_APP_API_KEYS = "${face.integration-app.api.key}";

    /**
     * Private constructor to stop initiation
     */
    private AppConfigStringConstants() {
        throw new IllegalStateException("AppConfigStringConstants is a utility class");
    }
}
