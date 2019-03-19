package nl.cwi.swat.formulacircuit.rel;

import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.Operator;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;
import java.util.Iterator;

public class IdConstant implements Expression {
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
  public Expression input(int pos) {
    throw new IndexOutOfBoundsException("Constant is zero arity");
  }

  @Override
  public Operator operator() {
    return null;
  }

  @NonNull
  @Override
  public Iterator<Expression> iterator() {
    return Collections.emptyIterator();
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
