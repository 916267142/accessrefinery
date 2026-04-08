package org.aws.grammar;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.aws.grammar.deserializer.ConditionDeserializer;
import org.aws.grammar.deserializer.PrincipalDeserializer;
import org.aws.grammar.deserializer.StringDeserializer;

import java.util.Objects;
import java.util.Set;

public class Statement {
    @JsonIgnore
    protected String sid;

    @JsonProperty("Effect")
    protected VarEffect effect = null;

    @JsonProperty("Principal")
    @JsonDeserialize(using = PrincipalDeserializer.class)
    protected Set<Principal> principal = null;

    @JsonProperty("Action")
    @JsonDeserialize(using = StringDeserializer.class)
    protected Set<String> action = null;

    @JsonProperty("Resource")
    @JsonDeserialize(using = StringDeserializer.class)
    protected Set<String> resource = null;

    @JsonProperty("Condition")
    @JsonDeserialize(using = ConditionDeserializer.class)
    protected Set<Condition> condition = null;

    public Statement() {}

    public Statement(Statement other) {
        this.effect = other.effect;
        this.principal = other.principal;
        this.action = other.action;
        this.resource = other.resource;
        this.condition = other.condition;
    }

    public Statement(VarEffect effect, Set<Principal> principal, Set<String> action, Set<String> resource, Set<Condition> condition) {
        this.effect = effect;
        this.principal = principal;
        this.action = action;
        this.resource = resource;
        this.condition = condition;
    }

    public Statement(Finding finding) {
        this.effect = VarEffect.Allow;
        this.principal = finding.getPrincipal();
        this.action = finding.getAction();
        this.resource = finding.getResource();
        this.condition = finding.getCondition();
    }

    public VarEffect getEffect() {
        return effect;
    }

    public void setEffect(VarEffect effect) {
        this.effect = effect;
    }

    public Set<String> getAction() {
        return action;
    }

    public void setAction(Set<String> action) {
        this.action = action;
    }

    public Set<String> getResource() {
        return resource;
    }

    public void setResource(Set<String> resource) {
        this.resource = resource;
    }

    public Set<Condition> getCondition() {
        return condition;
    }

    public void setCondition(Set<Condition> condition) {
        this.condition = condition;
    }

    public Set<Principal> getPrincipal() {
        return principal;
    }

    public void setPrincipal(Set<Principal> principal) {
        this.principal = principal;
    }

    public enum VarEffect implements StringComparableEnum {
        Allow("ALLOW"), Deny("DENY");

        private final String value;

        @JsonCreator
        VarEffect(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }

        public static boolean isValid(String value) {
            return StringComparableEnum.isValid(value, VarEffect.class);
        }

        public static VarEffect fromString(String value) {
            return StringComparableEnum.fromString(value, VarEffect.class);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Statement statement = (Statement) o;
        return effect == statement.effect && Objects.equals(principal, statement.principal) && Objects.equals(action, statement.action) && Objects.equals(resource, statement.resource) && Objects.equals(condition, statement.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(effect, principal, action, resource, condition);
    }
}