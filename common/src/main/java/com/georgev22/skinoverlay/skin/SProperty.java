package com.georgev22.skinoverlay.skin;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/**
 * Represents a property associated with a Minecraft game profile.
 *
 * <p>A property consists of a name, a value, and an optional signature.
 * Signatures may be used to verify the authenticity of signed profile
 * properties, such as skin textures.</p>
 *
 * @param name      the name of the property
 * @param value     the value of the property
 * @param signature the signature associated with the property, or
 *                  {@code null} if the property is unsigned
 */
public record SProperty(String name, String value, String signature) {

    /**
     * Returns a SHA-256 fingerprint of this property's UTF-8 value.
     * <p>
     * For a textures property, this identifies the complete encoded texture payload, including
     * model and cape metadata. The name and signature are excluded: a different signature for
     * the same value does not require another skin update. This compares payloads without
     * downloading images and does not verify the property's signature.
     * </p>
     *
     * @return a lowercase, 64-character hexadecimal digest of {@link #value()}
     */
    public String fingerprint() {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}