/**
 * Lightweight IPv4 data model types used by symbolic encodings.
 *
 * <p>This package defines immutable network-addressing primitives consumed by BDD/SAT
 * encoders and label conversion logic:
 * <ul>
 *   <li>{@link org.batfish.datamodel.Ip}: IPv4 address value type and bit-level helpers</li>
 *   <li>{@link org.batfish.datamodel.Prefix}: IPv4 prefix representation and prefix operations</li>
 *   <li>{@link org.batfish.datamodel.IpWildcard}: wildcard/mask-based IPv4 set representation</li>
 * </ul>
 *
 * <p>Together, they provide canonical parsing and comparison semantics for packet-header
 * domain constraints.
 */
package org.batfish.datamodel;
