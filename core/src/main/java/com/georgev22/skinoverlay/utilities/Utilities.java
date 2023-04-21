package com.georgev22.skinoverlay.utilities;

import com.georgev22.skinoverlay.handler.SProperty;
import com.google.gson.internal.LinkedTreeMap;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;

public class Utilities {

    /**
     * Creates an {@link SProperty} object from a {@link LinkedTreeMap} of strings.
     *
     * @param linkedTreeMap the linked tree map to create the SProperty object from
     * @return the created SProperty object
     * @deprecated This method is deprecated.
     */
    @Deprecated
    @Contract("_ -> new")
    public static @NotNull SProperty propertyFromLinkedTreeMap(@NotNull LinkedTreeMap<String, String> linkedTreeMap) {
        return new SProperty(linkedTreeMap.get("name"), linkedTreeMap.get("value"), linkedTreeMap.get("signature"));
    }

    /**
     * Converts a {@link SkinOptions} object to a Base64-encoded string.
     *
     * @param skinOptions the SkinOptions object to convert
     * @return the Base64-encoded string
     * @throws IOException if an I/O error occurs
     */
    public static String skinOptionsToBytes(SkinOptions skinOptions) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(skinOptions);
            final byte[] byteArray = bos.toByteArray();
            return Base64.getEncoder().encodeToString(byteArray);
        }
    }

    /**
     * Converts a Base64-encoded string to a {@link SkinOptions} object.
     *
     * @param bytes the Base64-encoded string to convert
     * @return the SkinOptions object
     * @throws IOException            if an I/O error occurs
     * @throws ClassNotFoundException if the class of a serialized object cannot be found
     */
    public static SkinOptions getSkinOptions(@NotNull String bytes) throws IOException, ClassNotFoundException {
        final byte[] skinOptionsBytes = Base64.getDecoder().decode(bytes);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(skinOptionsBytes); ObjectInput in = new ObjectInputStream(bis)) {
            return (SkinOptions) in.readObject();
        }
    }

    /**
     * Decrypts a Base64-encoded string using AES encryption.
     *
     * @param encryptedText the Base64-encoded string to decrypt
     * @return the decrypted string, or null if decryption failed
     */
    public static @Nullable String decrypt(String encryptedText) {
        try {
            byte[] salt = Arrays.copyOfRange(Base64.getDecoder().decode(encryptedText), 0, 16);
            KeySpec spec = new PBEKeySpec(OptionsUtil.SECRET.getStringValue().toCharArray(), salt, 65536, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] encryptedBytes = Arrays.copyOfRange(Base64.getDecoder().decode(encryptedText), 16, Base64.getDecoder().decode(encryptedText).length);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Encrypts a string using AES encryption and returns the result as a Base64-encoded string.
     *
     * @param plaintext the string to encrypt
     * @return the Base64-encoded encrypted string, or null if encryption failed
     */
    public static @Nullable String encrypt(String plaintext) {
        try {
            byte[] salt = new byte[16];
            SecureRandom random = new SecureRandom();
            random.nextBytes(salt);
            KeySpec spec = new PBEKeySpec(OptionsUtil.SECRET.getStringValue().toCharArray(), salt, 65536, 256);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] keyBytes = factory.generateSecret(spec).getEncoded();
            SecretKey key = new SecretKeySpec(keyBytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
            byte[] combined = new byte[16 + encryptedBytes.length];
            System.arraycopy(salt, 0, combined, 0, 16);
            System.arraycopy(encryptedBytes, 0, combined, 16, encryptedBytes.length);
            return Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * The Request class provides methods for making HTTPS requests.
     */
    public static class Request {
        /**
         * The address of the HTTP(S) endpoint.
         */
        private String address;

        /**
         * The bytes returned from the HTTP(S) response.
         */
        private byte[] bytes;

        /**
         * The HTTP status code returned by the endpoint.
         */
        private int httpCode;

        /**
         * The HTTPS URL connection used to make the request.
         */
        private HttpsURLConnection httpsURLConnection;

        /**
         * Sets the HTTP request method to "GET".
         *
         * @return This Request object.
         * @throws ProtocolException If the request method could not be set.
         */
        public Request getRequest() throws ProtocolException {
            this.httpsURLConnection.setRequestMethod("GET");
            this.httpsURLConnection.setRequestProperty("User-Agent", "SkinOverlay");
            return this;
        }

        /**
         * Sets the HTTP request method to "POST" and sets several request properties.
         *
         * @return This Request object.
         * @throws ProtocolException If the request method could not be set.
         */
        public Request postRequest() throws ProtocolException {
            this.httpsURLConnection.setRequestMethod("POST");
            this.httpsURLConnection.setRequestProperty("Connection", "Keep-Alive");
            this.httpsURLConnection.setRequestProperty("Cache-Control", "no-cache");
            this.httpsURLConnection.setRequestProperty("User-Agent", "SkinOverlay");
            this.httpsURLConnection.setDoOutput(true);
            return this;
        }

        /**
         * Opens a connection to the specified HTTP(S) endpoint.
         *
         * @param address The address of the endpoint.
         * @return This Request object.
         * @throws IOException If a connection to the endpoint could not be established.
         */
        public Request openConnection(String address) throws IOException {
            this.address = address;
            final URL url = new URL(address);
            this.httpsURLConnection = (HttpsURLConnection) url.openConnection();
            return this;
        }

        /**
         * Sets a request property to the specified key-value pair.
         *
         * @param key   The key for the property.
         * @param value The value for the property.
         * @return This Request object.
         */
        public Request setRequestProperty(String key, String value) {
            this.httpsURLConnection.setRequestProperty(key, value);
            return this;
        }

        /**
         * Writes one or more strings to the request output stream.
         *
         * @param data The strings to write.
         * @return This Request object.
         * @throws IOException If an I/O error occurs.
         */
        public Request writeToOutputStream(final String @NotNull ... data) throws IOException {
            for (final String str : data) {
                this.httpsURLConnection.getOutputStream().write(str.getBytes());
            }
            return this;
        }

        /**
         * Writes one or more byte arrays to the request output stream.
         *
         * @param data The byte arrays to write.
         * @return This Request object.
         * @throws IOException If an I/O error occurs.
         */
        public Request writeToOutputStream(final byte @NotNull []... data) throws IOException {
            for (final byte[] bytes : data) {
                this.httpsURLConnection.getOutputStream().write(bytes);
            }
            return this;
        }

        /**
         * Closes the request output stream.
         *
         * @return This Request object.
         * @throws IOException If an I/O error occurs.
         */
        public Request closeOutputStream() throws IOException {
            this.httpsURLConnection.getOutputStream().close();
            return this;
        }

        /**
         * Finalizes the request by getting the HTTP response code and reading the response body bytes.
         *
         * @return The updated Request object.
         * @throws IOException If an I/O error occurs while finalizing the request.
         */
        public Request finalizeRequest() throws IOException {
            this.httpCode = this.httpsURLConnection.getResponseCode();
            this.bytes = this.httpsURLConnection.getInputStream().readAllBytes();
            return this;
        }

        /**
         * Gets the HTTP response code of the request.
         *
         * @return The HTTP response code.
         */
        public int getHttpCode() {
            return this.httpCode;
        }

        /**
         * Gets the bytes of the response body of the request.
         *
         * @return The response body bytes.
         */
        public byte[] getBytes() {
            return this.bytes;
        }

        /**
         * Gets the address of the request.
         *
         * @return The request address.
         */
        public String getAddress() {
            return this.address;
        }

        /**
         * Gets the underlying HTTPS connection object.
         *
         * @return The HTTPS connection object.
         */
        public HttpsURLConnection getHttpsURLConnection() {
            return httpsURLConnection;
        }
    }

}
