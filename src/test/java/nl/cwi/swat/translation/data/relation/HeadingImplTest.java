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
  void headingWithOnlyIdFieldsMustReturnThatItOnlyHasIds() {
    HeadingImpl heading = new HeadingImpl(idFields(2));
    assertTrue(heading.idFieldsOnly());
  }

  @Test
  void headingWithMixedDomainsMustReturnThatItsNotIdOnly() {
    HeadingImpl heading = new HeadingImpl(append(idFields(1), intFields(1)));
    assertFalse(heading.idFieldsOnly());
  }


  private List<FieldDefinition> append(List<FieldDefinition> base, List<FieldDefinition> other) {
    base.addAll(other);
    return base;
  }

  private List<FieldDefinition> idFields(int nr) {
    return createFields(nr, IdDomain.ID, "id");
  }

  private List<FieldDefinition> intFields(int nr) {
    return createFields(nr, IntDomain.INT, "int");
  }

  @NotNull
  private List<FieldDefinition> createFields(int nr, Domain d, String prefix) {
    List<FieldDefinition> fields = new ArrayList<>(nr);

    for (int i = 0; i < nr; i++) {
      fields.add(new FieldDefinition(i, prefix + i, d));
    }

    return fields;
  }
}