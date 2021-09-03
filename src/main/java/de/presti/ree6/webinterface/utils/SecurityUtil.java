package de.presti.ree6.webinterface.utils;

import org.apache.commons.codec.binary.Base64;

import java.util.Random;

public class SecurityUtil {

    /**
     * Decrypt a Base64 String.
     *
     * @param in       The Target String.
     *
     * @return string  Returns the decrypted String.
     */
    public static String de(String in) {
        try {
            return new String(Base64.decodeBase64(in));
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Encrypt a Base64 String.
     *
     * @param in     The Target String.
     *
     * @return string  Returns the encrypted String.
     */
    public static String en(String in) {
        try {
            return Base64.encodeBase64String(in.getBytes());
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Generate a custom length random String.
     *
     * @param length   The Target Length.
     *
     * @return string  Returns a randomly generated String.
     */
    public static String randomString(int length) {
        return random(length, "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz@/+§!$()=?]}{[");
    }

    /**
     * Generate a custom length random String with custom chars.
     *
     * @param length   The Target Length.
     * @param chars    The Charset as String which should be used.
     *
     * @return string  Returns a randomly generated String.
     */
    public static String random(int length, String chars) {
        return random(length, chars.toCharArray());
    }

    /**
     * Generate a custom length random String with custom chars.
     *
     * @param length   The Target Length.
     * @param chars    The Charset as Char Collection which should be used.
     *
     * @return string  Returns a randomly generated String.
     */
    public static String random(int length, char[] chars) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; ++i) {
            stringBuilder.append(chars[new Random().nextInt(chars.length)]);
        }
        return stringBuilder.toString();
    }

}