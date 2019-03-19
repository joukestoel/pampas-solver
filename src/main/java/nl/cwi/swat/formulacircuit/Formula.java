package nl.cwi.swat.formulacircuit;

public interface Formula<T extends Term> extends Term<T> {
  Formula negation();
}
