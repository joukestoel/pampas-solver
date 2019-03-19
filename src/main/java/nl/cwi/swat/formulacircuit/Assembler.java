package nl.cwi.swat.formulacircuit;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface Assembler<T extends Term> {
  Term<T> assemble(@NonNull Operator op, @NonNull Term t1, @NonNull Term t2);
}
