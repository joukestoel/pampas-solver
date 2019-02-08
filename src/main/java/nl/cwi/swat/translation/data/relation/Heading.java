package nl.cwi.swat.translation.data.relation;

import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.translation.data.row.Tuple;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The Heading of a {@link Relation}.
 * A heading describes which fields (names and associated domains) are part
 * of the relation.
 * More formally (definition taken from 'An Introduction To Database Systems' by C.T.Date; 6th ed. 1994):
 * a Heading is a fixed set of attribute-name domain pairs {<A1:D1>,<A2,D2>...<An,Dn>} such that each attribute
 * Aj corresponds to exactly one domain Dj. The attribute names are arbitrary labels and are all distinct.
 */
public interface Heading extends Iterable<Attribute> {
  int arity();
  boolean isUnionCompatible(Heading other);

  String getFieldNameAt(int index);
  Domain getDomainAt(int index);
  Set<String> attributeNamesOnly();
  Set<String> getNamesOfIdDomainAttributes();

  List<Integer> getAttributeIndices(Set<String> attributeNames);

  Heading rename(Map<String,String> renamedAttributes);
  Heading project(Set<String> projectedAttributes);
  Heading join(Heading other);

  Set<String> intersect(Heading other);

  boolean containsOnlyIdAttributes();
  boolean isRowCompatible(Tuple tuple);
}
