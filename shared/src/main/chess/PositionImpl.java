package chess;

import java.util.Objects;

public class PositionImpl implements ChessPosition{
    private int row;
    private int column;

    public PositionImpl(int ro, int co) {
        row = ro;
        column = co;
    }

    public PositionImpl() {

    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PositionImpl position)) return false;
        return row == position.row && column == position.column;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, column);
    }

    @Override
    public String toString() {
        return "PositionImpl{" +
                "row=" + row +
                ", column=" + column +
                '}';
    }
}
