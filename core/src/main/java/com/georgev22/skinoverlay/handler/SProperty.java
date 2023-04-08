package com.georgev22.skinoverlay.handler;

/**
 * Represents a property with a name, value, and signature.
 */
public record SProperty(String name, String value, String signature) {

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
}
