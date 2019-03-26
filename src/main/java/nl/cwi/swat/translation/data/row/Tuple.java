package nl.cwi.swat.translation.data.row;

import nl.cwi.swat.formulacircuit.Expression;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

public interface Tuple extends Iterable<Expression>{
  int arity();
  Expression getAttributeAt(int i);
}

abstract class AbstractTuple implements Tuple { }

class EmptyTuple extends AbstractTuple {

  static final Tuple EMPTY = new EmptyTuple();

  private EmptyTuple() {}

  @Override
  public int arity() {
    return 0;
  }

  @Override
  public Expression getAttributeAt(int i) {
    throw new IllegalArgumentException("This row has no attributes");
  }

  @NonNull
  @Override
  public Iterator<Expression> iterator() {
    return Collections.emptyIterator();
  }

  @Override
  public String toString() {
    return "<>";
  }
}

class UnaryTuple extends AbstractTuple {
  private final Expression att;

  UnaryTuple(@NonNull Expression att) {
    this.att = att;
  }

  @Override
  public int arity() {
    return 1;
  }

  @Override
  public Expression getAttributeAt(int i) {
    if (i != 0) {
      throw new IllegalArgumentException("Tuple only contains 1 attribute");
    }
    return att;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    UnaryTuple that = (UnaryTuple) o;

    return att.equals(that.att);
  }

  @Override
  public int hashCode() {
    return att.hashCode();
  }

  @NonNull
  @Override
  public Iterator<Expression> iterator() {
    return new Iterator<Expression>() {
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

  @Override
  public String toString() {
    return "<" + att + ">";
  }
}

class BinaryTuple extends AbstractTuple {
  private final Expression att1;
  private final Expression att2;

  BinaryTuple(@NonNull Expression att1, @NonNull Expression att2) {
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
      default: throw new IllegalArgumentException("Tuple only contains 2 attributes");
    }
  }

  @Override
  public String toString() {
    return "<" + att1 + "," + att2 + ">";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    BinaryTuple that = (BinaryTuple) o;

    if (!att1.equals(that.att1)) return false;
    return att2.equals(that.att2);
  }

  @Override
  public int hashCode() {
    int result = att1.hashCode();
    result = 31 * result + att2.hashCode();
    return result;
  }

  @NonNull
  @Override
  public Iterator<Expression> iterator() {
    return new Iterator<Expression>() {
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

class TernaryTuple extends AbstractTuple {
  private final Expression att1;
  private final Expression att2;
  private final Expression att3;

  TernaryTuple(@NonNull Expression att1, @NonNull Expression att2, @NonNull Expression att3) {
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
      default: throw new IllegalArgumentException("Tuple only contains 3 attributes");
    }
  }

  @Override
  public String toString() {
    return "<" + att1 + "," + att2 + "," + att3 + ">";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TernaryTuple that = (TernaryTuple) o;

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

  @NonNull
  @Override
  public Iterator<Expression> iterator() {
    return new Iterator<Expression>() {
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

class FourAttributesTuple extends AbstractTuple {
  private final Expression att1;
  private final Expression att2;
  private final Expression att3;
  private final Expression att4;

  FourAttributesTuple(@NonNull Expression att1, @NonNull Expression att2, @NonNull Expression att3, @NonNull Expression att4) {
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
      default: throw new IllegalArgumentException("Tuple only contains 4 attributes");
    }
  }

  @Override
  public String toString() {
    return "<" + att1 + "," + att2 + "," + att3 + "," + att4 + ">";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FourAttributesTuple that = (FourAttributesTuple) o;

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

  @NonNull
  @Override
  public Iterator<Expression> iterator() {
    return new Iterator<Expression>() {
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

class FiveAttributesTuple extends AbstractTuple {
  private final Expression att1;
  private final Expression att2;
  private final Expression att3;
  private final Expression att4;
  private final Expression att5;

  FiveAttributesTuple(@NonNull Expression att1, @NonNull Expression att2, @NonNull Expression att3,
                      @NonNull Expression att4, @NonNull Expression att5) {
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
      default: throw new IllegalArgumentException("Tuple only contains 5 attributes");
    }
  }

  @Override
  public String toString() {
    return "<" + att1 + "," + att2 + "," + att3 + "," + att4 + "," + att5 + ">";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    FiveAttributesTuple that = (FiveAttributesTuple) o;

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

  @NonNull
  @Override
  public Iterator<Expression> iterator() {
    return new Iterator<Expression>() {
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

class NaryTuple extends AbstractTuple {
  private final Expression[] atts;

  NaryTuple(@NonNull Expression[] atts) {
    this.atts = atts;
  }

  @Override
  public int arity() {
    return atts.length;
  }

  @Override
  public Expression getAttributeAt(int i) {
    if (i < 0 || i >= atts.length) {
      throw new IllegalArgumentException(String.format("Tuple only contains %d attributes", atts.length));
    }

    return atts[i];
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("<");
    for (int i = 0; i < atts.length; i++) {
      sb.append(atts[i]);
    }
    sb.append(">");
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NaryTuple that = (NaryTuple) o;

    return Arrays.equals(atts, that.atts);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(atts);
  }

  @NonNull
  @Override
  public Iterator<Expression> iterator() {
    return new Iterator<Expression>() {
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
