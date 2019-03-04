package nl.cwi.swat.smtlogic2;

public interface Term {
  long label();

  int size();

  Term input(int pos);
}
