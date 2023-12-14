package org.cybnity.infrastructure.technical.message_bus.adapter.api;

/**
 * A stream is a data structure that acts like an append-only log but also implements several operations to overcome some of the limits of a typical append-only log.
 * Stream can be used to record and simultaneously syndicate event in real time (e.g capability domain feature execution; event sourcing of facts).
 * A stream deliver a guarantee over persistence of managed events.
 * Each stream entry is uniquely identified by an auto-generated identifier. IDs can be used to retrieve their associated entries later or to read and process all subsequent entries in the stream.
 */
public class Stream {

    /**
     * Type of attribute allowing to define a stream specification.
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
         * Key name regarding an information (e.g event payload) regarding any fact record stored, and retrieved by a Stream.
         */
        MESSAGE_PAYLOAD_KEY_NAME;
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
    public String name() {
        return this.name;
    }

}