package org.iam.policy.grammer;

import com.fasterxml.jackson.annotation.*;
import org.iam.utils.PolicyParser;

import java.util.List;
import java.util.Objects;

public class Policy {
    @JsonProperty("Version")
    protected String version;

    @JsonProperty("Id")
    protected String id;

    @JsonProperty("Statement")
    protected List<Statement> statement;

    public Policy() {}

    public Policy(Policy other) {
        this.version = other.version;
        this.statement = other.statement;
        this.id = other.id;
    }

    public Policy(String version, List<Statement> statement) {
        this.version = version;
        this.statement = statement;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Statement> getStatement() {
        return statement;
    }

    public void setStatement(List<Statement> statement) {
        this.statement = statement;
    }

    @Override
    public String toString() {
        return PolicyParser.toString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Policy policy = (Policy) o;
        return Objects.equals(version, policy.version) && Objects.equals(statement, policy.statement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, statement);
    }
}

