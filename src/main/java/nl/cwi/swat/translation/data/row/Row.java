package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.smtlogic.Expression;
import nl.cwi.swat.smtlogic.IdAtom;
import nl.cwi.swat.smtlogic.ints.IntConstant;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public interface Row {
  int getNrOfAttributes();
  Expression getAttributeAt(int i);

  Expression[] getAttributes();

  boolean isStable();
}

abstract class AbstractRow implements Row {
  protected boolean isStableAttribute(Expression att) {
    return att instanceof IdAtom || att instanceof IntConstant;
  }
}

class OneAttributeRow extends AbstractRow {
  private final Expression att;

  OneAttributeRow(@NotNull Expression att) {
    this.att = att;
  }

  @Override
  public int getNrOfAttributes() {
    return 1;
  }

  @Override
  public Expression getAttributeAt(int i) {
    if (i != 1) {
      throw new IllegalArgumentException("Row only contains 1 attribute");
    }
    return att;
  }

  @Override
  public Expression[] getAttributes() {
    return new Expression[]{att};
  }

  @Override
  public boolean isStable() {
    return isStableAttribute(att);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    OneAttributeRow that = (OneAttributeRow) o;

    return att.equals(that.att);
  }

  @Override
  public int hashCode() {
    return att.hashCode();
  }
}

class TwoAttributesRow extends AbstractRow {
  private final Expression att1;
  private final Expression att2;

  public TwoAttributesRow(@NotNull Expression att1, @NotNull Expression att2) {
    this.att1 = att1;
    this.att2 = att2;
  }

  @Override
  public int getNrOfAttributes() {
    return 2;
  }

  @Override
  public Expression getAttributeAt(int i) {
    switch (i) {
      case 0: return att1;
      case 1: return att2;
      default: throw new IllegalArgumentException("Row only contains 2 attributes");
    }
  }

  @Override
  public Expression[] getAttributes() {
    return new Expression[] {att1,att2};
  }

  @Override
  public boolean isStable() {
    return isStableAttribute(att1) && isStableAttribute(att2);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TwoAttributesRow that = (TwoAttributesRow) o;

    if (!att1.equals(that.att1)) return false;
    return att2.equals(that.att2);
  }

  @Override
  public int hashCode() {
    int result = att1.hashCode();
    result = 31 * result + att2.hashCode();
    return result;
  }
}

class ThreeAttributesRow extends AbstractRow  {
  private final Expression att1;
  private final Expression att2;
  private final Expression att3;

  public ThreeAttributesRow(@NotNull Expression att1, @NotNull Expression att2, @NotNull Expression att3) {
    this.att1 = att1;
    this.att2 = att2;
    this.att3 = att3;
  }

  @Override
  public int getNrOfAttributes() {
    return 3;
  }

  @Override
  public Expression getAttributeAt(int i) {
    switch (i) {
      case 0: return att1;
      case 1: return att2;
      case 2: return att3;
      default: throw new IllegalArgumentException("Row only contains 3 attributes");
    }
  }

  @Override
  public Expression[] getAttributes() {
    return new Expression[] {att1,att2,att3};
  }

  @Override
  public boolean isStable() {
    return isStableAttribute(att1) && isStableAttribute(att2) && isStableAttribute(att3);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ThreeAttributesRow that = (ThreeAttributesRow) o;

    if (!att1.equals(that.att1)) return false;
    if (!att2.equals(that.att2)) return false;
    return att3.equals(that.att3);
  }

  @Override
  public int hashCode() {
    int result = att1.hashCode();
    result = 31 * result + att2.hashCode();
    result = 31 * result + att3.hashCode();
    return result;
  }
}

class FourAttributesRow extends AbstractRow  {
  private final Expression att1;
  private final Expression att2;
  private final Expression att3;
  private final Expression att4;

