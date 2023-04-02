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

    @Contract("_ -> new")
    public static @NotNull SProperty propertyFromLinkedTreeMap(@NotNull LinkedTreeMap<String, String> linkedTreeMap) {
        return new SProperty(linkedTreeMap.get("name"), linkedTreeMap.get("value"), linkedTreeMap.get("signature"));
    }

    public static String skinOptionsToBytes(SkinOptions skinOptions) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream(); ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(skinOptions);
            final byte[] byteArray = bos.toByteArray();
            return Base64.getEncoder().encodeToString(byteArray);
        }
    }

    public static SkinOptions getSkinOptions(@NotNull String bytes) throws IOException, ClassNotFoundException {
        final byte[] skinOptionsBytes = Base64.getDecoder().decode(bytes);
        try (ByteArrayInputStream bis = new ByteArrayInputStream(skinOptionsBytes); ObjectInput in = new ObjectInputStream(bis)) {
            return (SkinOptions) in.readObject();
        }
    }

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


    public static class Request {
        private String address;
        private byte[] bytes;
        private int httpCode;
        private HttpsURLConnection httpsURLConnection;

        public Request getRequest() throws ProtocolException {
            this.httpsURLConnection.setRequestMethod("GET");
            this.httpsURLConnection.setRequestProperty("User-Agent", "SkinOverlay");
            return this;
        }

        public Request postRequest() throws ProtocolException {
            this.httpsURLConnection.setRequestMethod("POST");
            this.httpsURLConnection.setRequestProperty("Connection", "Keep-Alive");
            this.httpsURLConnection.setRequestProperty("Cache-Control", "no-cache");
            this.httpsURLConnection.setRequestProperty("User-Agent", "SkinOverlay");
            this.httpsURLConnection.setDoOutput(true);
            return this;
        }

        public Request openConnection(String address) throws IOException {
            this.address = address;
            final URL url = new URL(address);
            this.httpsURLConnection = (HttpsURLConnection) url.openConnection();
            return this;
        }

        public Request setRequestProperty(String key, String value) {
            this.httpsURLConnection.setRequestProperty(key, value);
            return this;
        }

        public Request writeToOutputStream(final String @NotNull ... data) throws IOException {
            for (final String str : data) {
                this.httpsURLConnection.getOutputStream().write(str.getBytes());
            }
            return this;
        }

        public Request writeToOutputStream(final byte @NotNull []... data) throws IOException {
            for (final byte[] bytes : data) {
                this.httpsURLConnection.getOutputStream().write(bytes);
            }
            return this;
        }

        public Request closeOutputStream() throws IOException {
            this.httpsURLConnection.getOutputStream().close();
            return this;
        }

        public Request finalizeRequest() throws IOException {
            this.httpCode = this.httpsURLConnection.getResponseCode();
            this.bytes = this.httpsURLConnection.getInputStream().readAllBytes();
            return this;
        }

        public int getHttpCode() {
            return this.httpCode;
        }

        public byte[] getBytes() {
            return this.bytes;
        }

        public String getAddress() {
            return this.address;
        }

        public HttpsURLConnection getHttpsURLConnection() {
            return httpsURLConnection;
        }
    }

}
