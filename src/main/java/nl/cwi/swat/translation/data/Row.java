package nl.cwi.swat.translation.data;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public abstract class Row {
  protected boolean stable;

  public abstract Cell cellAt(int p);
  public abstract Cell[] stableCells();
  protected abstract Cell[] filter(int[] cellsToFilter);
  protected abstract Cell[] asArray();

  public boolean isStable() {
    return this.stable;
  }

  protected Cell[] filter(int[] cellsToFilter, int nrOfCellsInRow) {
    Cell[] filtered = new Cell[nrOfCellsInRow - cellsToFilter.length];

    if (filtered.length == 0) {
      return filtered;
    }

    int currentIndex = 0;
    for (int i = 0; i < 2; i++) {
      boolean remove = false;
      for (int j = 0; j < cellsToFilter.length; j++) {
        if (i == cellsToFilter[j]) {
          remove = true;
        }
      }

      if (!remove) {
        filtered[currentIndex] = cellAt(i);
        currentIndex++;
      }
    }

    return filtered;
  }

  protected Cell[] empty() {
    return new Cell[0];
  }

  public Row append(Row other, int[] cellsToJoinInOther) {
    Cell[] joinedCells = other.filter(cellsToJoinInOther);
    if (joinedCells.length == 0) {
      return this;
    }
    return RowFactory.join(this.asArray(), joinedCells);
  }
}

class EmptyRow extends Row {
  public EmptyRow() {
    this.stable = true;
  }

  @Override
  public Cell cellAt(int p) {
    throw new IndexOutOfBoundsException();
  }

  @Override
  public boolean isStable() {
    return true;
  }

  @Override
  public Cell[] stableCells() {
    return empty();
  }

  @Override
  public Cell[] filter(int[] cellsToFilter) {
    return empty();
  }

  public Cell[] asArray() {
    return empty();
  }
}

class RowWith1 extends Row {
  private final Cell cell0;

  RowWith1(Cell cell0) {
    this.cell0 = cell0;

    this.stable = cell0.isStable();
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
  public Cell[] stableCells() {
    if (stable) {
      return asArray();
    } else {
      return empty();
    }
  }

  @Override
  public Cell[] filter(int[] cellsToFilter) {
    return empty();
  }

  @Override
  public Cell[] asArray() {
    return new Cell[]{cell0};
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

    this.stable = this.cell0.isStable() && this.cell1.isStable();
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
  public Cell[] stableCells() {
    if (stable) {
      return asArray();
    }

    Cell[] stableCells = new Cell[]{cell0.isStable() ? cell0 : cell1.isStable() ? cell1 : null};
    if (stableCells[0] == null) {
      return empty();
    } else {
      return stableCells;
    }
  }

  @Override
  public Cell[] asArray() {
    return new Cell[]{cell0,cell1};
  }

  @Override
  protected Cell[] filter(int[] cellsToFilter) {
    return filter(cellsToFilter,2);
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

    this.stable = cell0.isStable() && cell1.isStable() && cell2.isStable();
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
  public Cell[] stableCells() {
    if (stable) {
      return asArray();
    }

    Cell[] stableCells = new Cell[]{cell0.isStable() ? cell0 : cell1.isStable() ? cell1 : cell2.isStable() ? cell2 : null};
    if (stableCells[0] == null) {
      return empty();
    } else {
      return stableCells;
    }

  }

  @Override
  public Cell[] asArray() {
    return new Cell[]{cell0,cell1,cell2};
  }

  @Override
  protected Cell[] filter(int[] cellsToFilter) {
    return filter(cellsToFilter,3);
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

    this.stable = cell0.isStable() && cell1.isStable() && cell2.isStable() && cell3.isStable();
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
  public Cell[] stableCells() {
    if (stable) {
      return asArray();
    }

    Cell[] stableCells = new Cell[]{cell0.isStable() ? cell0 : cell1.isStable() ? cell1 : cell2.isStable() ? cell2 : cell3.isStable() ? cell3 : null};
    if (stableCells[0] == null) {
      return empty();
    } else {
      return stableCells;
    }
  }

  @Override
  public Cell[] asArray() {
    return new Cell[]{cell0,cell1,cell2,cell3};
  }

  @Override
  protected Cell[] filter(int[] cellsToFilter) {
    return filter(cellsToFilter,4);
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

    this.stable = cell0.isStable() && cell1.isStable() && cell2.isStable() && cell3.isStable() && cell4.isStable();
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
  public Cell[] stableCells() {
    if (stable) {
      return asArray();
    }

    Cell[] stableCells = new Cell[]{cell0.isStable() ? cell0 : cell1.isStable() ? cell1 : cell2.isStable() ? cell2 : cell3.isStable() ? cell3 : cell4.isStable() ? cell4 : null};
    if (stableCells[0] == null) {
      return empty();
    } else {
      return stableCells;
    }
  }

  @Override
  public Cell[] asArray() {
    return new Cell[]{cell0,cell1,cell2,cell3,cell4};
  }

  @Override
  protected Cell[] filter(int[] cellsToFilter) {
    return filter(cellsToFilter,5);
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

    this.stable = cell0.isStable() && cell1.isStable() && cell2.isStable() && cell3.isStable() && cell4.isStable() && cell5.isStable();
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
  public Cell[] stableCells() {
    if (stable) {
      return asArray();
    }

    Cell[] stableCells = new Cell[]{cell0.isStable() ? cell0 : cell1.isStable() ? cell1 : cell2.isStable() ? cell2 : cell3.isStable() ? cell3 : cell4.isStable() ? cell4 : cell5.isStable() ? cell5 : null};
    if (stableCells[0] == null) {
      return empty();
    } else {
      return stableCells;
    }
  }

  @Override
  public Cell[] asArray() {
    return new Cell[]{cell0,cell1,cell2,cell3,cell4,cell5};
  }

  @Override
  protected Cell[] filter(int[] cellsToFilter) {
    return filter(cellsToFilter,6);
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

    this.stable = true;
    for (int i= 0; i < cells.length; i++) {
      this.stable &= cells[i].isStable();
    }
  }

  @Override
  public Cell cellAt(int p) {
    if (p < 0 || p >= cells.length) {
      throw new IndexOutOfBoundsException();
    }

    return cells[p];
  }

  @Override
  public Cell[] stableCells() {
    if (stable) {
      return cells;
    }

    int nrOfStableCells = 0;
    for (int i = 0; i < cells.length; i++) {
      if (cells[i].isStable()) {
        nrOfStableCells++;
      }
    }

    Cell[] stableCells = new Cell[nrOfStableCells];
    int j = 0;
    for (int i = 0; i < cells.length; i++) {
      if (cells[i].isStable()) {
        stableCells[j] = cells[i];
        j++;
      }
    }

    return stableCells;
  }

  @Override
  public Cell[] asArray() {
    return cells;
  }

  @Override
  protected Cell[] filter(int[] cellsToFilter) {
    return filter(cellsToFilter,cells.length);
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
      case 0:
        return new EmptyRow();
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

  public static Row join(Cell[] left, Cell[] right) {
    Cell[] joined = new Cell[left.length + right.length];
    System.arraycopy(left, 0, joined, 0, left.length);
    System.arraycopy(right, 0, joined, left.length, right.length);

    return build(joined);
  }

  public static Row build(Map<String,Cell> tuple, Heading heading) {
    Cell[] row = new Cell[tuple.size()];
    for (String att : tuple.keySet()) {
      row[heading.position(att)] = tuple.get(att);
    }

    return build(row);
  }
}