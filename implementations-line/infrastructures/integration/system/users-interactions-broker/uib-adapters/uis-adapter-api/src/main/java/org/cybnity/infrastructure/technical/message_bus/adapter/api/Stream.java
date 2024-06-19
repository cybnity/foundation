package org.cybnity.infrastructure.technical.message_bus.adapter.api;

import org.cybnity.framework.INaming;

/**
 * A stream is a data structure that acts like an append-only log but also implements several operations to overcome some of the limits of a typical append-only log.
 * Stream can be used to record and simultaneously syndicate event in real time (e.g capability domain feature execution; event sourcing of facts).
 * A stream deliver a guarantee over persistence of managed events.
 * Each stream entry is uniquely identified by an auto-generated identifier. IDs can be used to retrieve their associated entries later or to read and process all subsequent entries in the stream.
 */
public class Stream implements INaming {

    /**
     * Type of attribute allowing to define a stream specification.
     * A specification can be useful for indexing of stream items.
     */
    public enum Specification {
        /**
         * Attribute name defining a stream's entrypoint path (e.g subdomain or feature name) which can be used to define (e.g into a command event) a recipient stream name.
         */
        STREAM_ENTRYPOINT_PATH_NAME,

        /**
         * Key name regarding the identification of any fact record appended, stored and retrieved by a Stream.
         */
        FACT_RECORD_ID_KEY_NAME,

        /**
         * Key name regarding the identification of any origin subject (e.g domain aggregate identifier), stored and retrieved by a Stream.
         */
        ORIGIN_SUBJECT_ID_KEY_NAME,

        /**
         * Key name regarding an information (e.g event payload) regarding any fact record stored, and retrieved by a Stream.
         */
        MESSAGE_PAYLOAD_KEY_NAME
    }

    private final String name;

    /**
     * Default constructor.
     *
     * @param streamName Mandatory name of this stream.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public Stream(String streamName) throws IllegalArgumentException {
        if (streamName == null || streamName.isEmpty())
            throw new IllegalArgumentException("Stream name parameter is required!");
        this.name = streamName;
    }

    /**
     * Get the name of this stream.
     *
     * @return A name.
     */
    @Override
    public String name() {
        return this.name;
    }

    /**
     * Equals implementation based on the stream name.
     *
     * @param obj To check.
     * @return False by default. True when equals stream name.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof Stream) {
            Stream other = (Stream) obj;
            return this.name().equals(other.name());
        }
        return false;
    }
}
