/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationLogic.enums;

import java.util.Arrays;
import java.util.Comparator;
import applicationLogic.Cell;

/**
 *
 * @author leon
 */
public enum CornersEnum
{

    UP,
    RIGHT_UP,
    RIGHT_DOWN,
    DOWN,
    LEFT_DOWN,
    LEFT_UP;

    final static int QUARTER = 4;
    final static int MIDDLE = 9;
    final static int THREE_QUARTERS = 13;

    private static Cell[][] cornersCells = new Cell[6][10];

    public static void SortAllCornersByIndex()
    {
        for (Cell[] cornersCell : cornersCells)
        {
            Arrays.sort(cornersCell, new Comparator<Cell>()
            {
                @Override
                public int compare(Cell c1, Cell c2)
                {
                    return c1.getCellIndexPerColor() - c2.getCellIndexPerColor();
                }
            });
        }
    }

    private static void addCellToArray(int row, int col, int ordinal, Cell cell, int FIRST_CONSTRAIN, int SECOND_CONSTRAIN, int THIRD_CONSTRAIN, int FOURTH_CONSTRAIN)
    {
        if (!((row == FIRST_CONSTRAIN && col == SECOND_CONSTRAIN) || 
               (row == THIRD_CONSTRAIN && col == FOURTH_CONSTRAIN)))
        {
            cell.addCellToArray(cornersCells[ordinal]);
        }
    }

    public Cell[] getTierCells(int level)
    {
        Cell[] cellsOfCorner = this.getCellsOfCorner();
        Cell[] res = null;
        switch (level)
        {
            case 1:
                res = new Cell[]
                {
                    cellsOfCorner[0]
                };
                break;
            case 2:
                res = new Cell[]
                {
                    cellsOfCorner[1], cellsOfCorner[2]
                };
                break;
            case 3:
                res = new Cell[]
                {
                    cellsOfCorner[3], cellsOfCorner[4], cellsOfCorner[5]
                };
                break;
            case 4:
                res = new Cell[]
                {
                    cellsOfCorner[6], cellsOfCorner[7], cellsOfCorner[8], cellsOfCorner[9]
                };
                break;
        }
        return res;
    }

    public Cell[] getCellsOfCorner()
    {
        return cornersCells[this.ordinal()];
    }

    public static Cell[] getCellsOfCorner(int cornerIndex)
    {
        return cornersCells[cornerIndex];
    }

    public static void connectCellToCorner(Cell cell)
    {
        int row = cell.getRow();
        int col = cell.getCol();

        if (row < QUARTER)
        {
            cell.addCellToArray(cornersCells[CornersEnum.UP.ordinal()]);
        }
        else if (col <= 6 && row >= QUARTER && row <= 7)
        {
            final int FIRST_CONSTRAIN = 6;
            final int SECOND_CONSTRAIN = 6;
            final int THIRD_CONSTRAIN = 7;
            final int FOURTH_CONSTRAIN = 5;
            addCellToArray(row, col, CornersEnum.LEFT_UP.ordinal(), cell, FIRST_CONSTRAIN, SECOND_CONSTRAIN, THIRD_CONSTRAIN, FOURTH_CONSTRAIN);
        }
        else if (col >= 18 && row >= QUARTER && row <= 7)
        {
            final int FIRST_CONSTRAIN = 6;
            final int SECOND_CONSTRAIN = 18;
            final int THIRD_CONSTRAIN = 7;
            final int FOURTH_CONSTRAIN = 19;
            addCellToArray(row, col, CornersEnum.RIGHT_UP.ordinal(), cell, FIRST_CONSTRAIN, SECOND_CONSTRAIN, THIRD_CONSTRAIN, FOURTH_CONSTRAIN);
        }
        else if (col <= 6 && row >= MIDDLE && row < THREE_QUARTERS)
        {
            final int FIRST_CONSTRAIN = 10;
            final int SECOND_CONSTRAIN = 6;
            final int THIRD_CONSTRAIN = MIDDLE;
            final int FOURTH_CONSTRAIN = 5;
            addCellToArray(row, col, CornersEnum.LEFT_DOWN.ordinal(), cell, FIRST_CONSTRAIN, SECOND_CONSTRAIN, THIRD_CONSTRAIN, FOURTH_CONSTRAIN);
        }
        else if (col >= 18 && row >= MIDDLE && row < THREE_QUARTERS)
        {
           final int FIRST_CONSTRAIN = 10;
            final int SECOND_CONSTRAIN = 18;
            final int THIRD_CONSTRAIN = MIDDLE;
            final int FOURTH_CONSTRAIN = 19;
            addCellToArray(row, col, CornersEnum.RIGHT_DOWN.ordinal(), cell, FIRST_CONSTRAIN, SECOND_CONSTRAIN, THIRD_CONSTRAIN, FOURTH_CONSTRAIN);
        }
        else if (row >= 13)
        {
            cell.addCellToArray(cornersCells[CornersEnum.DOWN.ordinal()]);
        }
    }

    public static boolean IsCellInCorner(CornersEnum corener, Cell cellToCheck)
    {
        boolean res = false;
        for (Cell cellInCorner : cornersCells[corener.ordinal()])
        {
            if (cellToCheck == cellInCorner)
            {
                res = true;
                break;
            }
        }
        return res;
    }

    public Cell getEdgeCell()
    {
        Cell res = null;
        return getCellsOfCorner()[0];
    }

    public static void initCornerCellsArray()
    {
        cornersCells = new Cell[6][10];
    }

    public int getTierOfCell(Cell cellToCheckItTier)
    {
        final int FIRST_TIER = 1;
        final int LAST_TIER = 4;

        int tierRes = 5; // any number larger than 4
        for (int tier = FIRST_TIER; tier <= LAST_TIER; tier++)
        {
            for (Cell cell : getTierCells(tier))
            {
                if (cell == cellToCheckItTier)
                {
                    tierRes = tier;
                    break;
                }
            }
        }
        return tierRes;
    }

    public static CornersEnum getCornerFromEdgeCell(Cell cell)
    {
        CornersEnum res = null;

        if (cell.getRow() == 0 && cell.getCol() == 12)
        {
            res = UP;
        }
        else if (cell.getRow() == 4 && cell.getCol() == 24)
        {
            res = RIGHT_UP;
        }
        else if (cell.getRow() == 12 && cell.getCol() == 24)
        {
            res = RIGHT_DOWN;
        }
        else if (cell.getRow() == 16 && cell.getCol() == 12)
        {
            res = DOWN;
        }
        else if (cell.getRow() == 12 && cell.getCol() == 0)
        {
            res = LEFT_DOWN;
        }
        else if (cell.getRow() == 4 && cell.getCol() == 0)
        {
            res = LEFT_UP;
        }
        return res;
    }
}
