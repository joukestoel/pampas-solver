package nl.cwi.swat.translation.data.relation;

import nl.cwi.swat.ast.Domain;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Heading extends Iterable<FieldDefinition> {
  int arity();
  boolean isUnionCompatible(Relation other);

  String getFieldNameAt(int index);
  Domain getDomainAt(int index);
  Set<String> fieldNamesOnly();

  List<Integer> getAttributeIndices(Set<String> attributeNames);

  Heading rename(Map<String,String> renamedFields);
  Heading project(Set<String> projectedFields);
  Heading join(Heading other);

  Set<String> intersect(Heading other);

  boolean idFieldsOnly();
}
