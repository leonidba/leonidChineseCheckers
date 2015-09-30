/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationLogic;

import static java.lang.Math.*;
import applicationLogic.enums.ColorsEnum;
import applicationLogic.enums.CornersEnum;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author leon
 */
public class Cell implements Comparable
{

    private SimpleBooleanProperty changeColor = new SimpleBooleanProperty(false);
    private final int col;
    private final int row;
    private ColorsEnum color;
    private int cellIndexPerColor;
    private int distanceToTarget;
    private boolean isInCellTarget = false;

    public SimpleBooleanProperty getChangeColor()
    {
        return changeColor;
    }

    public boolean isInCellTarget()
    {
        return isInCellTarget;
    }

    public void setIsInCellTarget()
    {
        this.isInCellTarget = true;
    }

    public Cell(int row, int col)
    {
        color = ColorsEnum.Empty;
        this.col = col;
        this.row = row;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 13 * hash + this.col;
        hash = 13 * hash + this.row;
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
        {
            return false;
        }
        if (getClass() != obj.getClass())
        {
            return false;
        }
        final Cell other = (Cell) obj;
        if (this.col != other.col)
        {
            return false;
        }
        return this.row == other.row;
    }

    void moveToDestCell(Cell destCell)
    {
        destCell.PaintCell(this.getColor());
        destCell.setCellIndexPerColor(this.getCellIndexPerColor());
        this.ClearCell();
    }

    public void setCellIndexPerColor(int cellIndexPerColor)
    {
        this.cellIndexPerColor = cellIndexPerColor;
    }

    public int getCellIndexPerColor()
    {
        return cellIndexPerColor;
    }

    public int getDistanceToTarget()
    {
        return distanceToTarget;
    }

    public void setDistanceToTarget(int distance)
    {
        distanceToTarget = distance;
    }

    public boolean IsEmpty()
    {
        return color == ColorsEnum.Empty;
    }

    public boolean PaintCell(ColorsEnum colorToPlace)
    {
        boolean canPaint = false;

        if (this != null && this.IsEmpty())
        {
            this.setColor(colorToPlace);
            canPaint = true;
        }

        return canPaint;
    }

    public void ClearCell()
    {
        this.setColor(ColorsEnum.Empty);
        color = ColorsEnum.Empty;
        setCellIndexPerColor(0);
    }

    public int getCol()
    {
        return col;
    }

    public int getRow()
    {
        return row;
    }

    public ColorsEnum getColor()
    {
        return color;
    }

    public void setColor(ColorsEnum color)
    {
        this.color = color;
        changeColor.setValue(true);
        changeColor.setValue(false);
    }

    public boolean addCellToArray(Cell[] arr)
    {
        boolean res = false;
        for (int i = 0; i < arr.length; i++)
        {
            if (arr[i] == null)
            {
                res = true;
                arr[i] = this;
                this.setCellIndexPerColor(i);
                break;
            }
        }
        return res;
    }

    public boolean IsInTargetCorner(ColorsEnum srcColor)
    {
        CornersEnum targetCorner = srcColor.getTargetCorner();
        return CornersEnum.IsCellInCorner(targetCorner, this);
    }

    public static Cell clone(Cell fromCell)
    {
        Cell res = new Cell(fromCell.row, fromCell.col);
        res.setCellIndexPerColor(fromCell.cellIndexPerColor);
        res.setColor(fromCell.color);
        return res;
    }

    @Override
    public int compareTo(Object o)
    {
        return this.getCol() - ((Cell) o).getCol();
    }

    public double calcDistance(Cell destCell)
    {
        double high = abs(destCell.row - this.row);
        double width = abs(destCell.col - this.col);
        double midRes = pow(high, 2) + pow(width, 2);
        double res = sqrt(midRes);
        return res;
    }
}
