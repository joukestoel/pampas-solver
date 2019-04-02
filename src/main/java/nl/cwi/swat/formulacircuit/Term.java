package nl.cwi.swat.formulacircuit;

import io.usethesource.capsule.Set;

public interface Term extends Iterable<Term> {
  long label();

  int size();

  <T extends Term> T input(int pos);

  Operator operator();

  <T extends Term> T negation();

  <T> T accept(SolverVisitor<T> visitor);

  default boolean contains(Operator op, long f, int k) {
    return f == label();
  }

  default void flatten(Operator op, Set.Transient<Term> flat, int k) {
    flat.__insert(this);
  }
}