  public FourAttributesRow(@NotNull Expression att1, @NotNull Expression att2, @NotNull Expression att3, @NotNull Expression att4) {
    this.att1 = att1;
    this.att2 = att2;
    this.att3 = att3;
    this.att4 = att4;
  }

  @Override
  public int getNrOfAttributes() {
    return 4;
  }

  @Override
  public Expression getAttributeAt(int i) {
    switch (i) {
      case 0: return att1;
      case 1: return att2;
      case 2: return att3;
      case 3: return att4;
      default: throw new IllegalArgumentException("Row only contains 4 attributes");
    }
  }

  @Override
  public Expression[] getAttributes() {
    return new Expression[] {att1,att2,att3,att4};
  }

  @Override
  public boolean isStable() {
    return isStableAttribute(att1) && isStableAttribute(att2) && isStableAttribute(att3) && isStableAttribute(att4);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FourAttributesRow that = (FourAttributesRow) o;

    if (!att1.equals(that.att1)) return false;
    if (!att2.equals(that.att2)) return false;
    if (!att3.equals(that.att3)) return false;
    return att4.equals(that.att4);
  }

  @Override
  public int hashCode() {
    int result = att1.hashCode();
    result = 31 * result + att2.hashCode();
    result = 31 * result + att3.hashCode();
    result = 31 * result + att4.hashCode();
    return result;
  }
}

class FiveAttributesRow extends AbstractRow  {
  private final Expression att1;
  private final Expression att2;
  private final Expression att3;
  private final Expression att4;
  private final Expression att5;

  public FiveAttributesRow(@NotNull Expression att1, @NotNull Expression att2, @NotNull Expression att3,
                           @NotNull Expression att4, @NotNull Expression att5) {
    this.att1 = att1;
    this.att2 = att2;
    this.att3 = att3;
    this.att4 = att4;
    this.att5 = att5;
  }

  @Override
  public int getNrOfAttributes() {
    return 5;
  }

  @Override
  public Expression getAttributeAt(int i) {
    switch (i) {
      case 0: return att1;
      case 1: return att2;
      case 2: return att3;
      case 3: return att4;
      case 4: return att5;
      default: throw new IllegalArgumentException("Row only contains 5 attributes");
    }
  }

  @Override
  public Expression[] getAttributes() {
    return new Expression[] {att1,att2,att3,att4,att5};
  }

  @Override
  public boolean isStable() {
    return isStableAttribute(att1) && isStableAttribute(att2) && isStableAttribute(att3) &&
            isStableAttribute(att4) && isStableAttribute(att5);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FiveAttributesRow that = (FiveAttributesRow) o;

    if (!att1.equals(that.att1)) return false;
    if (!att2.equals(that.att2)) return false;
    if (!att3.equals(that.att3)) return false;
    if (!att4.equals(that.att4)) return false;
    return att5.equals(that.att5);
  }

  @Override
  public int hashCode() {
    int result = att1.hashCode();
    result = 31 * result + att2.hashCode();
    result = 31 * result + att3.hashCode();
    result = 31 * result + att4.hashCode();
    result = 31 * result + att5.hashCode();
    return result;
  }
}

class NAttributeRow extends AbstractRow  {
  private final Expression[] atts;

  public NAttributeRow(@NotNull Expression[] atts) {
    this.atts = atts;
  }

  @Override
  public int getNrOfAttributes() {
    return atts.length;
  }

  @Override
  public Expression getAttributeAt(int i) {
    if (i < 0 || i >= atts.length) {
      throw new IllegalArgumentException(String.format("Row only contains %d attributes", atts.length));
    }

    return atts[i];
  }

  @Override
  public Expression[] getAttributes() {
    return atts;
  }

  @Override
  public boolean isStable() {
    for (Expression att : atts) {
      if (!isStableAttribute(att)) {
        return false;
      }
    }

    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NAttributeRow that = (NAttributeRow) o;

    return Arrays.equals(atts, that.atts);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(atts);
  }
}
