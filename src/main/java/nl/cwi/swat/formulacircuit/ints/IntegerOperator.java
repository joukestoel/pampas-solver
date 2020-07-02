package nl.cwi.swat.formulacircuit.ints;

import nl.cwi.swat.formulacircuit.Operator;

public abstract class IntegerOperator extends Operator {
  IntegerOperator(int ordinal) {
    super(ordinal);
  }

  public static final IntegerOperator EQUAL = new IntegerOperator(4) {
    @Override
    public String toString() {
      return "=";
    }
  };

  public static final IntegerOperator GT = new IntegerOperator(5) {
    @Override
    public String toString() {
      return ">";
    }
  };

  public static final IntegerOperator GTE = new IntegerOperator(6) {
    @Override
    public String toString() {
      return ">=";
    }
  };

  public static final IntegerOperator LT = new IntegerOperator(7) {
    @Override
    public String toString() {
      return "<";
    }
  };

  public static final IntegerOperator LTE = new IntegerOperator(8) {
    @Override
    public String toString() {
      return "<=";
    }
  };

  public static final IntegerOperator NEG = new IntegerOperator(10) {
    @Override
    public String toString() {
      return "-";
    }
  };

  public static final IntegerOperator ITE = new IntegerOperator(11) {
    @Override
    public String toString() {
      return "?";
    }
  };

  public static final IntegerOperator ADD = new IntegerOperator(12) {
    @Override
    public String toString() {
      return "+";
    }
  };

  public static final IntegerOperator SUB = new IntegerOperator(13) {
    @Override
    public String toString() {
      return "-";
    }
  };

  public static final IntegerOperator MUL = new IntegerOperator(14) {
    @Override
    public String toString() {
      return "*";
    }
  };

  public static final IntegerOperator DIV = new IntegerOperator(15) {
    @Override
    public String toString() {
      return "/";
    }
  };

  public static final IntegerOperator MOD = new IntegerOperator(16) {
    @Override
    public String toString() {
      return "%";
    }
  };

  public static final IntegerOperator INT_VAR = new IntegerOperator(17) {
    @Override
    public String toString() {
      return "i-var";
    }
  };

  public static final IntegerOperator INT_CONST = new IntegerOperator(18) {
    @Override
    public String toString() {
      return "i-const";
    }
  };
}
