package com.georgev22.skinoverlay.utilities;

import org.jetbrains.annotations.NotNull;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Utility class providing AES-GCM encryption and decryption with password-based key derivation,
 * combined with GZIP compression for efficient storage and transmission.
 */
public class EncryptUtils {

    private static final int SALT_LENGTH = 16;
    private static final int IV_LENGTH = 12;
    private static final int KEY_LENGTH = 256;
    private static final int TAG_LENGTH = 128;
    private static final int ITERATIONS = 65536;

    /**
     * Encrypts the given plaintext using AES-GCM with a password-derived key and GZIP compression.
     * <p>
     * The output includes the salt, IV, and ciphertext concatenated and Base64-encoded.
     *
     * @param plaintext The plaintext string to encrypt
     * @param password  The password used for key derivation (PBKDF2 with HMAC-SHA256)
     * @return A Base64-encoded string containing salt, IV, and ciphertext
     * @throws GeneralSecurityException If encryption or key derivation fails
     * @throws IOException              If compression fails
     */
    public static @NotNull String encrypt(@NotNull String plaintext, @NotNull String password)
            throws GeneralSecurityException, IOException {

        byte[] salt = new byte[SALT_LENGTH];
        byte[] iv = new byte[IV_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(salt);
        random.nextBytes(iv);

        SecretKey key = deriveKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, gcmSpec);

        byte[] compressed = compress(plaintext.getBytes(StandardCharsets.UTF_8));
        byte[] encrypted = cipher.doFinal(compressed);

        byte[] output = new byte[SALT_LENGTH + IV_LENGTH + encrypted.length];
        System.arraycopy(salt, 0, output, 0, SALT_LENGTH);
        System.arraycopy(iv, 0, output, SALT_LENGTH, IV_LENGTH);
        System.arraycopy(encrypted, 0, output, SALT_LENGTH + IV_LENGTH, encrypted.length);

        return Base64.getEncoder().encodeToString(output);
    }

    /**
     * Decrypts the given Base64-encoded string containing salt, IV, and ciphertext,
     * returning the original plaintext after GZIP decompression.
     *
     * @param encryptedText The Base64-encoded string to decrypt
     * @param password      The password used for key derivation (must match encryption password)
     * @return The decrypted plaintext string
     * @throws GeneralSecurityException If decryption or key derivation fails
     * @throws IOException              If decompression fails
     */
    public static @NotNull String decrypt(@NotNull String encryptedText, @NotNull String password)
            throws GeneralSecurityException, IOException {

        byte[] decoded = Base64.getDecoder().decode(encryptedText);

        byte[] salt = Arrays.copyOfRange(decoded, 0, SALT_LENGTH);
        byte[] iv = Arrays.copyOfRange(decoded, SALT_LENGTH, SALT_LENGTH + IV_LENGTH);
        byte[] ciphertext = Arrays.copyOfRange(decoded, SALT_LENGTH + IV_LENGTH, decoded.length);

        SecretKey key = deriveKey(password, salt);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH, iv);
        cipher.init(Cipher.DECRYPT_MODE, key, gcmSpec);

        byte[] compressed = cipher.doFinal(ciphertext);
        byte[] decompressed = decompress(compressed);

        return new String(decompressed, StandardCharsets.UTF_8);
    }

    /**
     * Derives an AES SecretKey from the given password and salt using PBKDF2 with HMAC-SHA256.
     *
     * @param password The password to derive the key from
     * @param salt     The salt used for key derivation
     * @return A SecretKey suitable for AES encryption
     * @throws NoSuchAlgorithmException If the PBKDF2 algorithm is unavailable
     * @throws InvalidKeySpecException  If the key specification is invalid
     */
    private static @NotNull SecretKey deriveKey(@NotNull String password, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Compresses the given byte array using GZIP.
     *
     * @param data The data to compress
     * @return Compressed byte array
     * @throws IOException If compression fails
     */
    private static byte @NotNull [] compress(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(bos)) {
            gzip.write(data);
        }
        return bos.toByteArray();
    }

    /**
     * Decompresses the given GZIP-compressed byte array.
     *
     * @param data The compressed data to decompress
     * @return Decompressed byte array
     * @throws IOException If decompression fails
     */
    private static byte @NotNull [] decompress(byte[] data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data))) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = gzip.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
        }
        return bos.toByteArray();
    }
}
