package nl.cwi.swat.formulacircuit;

import io.usethesource.capsule.Set;

public interface Term<T extends Term> extends Iterable<T> {
  long label();

  int size();

  T input(int pos);

  Operator operator();

  default boolean contains(Operator op, long f, int k) {
    return f == label();
  }

  default void flatten(Operator op, Set.Transient<Term> flat, int k) {
    flat.__insert(this);
  }
}
