package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.smtlogic.BooleanConstant;
import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.Formula;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Factory class that constructs {@link Tuple}. Contains factory methods to build partial rows and methods to merge two
 * rows together.
 */
public class RowFactory {
  public static final Constraint ALL_TRUE = new Constraint() {
    @Override
    public Formula exists() {
      return BooleanConstant.TRUE;
    }

    @Override
    public Formula attributeConstraints() {
      return BooleanConstant.TRUE;
    }

    @Override
    public Formula combined() {
      return BooleanConstant.TRUE;
    }
  };

  public static Constraint buildRowConstraint(@NotNull final Formula exists) {
    return new Constraint.ExistsOnlyConstraint(exists);
  }

  public static Constraint buildRowConstraint(@NotNull final Formula exists, @NotNull final Formula attributeConstraints) {
    return new Constraint.FullConstraint(exists, attributeConstraints);
  }

  /**
   * Builds a row of arity {@code attributes.length} containing the {@code attributes}
   * @param attributes an array of the attributes that are part of the new row
   * @return a row of arity (@code attributes.length} containing the passed in {@code attributes}
   */
  public static Tuple buildTuple(@NotNull final Expression... attributes) {
    switch (attributes.length) {
      case 0: return EmptyTuple.EMPTY;
      case 1: return new UnaryTuple(attributes[0]);
      case 2: return new BinaryTuple(attributes[0], attributes[1]);
      case 3: return new TernaryTuple(attributes[0], attributes[1], attributes[2]);
      case 4: return new FourAttributesTuple(attributes[0], attributes[1], attributes[2], attributes[3]);
      case 5: return new FiveAttributesTuple(attributes[0], attributes[1], attributes[2], attributes[3], attributes[4]);
      default: return new NAttributeTuple(attributes);
    }
  }

  /**
   * Builds a row containing only those attributes of which its indices are contained in the {@code attributeIndices}
   * list.
   * @param original the original row
   * @param attributeIndices the attributes to include in de partial row
   * @return a partial row containing only those attributes from the original iff its original index is contained in the
   *   {@code attributeIndices} list
   */
  public static Tuple buildPartialTuple(@NotNull final Tuple original, @NotNull final List<Integer> attributeIndices) {
    final Expression[] partialAtts = new Expression[attributeIndices.size()];

    int newIndex = 0;
    for (int i : attributeIndices) {
      partialAtts[newIndex] = original.getAttributeAt(i);
      newIndex += 1;
    }

    return buildTuple(partialAtts);
  }

  /**
   * Merges two rows in such a way that the attributes of the {@code base} row always are in the merged row and all
   * the attributes of the {@code other} row of which the indices are not contained in the {@code skipPosition} list are
   * appended to the end of the base row
   *
   * @param base the row to use as base
   * @param other the row that is appended to the base row
   * @param skipPositions the indices of the attributes that need to be skipped in the other row
   * @return a merged row containing all the attributes of the base row and the non-skipped attributes of the other row
   * @throws IllegalArgumentException when the skipPosition list contains indices outside the bounds of the other row
   */
  public static Tuple merge(@NotNull final Tuple base, @NotNull final Tuple other, @NotNull List<Integer> skipPositions) {
    for (int i : skipPositions) {
      if (i < 0 || i >= other.arity()) {
        throw new IllegalArgumentException("List with indices to skip contains indices outside the bounds of the 'other' row");
      }
    }

    final Expression[] exprs = Arrays.copyOf(getExpressions(base),base.arity() + other.arity() - skipPositions.size());

    int addIndex = base.arity();
    for (int i = 0; i < other.arity(); i++) {
      if (!skipPositions.contains(i)) {
        exprs[addIndex] = other.getAttributeAt(i);
        addIndex++;
      }
    }

    return buildTuple(exprs);
  }

  private static Expression[] getExpressions(@NotNull Tuple tuple) {
    final Expression[] exprs = new Expression[tuple.arity()];

    for (int i = 0; i < tuple.arity(); i++) {
      exprs[i] = tuple.getAttributeAt(i);
    }

    return exprs;
  }

}
