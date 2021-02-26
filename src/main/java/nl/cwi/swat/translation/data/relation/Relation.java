package nl.cwi.swat.translation.data.relation;

import io.usethesource.capsule.Map;
import nl.cwi.swat.formulacircuit.Formula;
import nl.cwi.swat.translation.data.row.Tuple;
import nl.cwi.swat.translation.data.row.Constraint;

import java.util.Set;

public interface Relation extends Iterable<Tuple> {

  int arity();
  Heading getHeading();

  int nrOfRows();
  boolean isEmpty();

  Formula getCombinedConstraints(Tuple tuple);
  Constraint getRowConstraint(Tuple tuple);

  boolean unionCompatible(Relation other);
  boolean isStable();

  Map.Immutable<Tuple, Constraint> rows();
  Relation rename(java.util.Map<String,String> renamings);
  Relation project(java.util.Set<String> projectedAttributes);

  Relation restrict();
  Relation transitiveClosure();

  Relation union(Relation other);
  Relation intersect(Relation other);
  Relation difference(Relation other);

  Formula subset(Relation other);
  Formula equal(Relation other);

  Relation naturalJoin(Relation other);
  Relation product(Relation other);

  Relation aggregate();

  Relation asSingleton(Tuple tuple);
}
