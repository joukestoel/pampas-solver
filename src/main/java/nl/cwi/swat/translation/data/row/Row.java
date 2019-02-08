package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.smtlogic.Expression;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public interface Row extends Iterable<Expression>{
  int arity();
  Expression getAttributeAt(int i);
}

abstract class AbstractRow implements Row { }

class EmptyRow extends AbstractRow {

  static final Row EMPTY = new EmptyRow();

  private EmptyRow() {}

  @Override
  public int arity() {
    return 0;
  }

  @Override
  public Expression getAttributeAt(int i) {
    throw new IllegalArgumentException("This row has no attributes");
  }

  @NotNull
  @Override
  public Iterator<Expression> iterator() {
    return Collections.emptyIterator();
  }

}

class OneAttributeRow extends AbstractRow {
  private final Expression att;

  OneAttributeRow(@NotNull Expression att) {
    this.att = att;
  }

  @Override
  public int arity() {
    return 1;
  }

  @Override
  public Expression getAttributeAt(int i) {
    if (i != 0) {
      throw new IllegalArgumentException("Row only contains 1 attribute");
    }
    return att;
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

  @NotNull
  @Override
  public Iterator<Expression> iterator() {
    return new Iterator<>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < 1;
      }

      @Override
      public Expression next() {
        current++;
        return att;
      }
    };
  }
}

class TwoAttributesRow extends AbstractRow {
  private final Expression att1;
  private final Expression att2;

  TwoAttributesRow(@NotNull Expression att1, @NotNull Expression att2) {
    this.att1 = att1;
    this.att2 = att2;
  }

  @Override
  public int arity() {
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

  @NotNull
  @Override
  public Iterator<Expression> iterator() {
    return new Iterator<>() {
      private int current = 0;
      @Override
      public boolean hasNext() {
        return current < 2;
      }

      @Override
      public Expression next() {
        current++;
        if (current == 1) {
          return att1;
        } else if (current == 2) {
          return att2;
        } else {
          throw new IllegalStateException("Can't iterate over more than 2 attributes");
        }
      }
    };
  }
}

class ThreeAttributesRow extends AbstractRow  {
  private final Expression att1;
  private final Expression att2;
  private final Expression att3;

  ThreeAttributesRow(@NotNull Expression att1, @NotNull Expression att2, @NotNull Expression att3) {
    this.att1 = att1;
    this.att2 = att2;
    this.att3 = att3;
  }

  @Override
  public int arity() {
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

  @NotNull
  @Override
  public Iterator<Expression> iterator() {
    return new Iterator<>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < 3;
      }

      @Override
      public Expression next() {
        current++;

        switch (current) {
          case 1: return att1;
          case 2: return att2;
          case 3: return att3;
          default: throw new IllegalStateException("Can't iterate over more than 3 attributes");
        }
      }
    };
  }
}

class FourAttributesRow extends AbstractRow  {
  private final Expression att1;
  private final Expression att2;
  private final Expression att3;
  private final Expression att4;

  FourAttributesRow(@NotNull Expression att1, @NotNull Expression att2, @NotNull Expression att3, @NotNull Expression att4) {
    this.att1 = att1;
    this.att2 = att2;
    this.att3 = att3;
    this.att4 = att4;
  }

  @Override
  public int arity() {
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

  @NotNull
  @Override
  public Iterator<Expression> iterator() {
    return new Iterator<>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < 4;
      }

      @Override
      public Expression next() {
        current++;
        switch (current) {
          case 1: return att1;
          case 2: return att2;
          case 3: return att3;
          case 4: return att4;
          default: throw new IllegalStateException("Can't iterate over more than 4 attributes");
        }
      }
    };
  }
}

class FiveAttributesRow extends AbstractRow  {
  private final Expression att1;
  private final Expression att2;
  private final Expression att3;
  private final Expression att4;
  private final Expression att5;

  FiveAttributesRow(@NotNull Expression att1, @NotNull Expression att2, @NotNull Expression att3,
                           @NotNull Expression att4, @NotNull Expression att5) {
    this.att1 = att1;
    this.att2 = att2;
    this.att3 = att3;
    this.att4 = att4;
    this.att5 = att5;
  }

  @Override
  public int arity() {
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

  @NotNull
  @Override
  public Iterator<Expression> iterator() {
    return new Iterator<>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < 5;
      }

      @Override
      public Expression next() {
        current++;

        switch (current) {
          case 1: return att1;
          case 2: return att2;
          case 3: return att3;
          case 4: return att4;
          case 5: return att5;
          default: throw new IllegalStateException("Can't iterate over more than 5 attributes");
        }
      }
    };
  }
}

class NAttributeRow extends AbstractRow  {
  private final Expression[] atts;

  NAttributeRow(@NotNull Expression[] atts) {
    this.atts = atts;
  }

  @Override
  public int arity() {
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

  @NotNull
  @Override
  public Iterator<Expression> iterator() {
    return new Iterator<>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < atts.length;
      }

      @Override
      public Expression next() {
        return atts[current++];
      }
    };
  }
}
