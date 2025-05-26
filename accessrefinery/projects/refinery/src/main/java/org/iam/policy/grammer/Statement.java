package org.iam.policy.grammer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;
import java.util.Objects;

public class Statement {
    @JsonProperty("Sid")
    protected String sid = null;

    @JsonProperty("Effect")
    protected VarEffect effect = null;

    @JsonProperty("Principal")
    @JsonDeserialize(using = PrincipalDeserializer.class)
    protected List<Principal> principal = null;

    @JsonProperty("Action")
    @JsonDeserialize(using = StringDeserializer.class)
    protected List<String> action = null;

    @JsonProperty("Resource")
    @JsonDeserialize(using = StringDeserializer.class)
    protected List<String> resource = null;

    @JsonProperty("Condition")
    @JsonDeserialize(using = ConditionDeserializer.class)
    protected List<Condition> condition = null;

    public Statement() {}

    public Statement(Statement other) {
        this.effect = other.effect;
        this.principal = other.principal;
        this.action = other.action;
        this.resource = other.resource;
        this.condition = other.condition;
    }

    public Statement(VarEffect effect, List<Principal> principal, List<String> action, List<String> resource, List<Condition> condition) {
        this.effect = effect;
        this.principal = principal;
        this.action = action;
        this.resource = resource;
        this.condition = condition;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public VarEffect getEffect() {
        return effect;
    }

    public void setEffect(VarEffect effect) {
        this.effect = effect;
    }

    public List<String> getAction() {
        return action;
    }

    public void setAction(List<String> action) {
        this.action = action;
    }

    public List<String> getResource() {
        return resource;
    }

    public void setResource(List<String> resource) {
        this.resource = resource;
    }

    public List<Condition> getCondition() {
        return condition;
    }

    public void setCondition(List<Condition> condition) {
        this.condition = condition;
    }

    public List<Principal> getPrincipal() {
        return principal;
    }

    public void setPrincipal(List<Principal> principal) {
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