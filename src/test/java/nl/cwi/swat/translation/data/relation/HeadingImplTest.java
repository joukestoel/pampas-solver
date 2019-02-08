package nl.cwi.swat.translation.data.relation;

import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.ast.ints.IntDomain;
import nl.cwi.swat.ast.relational.IdDomain;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HeadingImplTest {
  @Test
  void headingMustHaveDistinctFieldNames() {
    assertThrows(IllegalArgumentException.class, () -> new HeadingImpl(
            List.of(new Attribute("same", IdDomain.ID),
                    new Attribute("same", IdDomain.ID))));
  }

  @Test
  void headingWithOnlyIdFieldsMustReturnThatItOnlyHasIds() {
    Heading heading = new HeadingImpl(idFields(2));
    assertTrue(heading.containsOnlyIdAttributes());
  }

  @Test
  void headingWithMixedDomainsMustReturnThatItsNotIdOnly() {
    Heading heading = new HeadingImpl(append(idFields(1), intFields(1)));
    assertFalse(heading.containsOnlyIdAttributes());
  }

  @Test
  void headingWithOneFieldHasArityOne() {
    Heading heading = new HeadingImpl(idFields(1));
    assertEquals(1, heading.arity());
  }

  @Test
  void headingWithTenFieldsHasArityTen() {
    Heading heading = new HeadingImpl(append(idFields(8), intFields(2)));
    assertEquals(10, heading.arity());
  }

  @Test
  void twoHeadingsWithSameFieldDefinitionsAreUnionCompatible() {
    Heading left = new HeadingImpl(idFields(5));
    Heading right = new HeadingImpl(idFields(5));

    assertTrue(left.isUnionCompatible(right));
  }

  @Test
  void twoHeadingOfSameArityButDifferentDomainsAreNotUnionCompatible() {
    Heading left = new HeadingImpl(idFields(2));
    Heading right = new HeadingImpl(append(idFields(1), intFields(1)));

    assertFalse(left.isUnionCompatible(right));
  }

  @Test
  void twoHeadingsWithDifferentArityOfSameDomainAreNotUnionCompatible() {
    Heading left = new HeadingImpl(idFields(1));
    Heading right = new HeadingImpl(idFields(2));

    assertFalse(left.isUnionCompatible(right));
  }


  private List<Attribute> append(List<Attribute> base, List<Attribute> other) {
    base.addAll(other);
    return base;
  }

  private List<Attribute> idFields(int nr) {
    return createFields(nr, IdDomain.ID, "id");
  }

  private List<Attribute> intFields(int nr) {
    return createFields(nr, IntDomain.INT, "int");
  }

  @NotNull
  private List<Attribute> createFields(int nr, Domain d, String prefix) {
    List<Attribute> fields = new ArrayList<>(nr);

    for (int i = 0; i < nr; i++) {
      fields.add(new Attribute(prefix + i, d));
    }

    return fields;
  }
}