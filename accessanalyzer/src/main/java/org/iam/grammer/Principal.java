package org.iam.grammer;


import java.util.Objects;
import java.util.Set;

public class Principal {
    protected String domainName;
    protected Set<String> values;

    public Principal() {}

    public Principal(String domainName, Set<String> values) {
        this.domainName = domainName;
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

    public Set<String> getValues() {
        return values;
    }

    public void setValues(Set<String> values) {
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

    @Override
    public String toString() {
        return "Principal{" +
                "domainName='" + domainName + '\'' +
                ", values=" + values +
                '}';
    }
}