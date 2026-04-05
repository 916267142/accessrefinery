package org.iam.policy.grammer;

import java.util.List;
import java.util.Objects;

public class Principal {
    protected String domainName;
    protected List<String> values;

    public Principal() {}

    public Principal(String domainName, List<String> values) {
        this.domainName = "Principal." + domainName;
        this.values = values;
    }

    public Principal(Principal other) {
        this.domainName = other.domainName;
        this.values = other.values;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Principal principal = (Principal) o;
        return Objects.equals(domainName, principal.domainName) && Objects.equals(values, principal.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domainName, values);
    }
}