package nl.cwi.swat.translation.data.relation;

import nl.cwi.swat.smtlogic.Formula;
import nl.cwi.swat.translation.data.row.Tuple;
import nl.cwi.swat.translation.data.row.Constraint;

public interface Relation extends Iterable<Tuple> {

  int arity();
  Heading getHeading();

  int nrOfRows();
  boolean isEmpty();

  Formula getCombinedConstraints(Tuple tuple);
  Constraint getRowConstraint(Tuple tuple);

  boolean unionCompatible(Relation other);
  boolean isStable();

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
