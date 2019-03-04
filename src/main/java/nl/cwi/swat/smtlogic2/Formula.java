package nl.cwi.swat.smtlogic2;

import io.usethesource.capsule.Set;

public interface Formula extends Term {
  Formula negation();

  default boolean contains(BooleanOperator op, long f, int k) {
    return f == label();
  }

  default void flatten(BooleanOperator op, Set.Transient<Term> flat, int k) {
    flat.__insert(this);
  }

}
