package nl.cwi.swat.smtlogic;

import java.util.Iterator;
import java.util.Objects;

public abstract class Operator implements Comparable<Operator>{
    final int ordinal;

    private Operator(int ordinal) {
      this.ordinal = ordinal;
    }

    /**
     * Returns the ordinal of this operator constant.
     * @return the ordinal of this operator constant.
     */
    public final int ordinal() {
      return ordinal;
    }

    /**
     * Returns an integer i such that i < 0 if this.ordinal < op.ordinal,
     * i = 0 when this.ordinal = op.ordinal, and i > 0 when this.ordinal > op.ordinal.
     * @return i: int | this.ordinal < op.ordinal => i < 0,
     *         this.ordinal = op.ordinal => i = 0, i > 0
     * @throws NullPointerException  op = null
     */
    public int compareTo(Operator op) {
      return ordinal - op.ordinal;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Operator operator = (Operator) o;
      return ordinal == operator.ordinal;
    }

    @Override
    public int hashCode() {
      return Objects.hash(ordinal);
    }

  /**
     * N-ary {@link MultiGate AND} operator.
     */
    public static final Nary AND = new Nary(0) {
      public String toString() { return "&"; }
      /** @return true */
      public BooleanConstant identity() { return BooleanConstant.TRUE; }
      /** @return false */
      public BooleanConstant shortCircuit() { return BooleanConstant.FALSE; }
      /** @return OR */
      public Nary complement() {  return OR; }
    };

    /**
     * N-ary {@link MultiGate OR} operator.
     */
    public static final Nary OR = new Nary(1) {
      public String toString() { return "|"; }
      /** @return false */
      public BooleanConstant identity() { return BooleanConstant.FALSE; }
      /** @return true */
      public BooleanConstant shortCircuit() { return BooleanConstant.TRUE; }
      /** @return AND */
      public Nary complement() {  return AND; }
    };

    /**
     * Ternary {@link ITEGate if-then-else} operator.
     */
    public static final Ternary ITE = new Ternary(2) {
      public String toString() { return "?"; }
    };

    /**
     * Unary {@link NotGate negation} operator.
     */
    public static final Operator NOT = new Operator(3) {
      public String toString() { return "!"; }
    };

    /**
     * Zero-arity {@link BooleanVariable variable} operator.
     */
    public static final Operator VAR = new Operator(4) {
      public String toString() { return "var"; }
    };

    /**
     * Zero-arity {@link BooleanConstant constant} operator.
     */
    public static final Operator CONST = new Operator(5) {
      public String toString() { return "const"; }
    };

    /**
     * An n-ary operator, where n>=2
     */
    public static abstract class Nary extends Operator {

      private Nary(int ordinal) {
        super(ordinal);
      }

      /**
       * Returns the boolean constant <i>c</i> such that
       * for all logical values <i>x</i>, <i>c</i> composed
       * with <i>x</i> using this operator will result in <i>x</i>.
       * @return the identity value of this binary operator
       */
      public abstract BooleanConstant identity();
      /**
       * Returns the boolean constant <i>c</i> such that
       * for all logical values <i>x</i>, <i>c</i> composed
       * with <i>x</i> using this operator will result in <i>c</i>.
       * @return the short circuiting value of this binary operator
       */
      public abstract BooleanConstant shortCircuit();
      /**
       * Returns the binary operator whose identity and short circuit
       * values are the negation of this operator's identity and
       * short circuit.
       * @return the complement of this binary operator
       */
      public abstract Operator.Nary complement();


    }

    static abstract class Ternary extends Operator {

      private Ternary(int ordinal) {
        super(ordinal);
      }

    }

  }
