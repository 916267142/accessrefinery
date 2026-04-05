package org.iam.model;

import org.iam.core.MCPFactory;

import java.util.HashMap;
import java.util.HashSet;

/**
 * DomainLabelTrees manages label trees for multiple domains.
 * <p>
 * Maintains a mapping from domain names to their corresponding LabelTree instances,
 * and provides methods to query and set label trees for each domain.
 * </p>
 *
 * @author
 * @since 2025-02-28
 */
public class DomainLabelTrees {

    /**
     * Maps domain names to their LabelTree instances.
     */
    HashMap<Object, LabelTree<Object>> domainToTrees;

    /**
     * Default constructor. Initializes the domain-to-tree map.
     */
    public DomainLabelTrees() {
        domainToTrees = new HashMap<>();
    }

    /**
     * Constructs DomainLabelTrees using an MCPFactory.
     * For each domain in the factory, creates a LabelTree based on its children nodes.
     *
     * @param mcpFactory the MCPFactory instance
     */
    public DomainLabelTrees(MCPFactory mcpFactory) {
        domainToTrees = new HashMap<>();
        mcpFactory
                .getDomainNames()
                .forEach(
                        (domainName, type) ->
                                this.setDomainLabelTrees(domainName, new LabelTree<>(mcpFactory.getDomainChildrenNodes(domainName)))
                );
    }

    /**
     * Returns the maximal labels (children nodes) for a given domain and value.
     *
     * @param domain the domain name
     * @param value  the value to query
     * @return set of maximal child nodes for the value in the domain
     */
    public HashSet<Object> getMaximumLabels(String domain, Object value) {
        return domainToTrees.get(domain).getMaximumChildrens(value);
    }

    /**
     * Sets the LabelTree for a specific domain.
     *
     * @param domain    the domain name
     * @param labelTree the LabelTree instance to associate
     */
    public void setDomainLabelTrees(String domain, LabelTree<Object> labelTree) {
        domainToTrees.put(domain, labelTree);
    }
}