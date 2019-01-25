package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.smtlogic.BooleanConstant;
import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.Formula;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class RowFactory {
  public static final RowConstraint ALL_TRUE = new RowConstraint() {
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

  public static RowConstraint buildRowConstraint(@NotNull final Formula exists) {
    return new RowConstraint.ExistsOnlyRowConstaint(exists);
  }

  public static RowConstraint buildRowConstraint(@NotNull final Formula exists, @NotNull final Formula attributeConstraints) {
    return new RowConstraint.FullRowConstraint(exists, attributeConstraints);
  }

  public static Row buildRow(@NotNull final Expression... attributes) {
    if (attributes.length == 0) {
      throw new IllegalArgumentException("Can not build a row with zero attributes");
    }

    switch (attributes.length) {
      case 1: return new OneAttributeRow(attributes[0]);
      case 2: return new TwoAttributesRow(attributes[0], attributes[1]);
      case 3: return new ThreeAttributesRow(attributes[0], attributes[1], attributes[2]);
      case 4: return new FourAttributesRow(attributes[0], attributes[1], attributes[2], attributes[3]);
      case 5: return new FiveAttributesRow(attributes[0], attributes[1], attributes[2], attributes[3], attributes[4]);
      default: return new NAttributeRow(attributes);
    }
  }

  public static Row buildPartialRow(@NotNull final Row original, @NotNull final List<Integer> attributeIndices) {
    final Expression[] partialAtts = new Expression[attributeIndices.size()];

    int newIndex = 0;
    for (int i : attributeIndices) {
      partialAtts[newIndex] = original.getAttributeAt(i);
      newIndex += 1;
    }

    return buildRow(partialAtts);
  }

  public static Row merge(@NotNull final Row left, @NotNull final Row right, List<Integer> columnsToSkipRight) {
    final Expression[] leftAtts = left.getAttributes();
    final Expression[] rightAtts = right.getAttributes();

    // Create the new attributes, copying all elements from the left row
    final Expression[] newAttributes = Arrays.copyOf(leftAtts, leftAtts.length + (rightAtts.length - columnsToSkipRight.size()));

    // Now append all attributes from the right row skipping the tagged columns

    int newAttsIndex = leftAtts.length;
    for (int i = 0; i < rightAtts.length; i++) {
      if (!columnsToSkipRight.contains(i)) {
        newAttributes[newAttsIndex] = rightAtts[i];
        newAttsIndex += 1;
      }
    }

    return buildRow(newAttributes);
  }
}
