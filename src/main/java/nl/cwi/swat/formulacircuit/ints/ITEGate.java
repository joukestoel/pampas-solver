package nl.cwi.swat.formulacircuit.ints;

import io.usethesource.capsule.Set;
import nl.cwi.swat.formulacircuit.Expression;
import nl.cwi.swat.formulacircuit.Gate;
import nl.cwi.swat.formulacircuit.Operator;
import nl.cwi.swat.formulacircuit.Term;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

public class ITEGate extends Gate implements Expression {
  private final Term ifCon;
  private final Term thenCon;
  private final Term elseCon;

  public ITEGate(long label, Term ifCon, Term thenCon, Term elseCon) {
    super(IntegerOperator.ITE, label);
    this.ifCon = ifCon;
    this.thenCon = thenCon;
    this.elseCon = elseCon;
  }

  @Override
  public int size() {
    return 3;
  }

  @Override
  public Term input(int pos) {
    switch (pos) {
      case 0: return ifCon;
      case 1: return thenCon;
      case 2: return elseCon;
      default: throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public Term negation() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean contains(Operator op, long f, int k) {
    // k > 0
    if (f == label() || ifCon.label() == f || thenCon.label() == f || elseCon.label() == f) {
      return true;
    }

    return false;
  }

  @Override
  public void flatten(Operator op, Set.Transient<Term> flat, int k) {
    if (op==IntegerOperator.ITE && k > 2) {
      flat.add(ifCon);
      flat.add(thenCon);
      flat.add(elseCon);
    } else {
      flat.add(this);
    }
  }

  @NotNull
  @Override
  public Iterator<Term> iterator() {
    throw new UnsupportedOperationException("Can't iterate over an ITE gate");
  }
}
