package org.cybnity.framework.immutable.utility;

import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Implementation class regarding the algorithm generating a structural version
 * of a class type based on the read of type name, the sort of the class's
 * fields and predecessors alphabetically by name and including a description of
 * their type.
 *
 * @author olivier
 */
public class VersionConcreteStrategy extends StructuralVersionStrategy {

    public VersionConcreteStrategy() {
        super();
    }

    /**
     * Implementation returning a base64 result.
     */
    @Requirement(reqType = RequirementCategory.Robusteness, reqId = "REQ_CONS_8")
    @Override
    public String composeCanonicalVersionHash(Class<?> factType) throws IllegalArgumentException {
        if (factType == null)
            throw new IllegalArgumentException("Mandatory factType parameter is required!");
        try {
            StringBuilder versionHashValue = new StringBuilder();
            // Get the type name of the class
            versionHashValue.append(factType.getSimpleName());
            // Add { character as start of JSON form of fields descriptions
            versionHashValue.append("{");

            // Sort the fields alphabetically by their names
            List<Field> fieldsList = getAllFields(factType);
            Comparator<Field> nameBasedComparator = Comparator.comparing(Field::getName);
            Collections.sort(fieldsList, nameBasedComparator);// Ordering based on field name

            // Build a description of each field
            boolean addSep = false;
            for (Field aField : fieldsList) {
                if (addSep)
                    versionHashValue.append(";"); // Add field separator

                versionHashValue.append(aField.getName()); // property name added
                versionHashValue.append(":"); // separator between name and type
                versionHashValue.append(aField.getType().getSimpleName()); // target type name
                addSep = true;
            }

            // Add } character as end of JSON form of fields descriptions
            versionHashValue.append("}");

            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // Encrypt the binary plaintext from character encoded String (UTF8)
            byte[] messageDigest = md.digest(versionHashValue.toString().getBytes(StandardCharsets.UTF_8));
            // Encode the encrypted binary plaintext to "text" value using base 64 encoding
            return Base64.getEncoder().encodeToString(messageDigest);
        } catch (NoSuchAlgorithmException e) {
            // Log problem of algorithm missing in the implementation component
            return null;
        }
    }

    private List<Field> getAllFields(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        result.addAll(Arrays.asList(clazz.getDeclaredFields()));// including any visibility but without inherited fields
        return result;
    }

}
