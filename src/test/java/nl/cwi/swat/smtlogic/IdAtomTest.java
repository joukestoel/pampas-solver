package nl.cwi.swat.smtlogic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdAtomTest {

  @Test
  void equalAtoms() {
    IdAtom a = new IdAtom("a");
    IdAtom b = new IdAtom("a");

    assertEquals(a,b, "Id atoms with the same label should be equal");
  }

  @Test
  void unequalAtoms() {
    IdAtom a = new IdAtom("a");
    IdAtom b = new IdAtom("b");

    assertNotEquals(a,b,"Id atoms with different labels should not be equal");
  }
}