package nl.cwi.swat.formulacircuit;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface Assembler {
  Term assemble(@NonNull Operator op, @NonNull Term t1, @NonNull Term t2);
}
