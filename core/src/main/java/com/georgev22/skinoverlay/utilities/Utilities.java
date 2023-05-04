package com.georgev22.skinoverlay.utilities;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

public class Utilities {

    /**
     * Generates a deterministic UUID from a given seed using the SHA-256 hash function.
     *
     * @param seed the input seed used to generate the UUID
     * @return a UUID generated from the seed
     */
    @Contract("_ -> new")
    public static @NotNull UUID generateUUID(@NotNull String seed) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(seed.getBytes(StandardCharsets.UTF_8));
            byte[] hash = md.digest();
            long msb = 0;
            long lsb = 0;
            for (int i = 0; i < 8; i++)
                msb = (msb << 8) | (hash[i] & 0xff);
            for (int i = 8; i < 16; i++)
                lsb = (lsb << 8) | (hash[i] & 0xff);
            return new UUID(msb, lsb);
        } catch (NoSuchAlgorithmException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Decodes the specified Base64-encoded string into an object.
     *
     * @param bytesString the Base64-encoded string
     * @return the decoded object
     * @throws RuntimeException if the decoding fails for any reason
     */
    public static Object getObject(@NotNull String bytesString) {
        // Decode the Base64 string into bytes
        final byte[] bytes = Base64.getDecoder().decode(bytesString);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            // Read the object from the input stream
            return in.readObject();
        } catch (ClassNotFoundException | IOException e) {
            // Throw a runtime exception if the decoding fails
            throw new RuntimeException(e);
        }
    }

    /**
     * Encodes the specified object into a Base64-encoded string.
     *
     * @param object the object to encode
     * @return the Base64-encoded string
     * @throws RuntimeException if the encoding fails for any reason
     */
    public static String objectToString(Object object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            // Write the object to the output stream
            out.writeObject(object);
            final byte[] byteArray = bos.toByteArray();
            // Encode the byte array into a Base64 string
            return Base64.getEncoder().encodeToString(byteArray);
        } catch (IOException e) {
            // Throw a runtime exception if the encoding fails
            throw new RuntimeException(e);
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
