package com.georgev22.skinoverlay.skin;

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
}