package org.cybnity.feature.defense_template.domain.model;

import org.cybnity.feature.defense_template.DomainObjectType;
import org.cybnity.feature.defense_template.IReferential;
import org.cybnity.feature.defense_template.ITemplate;
import org.cybnity.framework.IContext;
import org.cybnity.framework.domain.Command;
import org.cybnity.framework.domain.MutableAttribute;
import org.cybnity.framework.domain.model.Aggregate;
import org.cybnity.framework.immutable.Entity;
import org.cybnity.framework.immutable.HistoryState;
import org.cybnity.framework.immutable.Identifier;
import org.cybnity.framework.immutable.ImmutabilityException;
import org.cybnity.framework.immutable.utility.VersionConcreteStrategy;
import org.cybnity.framework.support.annotation.Requirement;
import org.cybnity.framework.support.annotation.RequirementCategory;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Common definition class regarding a specification object which define a
 * template (e.g process aggregate object) of a domain object reference. A
 * template role is to define a standardized structure of a domain object,
 * and/or to add standardized behavior (e.g domain object changes notification
 * with specific events) to a domain object.
 *
 * @author olivier
 */
@Requirement(reqType = RequirementCategory.Functional, reqId = "REQ_FCT_73")
public class Template extends Aggregate implements ITemplate {

    private static final long serialVersionUID = new VersionConcreteStrategy()
            .composeCanonicalVersionHash(Template.class).hashCode();

    /**
     * Optional origin of this template.
     */
    private IReferential originReferential;

    /**
     * Mandatory name of this template.
     */
    private MutableAttribute name;

    /**
     * Represent a mandatory type of domain object that is modeled as a template.
     */
    private DomainObjectType modelOf;

    /**
     * Default constructor.
     *
     * @param predecessor       Mandatory parent of this root aggregate entity.
     * @param id                Unique and mandatory identifier of this template.
     * @param originReferential Optional referential which is origin of this
     *                          template.
     * @param name              Mandatory label naming this template.
     * @param modelOf           Mandatory type of domain object which is specified
     *                          as a template.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     *                                  When a problem of immutability is occurred.
     *                                  When predecessor mandatory parameter is not
     *                                  defined or without defined identifier.
     */
    public Template(Entity predecessor, Identifier id, IReferential originReferential, String name,
                    DomainObjectType modelOf) throws IllegalArgumentException {
        super(predecessor, id);
        if (modelOf == null)
            throw new IllegalArgumentException("templateType parameter is required!");
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("Name  parameter is required!");
        if (id == null)
            throw new IllegalArgumentException("Id parameter is required!");

        // Create mutable name attached to predecessor
        // that is this template
        this.name = new MutableAttribute(this.rootEntity(), new org.cybnity.framework.domain.Attribute("name", name), HistoryState.COMMITTED);

        this.modelOf = modelOf;
        this.originReferential = originReferential;
    }

    /**
     * Default constructor.
     *
     * @param predecessor       Mandatory parent of this root aggregate entity.
     * @param identifiers       Mandatory set of identifiers of this template, that
     *                          contains non-duplicable elements.
     * @param originReferential Optional referential which is origin of this
     *                          template.
     * @param name              Mandatory label naming this template.
     * @param modelOf           Mandatory type of domain object which is specified
     *                          as a template.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     *                                  When a problem of immutability is occurred.
     *                                  When predecessor mandatory parameter is not
     *                                  defined or without defined identifier.
     */
    public Template(Entity predecessor, LinkedHashSet<Identifier> identifiers, IReferential originReferential,
                    String name, DomainObjectType modelOf) throws IllegalArgumentException {
        super(predecessor, identifiers);
        if (modelOf == null)
            throw new IllegalArgumentException("templateType parameter is required!");
        if (name == null || "".equals(name))
            throw new IllegalArgumentException("Name  parameter is required!");
        if (identifiers == null || identifiers.isEmpty())
            throw new IllegalArgumentException("Identifiers parameter is required!");
        // Create mutable name attached to predecessor
        // that is this template
        this.name = new MutableAttribute(this.rootEntity(), new org.cybnity.framework.domain.Attribute("name", name), HistoryState.COMMITTED);
        this.modelOf = modelOf;
        this.originReferential = originReferential;
    }

