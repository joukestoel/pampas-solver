package nl.cwi.swat.formulacircuit;

import io.usethesource.capsule.Set;

public interface Term extends Iterable<Term> {
  long label();

  int size();

  <T extends Term> T input(int pos);

  Operator operator();

  <T extends Term> T negation();

  default boolean contains(Operator op, long f, int k) {
    return f == label();
  }

  default void flatten(Operator op, Set.Transient<Term> flat, int k) {
    flat.__insert(this);
  }
}
