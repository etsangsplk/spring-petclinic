package org.springframework.samples.tracing;

import javax.validation.constraints.NotNull;
import java.util.NoSuchElementException;
import java.util.Objects;

final class ConfigUtil {
    private static final String keyNotNullMessage = "key %s cannot be null";
    private static final String exceptionMessageFmt = "environment key %s returns no value";

    /**
     * Get the required value for  environment key.
     * @param key key in String to get environment values from.
     * @return a String
     */
    public static String getRequiredEnv(@NotNull String key) {
        Objects.requireNonNull(key, String.format(keyNotNullMessage, key));
        String val = System.getenv(key);
        if (val.isEmpty()) {
            throw new NoSuchElementException(String.format(exceptionMessageFmt, key));
        }
        return val;
    }

    /**
     * Get the optional boolean value for  environment key.
     * @param key key to get environment values from.
     * @return a Boolean
     */
    public static boolean getRequiredBoolean(@NotNull String key) {
        Objects.requireNonNull(key, String.format(keyNotNullMessage, key));
        String val = System.getenv(key);
        if (val.isEmpty()) {
            throw new NoSuchElementException(String.format(exceptionMessageFmt, key));
        }
        return Boolean.parseBoolean(val);
    }

    /**
     * Get the required integer value for  environment key.
     * @param key key to get environment values from.
     * @return an Integer
     */
    public static int getRequiredInt(@NotNull String key) {
        Objects.requireNonNull(key, String.format(keyNotNullMessage, key));
        String val = System.getenv(key);
        if (val.isEmpty()) {
            throw new NoSuchElementException(String.format(exceptionMessageFmt, key));
        }
        return Integer.parseInt(val);
    }

    /**
     * Get the optional value for  environment key. Returns an empty string by default.
     * @param key key to get environment values from.
     * @param defaultValue default value to return if none found from environment.
     * @return a String
     */
    public static String getOptionalEnv(@NotNull String key, String defaultValue) {
        Objects.requireNonNull(key, String.format(keyNotNullMessage, key));
        return System.getenv().getOrDefault(key, defaultValue);
    }

    /**
     * Get the optional boolean value for  environment key. Returns user defined default value.
     * @param key to get environment values from.
     * @param defaultValue default value to return if none found from environment.
     * @return a Boolean
     */
    public static boolean getOptionalBoolean(@NotNull String key, boolean defaultValue) {
        Objects.requireNonNull(key, String.format(keyNotNullMessage, key));
        String val  = System.getenv(key);
        return val != null ? Boolean.parseBoolean(val) : defaultValue;
    }

    /**
     * Get the optional integer value for  environment key. Returns user defined default value.
     * @param key to get environment values from.
     * @param defaultValue default value to return if none found from environment.
     * @return an Integer
     */
    public static int getOptionalInt(@NotNull String key, int defaultValue) {
        Objects.requireNonNull(key, String.format(keyNotNullMessage, key));
        String val  = System.getenv(key);
        return val != null ? Integer.parseInt(val) : defaultValue;
    }

}
