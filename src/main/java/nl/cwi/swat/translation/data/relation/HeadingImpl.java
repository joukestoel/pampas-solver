package nl.cwi.swat.translation.data.relation;

import nl.cwi.swat.ast.Domain;
import nl.cwi.swat.ast.ints.IntDomain;
import nl.cwi.swat.ast.relational.Id;
import nl.cwi.swat.ast.relational.IdDomain;
import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.IdAtom;
import nl.cwi.swat.smtlogic.ints.IntConstant;
import nl.cwi.swat.smtlogic.ints.IntVariable;
import nl.cwi.swat.translation.data.row.Row;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class HeadingImpl implements Heading {
  private final List<FieldDefinition> fields;

  private boolean idsOnly = true;

  HeadingImpl(@NotNull List<FieldDefinition> fields) {
    Iterator<FieldDefinition> fi = fields.iterator();

    while (fi.hasNext() && idsOnly) {
      FieldDefinition fd = fi.next();
      idsOnly = fd.getDomain() == IdDomain.ID;
    }

    this.fields = Collections.unmodifiableList(fields);
  }

  public int getAttributeIndex(String attributeName) {
    for (int i = 0; i < fields.size(); i++) {
      if (attributeName.equals(fields.get(i).getName())) {
        return i;
      }
    }

    throw new IllegalArgumentException(String.format("Relation does not have an attribute with the name %s", attributeName));
  }

  public List<Integer> getAttributeIndices(Set<String> attributeNames) {
    List<Integer> indices = new ArrayList<>(attributeNames.size());

    for (int i = 0; i < fields.size(); i++) {
      if (attributeNames.contains(fields.get(i).getName())) {
        indices.add(i);
      }
    }

    return indices;
  }

  @Override
  public int arity() {
    return fields.size();
  }

  @NotNull
  @Override
  public Iterator<FieldDefinition> iterator() {
    return fields.iterator();
  }

  public boolean isUnionCompatible(Relation other) {
    Heading otherHeading = other.getHeading();

    if (otherHeading.arity() != this.arity()) {
      return false;
    }

    return this.equals(otherHeading);
  }

  @Override
  public Set<String> fieldNamesOnly() {
    return fields.stream()
            .map(FieldDefinition::getName)
            .collect(Collectors.toSet());
  }

  @Override
  public Set<String> getIdFieldNames() {
    return fields.stream()
            .filter(f -> f.getDomain() == IdDomain.ID)
            .map(FieldDefinition::getName)
            .collect(Collectors.toSet());
  }

  /**
   * @param index - needs to be in the range of fields
   * @return
   */
  @Override
  public String getFieldNameAt(int index) {
    if (index < 0 || index >= arity()) {
      throw new IllegalArgumentException("Provided index is outside the arity of the relation");
    }

    return fields.get(index).getName();
  }

  @Override
  public Domain getDomainAt(int index) {
    if (index < 0 || index >= arity()) {
      throw new IllegalArgumentException("Provided index is outside the arity of the relation");
    }

    return fields.get(index).getDomain();
  }

  /**
   * Creates a new heading with some fields renamed.
   *
   * @param renamedFields the fields to rename
   * @requires \forall String a | renamedFields.containsKey(a) | fields.has(a),
   * @return new {@ref HeadingImpl} with the fields renamed
   * @throws IllegalArgumentException if
   *  not all fields in the {@code renamedFields} param were part of the original heading
   */
  @Override
  public Heading rename(@NotNull Map<String, String> renamedFields) {
    if (renamedFields.size() > arity()) {
      throw new IllegalArgumentException("The number of fields to rename is larger than the number of fields in the heading");
    }

    int nrOfRenamedFields = 0;

    List<FieldDefinition> newFields = new ArrayList<>(fields);

    for (int i = 0; i < newFields.size(); i++) {
      FieldDefinition fd = newFields.get(i);

      if (renamedFields.containsKey(fd.getName())) {
        newFields.set(i, new FieldDefinition(i, renamedFields.get(fd.getName()), fd.getDomain()));
        nrOfRenamedFields += 1;
      }
    }

    if (nrOfRenamedFields != renamedFields.size()) {
      throw new IllegalArgumentException("Not all renamings could be applied. Do all the renamed fields exist?");
    }

    return new HeadingImpl(newFields);
  }

  /**
   * Creates a new heading with some fields projected.
   *
   * @param projectedFields the fields to project out of the heading.
   * @requires \forall String a | projectedFields.contains(a) | fields.has(a)
   * @return new {@link HeadingImpl} containing only the projected fields.
   * @throws IllegalArgumentException if
   *  not all fields in the {@code projectedFields} were part of the original heading
   */
  @Override
  public Heading project(@NotNull Set<String> projectedFields) {
    int nrOfProjectedFields = 0;

    List<FieldDefinition> newFields = new ArrayList<>(projectedFields.size());

    for (FieldDefinition fd : fields) {
      if (projectedFields.contains(fd.getName())) {
        newFields.add(fd);
        nrOfProjectedFields += 1;
      }
    }

    if (nrOfProjectedFields != projectedFields.size()) {
      throw new IllegalArgumentException("Not all fields could be projected. Do all the projected fields exist?");
    }

    return new HeadingImpl(newFields);
  }

  @Override
  public Heading join(@NotNull Heading other) {
    ArrayList<FieldDefinition> joinedFields = new ArrayList<>(fields);
    for (FieldDefinition fd : other) {
      if (!joinedFields.contains(fd)) {
        joinedFields.add(fd);
      }
    }

    return new HeadingImpl(joinedFields);
  }

  @Override
  public Set<String> intersect(@NotNull Heading other) {
    Set<String> fieldNames = fieldNamesOnly();
    fieldNames.retainAll(other.fieldNamesOnly());
    return fieldNames;
  }

  @Override
  public boolean idFieldsOnly() {
    return idsOnly;
  }

  @Override
  public boolean isRowCompatible(Row row) {
    if (row.arity() != arity()) {
      return false;
    }

    for (FieldDefinition fd : this) {
      Expression att = row.getAttributeAt(fd.getPosition());

      if (fd.getDomain() == IdDomain.ID && !(att instanceof IdAtom)) {
        return false;
      } else if (fd.getDomain() == IntDomain.INT && (!(att instanceof IntConstant || att instanceof IntVariable))) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    HeadingImpl that = (HeadingImpl) o;

    return fields.equals(that.fields);
  }

  @Override
  public int hashCode() {
    return fields.hashCode();
  }
}
