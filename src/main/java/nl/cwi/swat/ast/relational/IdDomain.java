package nl.cwi.swat.ast.relational;

import nl.cwi.swat.ast.Domain;

public class IdDomain extends Domain {
  public static final IdDomain ID = new IdDomain();

  @Override
  public String toString() {
    return "ID";
  }
}
