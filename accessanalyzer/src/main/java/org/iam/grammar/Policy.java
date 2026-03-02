package org.iam.grammar;

import com.fasterxml.jackson.annotation.*;
import org.iam.utils.PolicyParser;

import java.util.Objects;
import java.util.Set;

public class Policy {

    @JsonIgnore
    protected String id;

    @JsonProperty("Version")
    protected String version;

    @JsonProperty("Statement")
    protected Set<Statement> statement;

    public Policy() {}

    public Policy(Policy other) {
        this.version = other.version;
        this.statement = other.statement;
    }

    public Policy(String version, Set<Statement> statement) {
        this.version = version;
        this.statement = statement;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<Statement> getStatement() {
        return statement;
    }

    public void setStatement(Set<Statement> statement) {
        this.statement = statement;
    }

    @Override
    public String toString() {
        return PolicyParser.toString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Policy policy = (Policy) o;
        return Objects.equals(version, policy.version) && Objects.equals(statement, policy.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, statement);
    }
}

