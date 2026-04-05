package org.iam.grammar;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Finding {

    protected Set<Principal> principal = null;

    protected Set<String> action = null;

    protected Set<String> resource = null;

    /**
     * Necessary rules to follow for future development:
     * Every condition only have one key-value pair.
     */
    protected Set<Condition> condition = null;

    public Finding() {
    }

    public Finding(Finding other) {
        this.principal = other.principal;
        this.action = other.action;
        this.resource = other.resource;
        this.condition = other.condition == null ? null : other.condition.stream()
                .map(Condition::new)
                .collect(Collectors.toSet());
    }

    public Finding(Set<Principal> principal, Set<String> action, Set<String> resource, Set<Condition> condition) {
        this.principal = principal;
        this.action = action;
        this.resource = resource;
        this.condition = condition;
    }

    public Set<Principal> getPrincipal() {
        return principal;
    }

    public void setPrincipal(Set<Principal> principal) {
        this.principal = principal;
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

    @Override
    public String toString() {
        return "Findings{" +
                "principal=" + principal.toString() +
                ", action=" + action +
                ", resource=" + resource +
                ", condition=" + condition +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Finding finding = (Finding) o;
        return Objects.equals(principal, finding.principal) &&
                Objects.equals(action, finding.action) &&
                Objects.equals(resource, finding.resource) &&
                Objects.equals(condition, finding.condition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(principal, action, resource, condition);
    }
}
