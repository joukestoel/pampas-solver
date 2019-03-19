/*
 * Kodkod -- Copyright (c) 2005-present, Emina Torlak
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package nl.cwi.swat.formulacircuit.bool;

import nl.cwi.swat.formulacircuit.Operator;

public abstract class BooleanOperator extends Operator {
  BooleanOperator(final int ordinal) {
    super(ordinal);
  }

  public static final Nary AND = new Nary(0) {
    public String toString() { return "&"; }
    /** @return true */
    public BooleanConstant identity() { return BooleanConstant.TRUE; }
    /** @return false */
    public BooleanConstant shortCircuit() { return BooleanConstant.FALSE; }
    /** @return OR */
    public Nary complement() {  return OR; }
  };

  public static final Nary OR = new Nary(1) {
    public String toString() { return "|"; }
    /** @return false */
    public BooleanConstant identity() { return BooleanConstant.FALSE; }
    /** @return true */
    public BooleanConstant shortCircuit() { return BooleanConstant.TRUE; }
    /** @return AND */
    public Nary complement() {  return AND; }
  };

  public static final BooleanOperator NOT = new BooleanOperator(2) {
    public String toString() { return "!"; }
  };

  public static final BooleanOperator BOOLEAN_VAR = new BooleanOperator(3) {
    public String toString() { return "b-var"; }
  };

  public static final BooleanOperator BOOLEAN_CONST = new BooleanOperator(Integer.MAX_VALUE) {
    public String toString() { return "b-const"; }
  };

  public static abstract class Nary extends BooleanOperator {

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
    public abstract BooleanOperator.Nary complement();
  }
}
