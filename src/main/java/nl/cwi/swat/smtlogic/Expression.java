package nl.cwi.swat.smtlogic;

public interface Expression {
  Operator operator();

  int hash(Operator op);

  long label();
}
