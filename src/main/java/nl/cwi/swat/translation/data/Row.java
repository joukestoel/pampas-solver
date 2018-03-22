package nl.cwi.swat.translation.data;

import java.util.Arrays;
import java.util.Objects;

public abstract class Row {
    public abstract Cell cellAt(int p);
}

class RowWith1 extends Row {
  private final Cell cell0;


  RowWith1(Cell cell0) {
    this.cell0 = cell0;
  }

  @Override
  public Cell cellAt(int p) {
    if (p == 0) {
      return cell0;
    }
    else {
      throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RowWith1 rowWith1 = (RowWith1) o;
    return Objects.equals(cell0, rowWith1.cell0);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cell0);
  }
}

class RowWith2 extends Row {
  private final Cell cell0;
  private final Cell cell1;

  public RowWith2(Cell cell0, Cell cell1) {
    this.cell0 = cell0;
    this.cell1 = cell1;
  }

  @Override
  public Cell cellAt(int p) {
    switch (p) {
      case 0: return cell0;
      case 1: return cell1;
      default: throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RowWith2 rowWith2 = (RowWith2) o;
    return Objects.equals(cell0, rowWith2.cell0) &&
            Objects.equals(cell1, rowWith2.cell1);
  }

  @Override
  public int hashCode() {

    return Objects.hash(cell0, cell1);
  }
}

class RowWith3 extends Row {
  private final Cell cell0;
  private final Cell cell1;
  private final Cell cell2;

  public RowWith3(Cell cell0, Cell cell1, Cell cell2) {
    this.cell0 = cell0;
    this.cell1 = cell1;
    this.cell2 = cell2;
  }

  @Override
  public Cell cellAt(int p) {
    switch(p) {
      case 0: return cell0;
      case 1: return cell1;
      case 2: return cell2;
      default: throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RowWith3 rowWith3 = (RowWith3) o;
    return Objects.equals(cell0, rowWith3.cell0) &&
            Objects.equals(cell1, rowWith3.cell1) &&
            Objects.equals(cell2, rowWith3.cell2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cell0, cell1, cell2);
  }
}

class RowWith4 extends Row {
  private final Cell cell0;
  private final Cell cell1;
  private final Cell cell2;
  private final Cell cell3;

  public RowWith4(Cell cell0, Cell cell1, Cell cell2, Cell cell3) {
    this.cell0 = cell0;
    this.cell1 = cell1;
    this.cell2 = cell2;
    this.cell3 = cell3;
  }

  @Override
  public Cell cellAt(int p) {
    switch(p) {
      case 0: return cell0;
      case 1: return cell1;
      case 2: return cell2;
      case 3: return cell3;
      default: throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RowWith4 rowWith4 = (RowWith4) o;
    return Objects.equals(cell0, rowWith4.cell0) &&
            Objects.equals(cell1, rowWith4.cell1) &&
            Objects.equals(cell2, rowWith4.cell2) &&
            Objects.equals(cell3, rowWith4.cell3);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cell0, cell1, cell2, cell3);
  }
}

class RowWith5 extends Row {
  private final Cell cell0;
  private final Cell cell1;
  private final Cell cell2;
  private final Cell cell3;
  private final Cell cell4;

  public RowWith5(Cell cell0, Cell cell1, Cell cell2, Cell cell3, Cell cell4) {
    this.cell0 = cell0;
    this.cell1 = cell1;
    this.cell2 = cell2;
    this.cell3 = cell3;
    this.cell4 = cell4;
  }

  @Override
  public Cell cellAt(int p) {
    switch(p) {
      case 0: return cell0;
      case 1: return cell1;
      case 2: return cell2;
      case 3: return cell3;
      case 4: return cell4;
      default: throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RowWith5 rowWith5 = (RowWith5) o;
    return Objects.equals(cell0, rowWith5.cell0) &&
            Objects.equals(cell1, rowWith5.cell1) &&
            Objects.equals(cell2, rowWith5.cell2) &&
            Objects.equals(cell3, rowWith5.cell3) &&
            Objects.equals(cell4, rowWith5.cell4);
  }

  @Override
  public int hashCode() {

    return Objects.hash(cell0, cell1, cell2, cell3, cell4);
  }
}

class RowWith6 extends Row {
  private final Cell cell0;
  private final Cell cell1;
  private final Cell cell2;
  private final Cell cell3;
  private final Cell cell4;
  private final Cell cell5;

  public RowWith6(Cell cell0, Cell cell1, Cell cell2, Cell cell3, Cell cell4, Cell cell5) {
    this.cell0 = cell0;
    this.cell1 = cell1;
    this.cell2 = cell2;
    this.cell3 = cell3;
    this.cell4 = cell4;
    this.cell5 = cell5;
  }

  @Override
  public Cell cellAt(int p) {
    switch(p) {
      case 0: return cell0;
      case 1: return cell1;
      case 2: return cell2;
      case 3: return cell3;
      case 4: return cell4;
      case 5: return cell5;
      default: throw new IndexOutOfBoundsException();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RowWith6 rowWith6 = (RowWith6) o;
    return Objects.equals(cell0, rowWith6.cell0) &&
            Objects.equals(cell1, rowWith6.cell1) &&
            Objects.equals(cell2, rowWith6.cell2) &&
            Objects.equals(cell3, rowWith6.cell3) &&
            Objects.equals(cell4, rowWith6.cell4) &&
            Objects.equals(cell5, rowWith6.cell5);
  }

  @Override
  public int hashCode() {

    return Objects.hash(cell0, cell1, cell2, cell3, cell4, cell5);
  }
}

class RowWithN extends Row {
  private final Cell[] cells;

  RowWithN(Cell... cells) {
    this.cells = cells;
  }

  @Override
  public Cell cellAt(int p) {
    if (p < 0 || p >= cells.length) {
      throw new IndexOutOfBoundsException();
    }

    return cells[p];
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    RowWithN rowWithN = (RowWithN) o;
    return Arrays.equals(cells, rowWithN.cells);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(cells);
  }
}

class RowFactory {
  public static Row build(Cell... cells) {
    switch(cells.length) {
      case 1:
        return new RowWith1(cells[0]);
      case 2:
        return new RowWith2(cells[0], cells[1]);
      case 3:
        return new RowWith3(cells[0], cells[1], cells[2]);
      case 4:
        return new RowWith4(cells[0], cells[1], cells[2], cells[3]);
      case 5:
        return new RowWith5(cells[0], cells[1], cells[2], cells[3], cells[4]);
      case 6:
        return new RowWith6(cells[0], cells[1], cells[2], cells[3], cells[4], cells[5]);
      default:
        return new RowWithN(cells);
    }
  }
}