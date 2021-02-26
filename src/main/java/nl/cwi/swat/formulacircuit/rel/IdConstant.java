package nl.cwi.swat.formulacircuit.rel;

import nl.cwi.swat.formulacircuit.*;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Iterator;

public class IdConstant implements Expression, Constant {
  private final long label;
  private final String atom;

  public IdConstant(long label, @NonNull String atom) {
    this.label = label;
    this.atom = atom;
  }

  @Override
  public long label() {
    return label;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public Term input(int pos) {
    throw new IndexOutOfBoundsException("Constant is zero arity");
  }

  @Override
  public Operator operator() {
    return null;
  }

  @Override
  public Term negation() {
    throw new UnsupportedOperationException();
  }

  @NonNull
  @Override
  public Iterator<Term> iterator() {
    return Collections.emptyIterator();
  }

  @Override
  public String toString() {
    return atom;
  }

  @Override
  public <T> T accept(SolverVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    IdConstant that = (IdConstant) o;

    if (label != that.label) return false;
    return atom.equals(that.atom);
  }

  @Override
  public int hashCode() {
    int result = (int) (label ^ (label >>> 32));
    result = 31 * result + atom.hashCode();
    return result;
  }
}
