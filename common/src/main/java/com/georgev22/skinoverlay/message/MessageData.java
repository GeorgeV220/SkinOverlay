package com.georgev22.skinoverlay.message;

/**
 * Represents parsed message data with sub-channel and data entries.
 *
 * @param subChannel  the sub-channel string
 * @param dataEntries the decrypted data entries
 */
public record MessageData(String subChannel, String[] dataEntries) {
}