    /**
     * Default constructor.
     *
     * @param predecessor       Mandatory parent of this root aggregate entity.
     * @param identifiers       Mandatory set of identifiers of this template, that
     *                          contains non-duplicable elements.
     * @param originReferential Optional referential which is origin of this
     *                          template.
     * @param name              Mandatory label naming this template.
     * @param modelOf           Mandatory type of domain object which is specified
     *                          as a template.
     * @throws IllegalArgumentException When any mandatory parameter is missing.
     *                                  When a problem of immutability is occurred.
     *                                  When predecessor mandatory parameter is not
     *                                  defined or without defined identifier.
     */
    private Template(Entity predecessor, LinkedHashSet<Identifier> identifiers, IReferential originReferential,
                     MutableAttribute name, DomainObjectType modelOf) throws IllegalArgumentException {
        super(predecessor, identifiers);
        if (modelOf == null)
            throw new IllegalArgumentException("templateType parameter is required!");
        if (name == null)
            throw new IllegalArgumentException("Name  parameter is required!");
        if (identifiers == null || identifiers.isEmpty())
            throw new IllegalArgumentException("Identifiers parameter is required!");
        this.name = name;
        this.modelOf = modelOf;
        this.originReferential = originReferential;
    }

    @Override
    public Serializable immutable() throws ImmutabilityException {
        Template copy = new Template(this.parent(), new LinkedHashSet<>(this.identifiers()), this.originReferential(),
                this.named(), this.type());
        // Complete with additional attributes of this complex aggregate
        copy.createdAt = this.occurredAt();
        return copy;
    }

    /**
     * Redefined equality evaluation including the identity and name.
     */
    @Override
    public boolean equals(Object obj) {
        boolean isEquals = false;
        // Comparison based on process identity
        if (super.equals(obj) && obj instanceof Template) {
            try {
                Template compared = (Template) obj;
                // Comparison based on version functional attributes
                // Check if same names
                String objNameAttribute = compared.name();
                String thisNameAttribute = this.name();
                if (objNameAttribute != null && thisNameAttribute != null) {
                    isEquals = objNameAttribute.equals(thisNameAttribute);
                }
            } catch (Exception e) {
                // any missing information generating null pointer exception or problem of
                // information read
            }
        }
        return isEquals;
    }

    /**
     * Do nothing about this object.
     */
    @Override
    public void handle(Command command, IContext ctx) throws IllegalArgumentException {
    }

    /**
     * Do nothing.
     */
    @Override
    public Set<String> handledCommandTypeVersions() {
        return null;
    }

    /**
     * Get the origin of this template.
     *
     * @return A referential or null.
     * @throws ImmutabilityException When impossible creation of immutable version.
     */
    public IReferential originReferential() throws ImmutabilityException {
        if (this.originReferential != null)
            return (IReferential) this.originReferential.immutable();
        return this.originReferential;
    }

    /**
     * Get the current name of this template.
     *
     * @return An immutable version of this template name.
     * @throws ImmutabilityException When impossible instantiation of the immutable
     *                               version to return.
     */
    public MutableAttribute named() throws ImmutabilityException {
        return (MutableAttribute) this.name.immutable();
    }

    @Override
    public String name() {
        return this.name.value().value();
    }

    /**
     * Change the current name of this template.
     *
     * @param name Mandatory new version of name version for replace the current.
     * @throws IllegalArgumentException When mandatory parameter is not defined or
     *                                  is not valid in terms of minimum conformity.
     *                                  When name parameter is not regarding same
     *                                  template identity.
     * @throws ImmutabilityException
     */
    public void changeName(MutableAttribute name) throws IllegalArgumentException, ImmutabilityException {
        if (name == null)
            throw new IllegalArgumentException("Name parameter is required!");
        // Check conformity
        checkNameConformity(name, rootEntity());
        // Update this staging
        this.name = name;
    }

    /**
     * Verify if the name attribute have owner equals.
     *
     * @param name          Optional name instance to verify.
     * @param templateOwner Mandatory owner of the attribute to compare as a name
     *                      condition.
     * @throws IllegalArgumentException When name instance is not valid.
     * @throws ImmutabilityException    When impossible read of template owner.
     */
    void checkNameConformity(MutableAttribute name, Entity templateOwner)
            throws IllegalArgumentException, ImmutabilityException {
        if (name != null) {
            if (templateOwner == null)
                throw new IllegalArgumentException("Template owner parameter is required!");
            if (templateOwner != null) {
                // Check that owner of the new attribute is equals to this template identity
                if (name.owner() == null || !name.owner().equals(templateOwner))
                    throw new IllegalArgumentException("The owner of the new name shall be equals to this template!");
            }
        }
    }

    @Override
    public DomainObjectType type() throws ImmutabilityException {
        return (DomainObjectType) this.modelOf.immutable();
    }

}
