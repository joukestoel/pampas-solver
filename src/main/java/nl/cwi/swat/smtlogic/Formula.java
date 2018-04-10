package nl.cwi.swat.smtlogic;

import io.usethesource.capsule.Set;

public interface Formula extends Iterable<Formula> {
  Operator operator();
  Formula negation();

  long label();

  int hash(Operator operator);

  int size();

  Formula input(int pos);

  default boolean contains(Operator op, long f, int k) {
    return f == label();
  }

  default void flatten(Operator op, Set.Transient<Formula> flat, int k) {
    flat.__insert(this);
  }
}
