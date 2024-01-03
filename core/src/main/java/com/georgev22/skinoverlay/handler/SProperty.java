package com.georgev22.skinoverlay.handler;

import com.georgev22.library.yaml.serialization.ConfigurationSerializable;
import com.georgev22.library.yaml.serialization.SerializableAs;
import com.georgev22.skinoverlay.SkinOverlay;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Map;

/**
 * Represents a property with a name, value, and signature.
 */
@SerializableAs("SProperty")
public record SProperty(String name, String value, String signature) implements ConfigurationSerializable {

    /**
     * Constructs a new {@code SProperty} with the given name, value, and signature.
     *
     * @param name      the name of the property
     * @param value     the value of the property
     * @param signature the signature of the property
     */
    public SProperty {
    }

    /**
     * Returns the name of this property.
     *
     * @return the name of this property
     */
    public String name() {
        return name;
    }

    /**
     * Returns the value of this property.
     *
     * @return the value of this property
     */
    public String value() {
        return value;
    }

    /**
     * Returns the signature of this property.
     *
     * @return the signature of this property
     */
    public String signature() {
        return signature;
    }

    /**
     * Returns a string representation of this property.
     *
     * @return a string representation of this property
     */
    @Override
    public String toString() {
        return "SProperty{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }

    /**
     * Serializes the SProperty object to a JSON string.
     *
     * @return The serialized JSON string.
     */
    public String toJson() {
        return SkinOverlay.getInstance().getGson().toJson(this);
    }

    /**
     * Deserializes a JSON string to a SProperty object.
     *
     * @param json The JSON string to deserialize.
     * @return The deserialized SProperty object.
     */
    public static SProperty fromJson(String json) {
        return SkinOverlay.getInstance().getGson().fromJson(json, SProperty.class);
    }

    /**
     * Serializes the {@code SProperty} to a YAML-compatible map for serialization.
     *
     * @return A YAML-compatible map containing name, value, and signature information.
     */
    @Contract(pure = true)
    @Override
    public @NotNull @Unmodifiable Map<String, Object> serialize() {
        return Map.of("name", name, "value", value, "signature", signature);
    }

    /**
     * Deserializes the YAML-compatible map to an {@code SProperty}.
     *
     * @param map The YAML-compatible map containing name, value, and signature information.
     * @return The deserialized {@code SProperty}.
     */
    @Contract(pure = true)
    public static @NotNull SProperty deserialize(@NotNull Map<String, Object> map) {
        return new SProperty((String) map.get("name"), (String) map.get("value"), (String) map.get("signature"));
    }
}
