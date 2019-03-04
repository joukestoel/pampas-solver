package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.smtlogic.Expression;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;

/**
 * Factory class that constructs a {@link Tuple}. Contains factory methods to build partial tuples from base tuples
 * and methods to merge two tuples together.
 */
public class TupleFactory {
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
      default: return new NaryTuple(attributes);
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
  public static Tuple buildPartialTuple(@NotNull final Tuple original, @NotNull final Set<Integer> attributeIndices) {
    if (attributeIndices.size() > original.arity()) {
      throw new IllegalArgumentException("Can not build a partial tuple with more attributes than the original");
    }

    final Expression[] partialAtts = new Expression[attributeIndices.size()];

    int newIndex = 0;
    for (int i : attributeIndices) {
      partialAtts[newIndex] = original.getAttributeAt(i);
      newIndex += 1;
    }

    return buildTuple(partialAtts);
  }

  /**
   * Merges two tuples in such a way that the attributes of the {@code base} tuple always are in the beginning of the
   * merged tuple and all the attributes of the {@code other} tuple of which the indices are not contained in
   * the {@code skipPosition} list are appended to the end of the base tuple.
   *
   * @param base the tuple to use as base
   * @param other the tuple that is appended to the base row
   * @param skipPositions the indices of the attributes that need to be skipped in the {@code other} tuple
   * @return a merged tuple containing all the attributes of the {@code base} tuple and the non-skipped attributes of the {@code other} tuple
   * @throws IllegalArgumentException when the skipPosition list contains indices outside the bounds of the {@code other} tuple
   */
  public static Tuple merge(@NotNull final Tuple base, @NotNull final Tuple other, @NotNull Set<Integer> skipPositions) {
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
