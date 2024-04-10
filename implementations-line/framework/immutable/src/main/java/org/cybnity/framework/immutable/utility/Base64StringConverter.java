package org.cybnity.framework.immutable.utility;

import java.io.*;
import java.util.Base64;
import java.util.Optional;

/**
 * Transformer of object to string and reverse method utility class.
 */
public class Base64StringConverter {

    private Base64StringConverter() {
    }

    /**
     * Transform an object into a Base64 encoded string.
     *
     * @param object Mandatory object to serialize.
     * @return Base 64 string version.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public static Optional<String> convertToString(final Serializable object) throws IllegalArgumentException {
        if (object == null) throw new IllegalArgumentException("object parameter is required!");
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
            return Optional.of(Base64.getEncoder().encodeToString(baos.toByteArray()));
        } catch (final IOException e) {
            return Optional.empty();
        }
    }

    /**
     * Transform Base64 encoded string into an instance.
     *
     * @param objectAsString Mandatory object string version to deserialize.
     * @return Deserialized instance.
     * @throws IllegalArgumentException When mandatory parameter is missing.
     */
    public static <T extends Serializable> Optional<T> convertFrom(final String objectAsString) throws IllegalArgumentException {
        if (objectAsString == null || objectAsString.isEmpty())
            throw new IllegalArgumentException("objectAsString parameter shall be defined!");
        final byte[] data = Base64.getDecoder().decode(objectAsString);
        try (final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return Optional.of((T) ois.readObject());
        } catch (final IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
