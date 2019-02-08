package nl.cwi.swat.translation.data.relation;

import nl.cwi.swat.ast.Domain;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class HeadingTest {
  @Test
  void headingMustHaveDistinctFieldNames() {
    assertThrows(IllegalArgumentException.class, () -> new Heading(
            List.of(new Attribute("same", Domain.ID),
                    new Attribute("same", Domain.ID))));
  }

  @Test
  void headingWithOnlyIdFieldsMustReturnThatItOnlyHasIds() {
    Heading heading = new Heading(idAtts(2));
    assertTrue(heading.containsOnlyIdAttributes());
  }

  @Test
  void headingWithMixedDomainsMustReturnThatItsNotIdOnly() {
    Heading heading = new Heading(append(idAtts(1), intAtts(1)));
    assertFalse(heading.containsOnlyIdAttributes());
  }

  @Test
  void headingWithOneFieldHasArityOne() {
    Heading heading = new Heading(idAtts(1));
    assertEquals(1, heading.arity());
  }

  @Test
  void headingWithTenFieldsHasArityTen() {
    Heading heading = new Heading(append(idAtts(8), intAtts(2)));
    assertEquals(10, heading.arity());
  }

  @Test
  void twoHeadingsWithSameAttributesInOrderAreUnionCompatible() {
    Heading left = new Heading(idAtts(5));
    Heading right = new Heading(idAtts(5));

    assertTrue(left.isUnionCompatible(right));
  }

  @Test
  void twoHeadingsWithSameAttributesOutOfOrderAreUnionCompatible() {
    Heading left = new Heading(List.of(createAtt("a", Domain.ID), createAtt("b", Domain.ID)));
    Heading right = new Heading(List.of(createAtt("b", Domain.ID), createAtt("a", Domain.ID)));

    assertTrue(left.isUnionCompatible(right));
  }

  @Test
  void twoHeadingOfSameArityButDifferentDomainsAreNotUnionCompatible() {
    Heading left = new Heading(idAtts(2));
    Heading right = new Heading(append(idAtts(1), intAtts(1)));

    assertFalse(left.isUnionCompatible(right));
  }

  @Test
  void twoHeadingsWithDifferentArityOfSameDomainAreNotUnionCompatible() {
    Heading left = new Heading(idAtts(1));
    Heading right = new Heading(idAtts(2));

    assertFalse(left.isUnionCompatible(right));
  }

  @Test
  void gettingAnAttributeNameOutsideBoundsThrowsException() {
    Heading heading = new Heading(idAtts(1));
    assertThrows(IllegalArgumentException.class, () -> heading.getAttributeNameAt(1));
    assertThrows(IllegalArgumentException.class, () -> heading.getAttributeNameAt(-1));
  }

  @Test
  void gettingAttributeNameInsideBoundsReturnsTheName() {
    Heading heading = new Heading(idAtts(1));
    assertEquals("id0", heading.getAttributeNameAt(0));
  }

  @Test
  void gettingAnDomainOutsideBoundsThrowsException() {
    Heading heading = new Heading(idAtts(1));
    assertThrows(IllegalArgumentException.class, () -> heading.getDomainAt(1));
    assertThrows(IllegalArgumentException.class, () -> heading.getDomainAt(-1));
  }

  @Test
  void gettingAnDomainInsideBoundsReturnsTheDomain() {
    Heading heading = new Heading(idAtts(1));
    assertEquals(Domain.ID, heading.getDomainAt(0));
  }

  @Test
  void canIterateOverHeading() {
    Heading heading = new Heading(idAtts(5));
    int count = 0;
    for (Attribute a : heading) {
      assertEquals(new Attribute("id" + count, Domain.ID), a);
      count++;
    }
  }

  @Test
  void gettingTheIdAttributesNamesOfAnIdOnlyHeadingReturnsAllTheNames() {
    Heading heading = new Heading(idAtts(3));
    assertEquals(Set.of("id0","id1","id2"), heading.getNamesOfIdDomainAttributes());
  }

  @Test
  void gettingTheIdAttributesNamesOfAnNonIdHeadingReturnsAnEmptySet() {
    Heading heading = new Heading(intAtts(5));
    assertEquals(Collections.emptySet(), heading.getNamesOfIdDomainAttributes());
  }

  @Test
  void gettingTheIdAttributesNamesOfAMixedHeadingReturnsOnlyTheIdAttributeNames() {
    Heading heading = new Heading(append(intAtts(2), idAtts(2)));
    assertEquals(Set.of("id0","id1"), heading.getNamesOfIdDomainAttributes());
  }

  @Test
  void gettingAttributeIndicesOfNonExistingAttributesThrowsAndException() {
    Heading heading = new Heading(idAtts(3));
    assertThrows(IllegalArgumentException.class, () -> heading.getAttributeIndices(Set.of("do","not","exist")));
  }

  @Test
  void gettingAttributeIndicesOfExistingAttributesReturnsIndices() {
    Heading heading = new Heading(idAtts(3));
    assertEquals(Set.of(0,2), heading.getAttributeIndices(Set.of("id0","id2")));
  }

  @Test
  void renamingAllFieldsGivesANewHeadingWhereOnlyTheNamesAreChanged() {
    Heading heading = new Heading(idAtts(2));
    Heading renamedHeading = new Heading(createAtts(2, Domain.ID, "idd"));

    assertEquals(renamedHeading, heading.rename(Map.of("id0", "idd0", "id1", "idd1")));
  }

  @Test
  void partialRenamingGivesANewHeadingWhereSomeOfTheNamesAreChanged() {
    Heading heading = new Heading(append(createAtts(1, Domain.ID, "a"), createAtts(1, Domain.ID, "b")));
    Heading renamedHeading = new Heading(append(createAtts(1, Domain.ID, "aa"), createAtts(1, Domain.ID, "b")));

    assertEquals(renamedHeading, heading.rename(Map.of("a0", "aa0")));
  }

  private List<Attribute> append(List<Attribute> base, List<Attribute> other) {
    base.addAll(other);
    return base;
  }

  private List<Attribute> idAtts(int nr) {
    return createAtts(nr, Domain.ID, "id");
  }

  private List<Attribute> intAtts(int nr) {
    return createAtts(nr, Domain.INT, "int");
  }

  @NotNull
  private List<Attribute> createAtts(int nr, Domain d, String prefix) {
    List<Attribute> fields = new ArrayList<>(nr);

    for (int i = 0; i < nr; i++) {
      fields.add(createAtt(prefix + i, d));
    }

    return fields;
  }

  @NotNull
  private Attribute createAtt(String name, Domain d) {
    return new Attribute(name, d);
  }
}