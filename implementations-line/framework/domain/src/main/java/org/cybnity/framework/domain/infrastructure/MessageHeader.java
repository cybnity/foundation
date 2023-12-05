package org.cybnity.framework.domain.infrastructure;

/**
 * Common message header allowing optional attribute adding on message that can support processing.
 */
public enum MessageHeader {

    /**
     * Header regarding a response path (e.g specific channel of message sender observed and based on a transaction identifier according to a middleware used).
     */
    REPLY_ADDRESS_HEADER;
}
