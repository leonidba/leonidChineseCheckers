/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import applicationLogic.enums.CornersEnum;
import applicationLogic.enums.DirectionsEnum;

/**
 *
 * @author leon
 */
public class Board
{

    final int TOP = 0;
    final int QUARTER = 4;
    final int MIDDLE = 9;
    final int THREE_QUARTERS = 13;
    final int BUTTOM = 17;

    private final Cell[][] board;
    private final ArrayList<Cell>[] cellsByRow = new ArrayList[17];
    public final int ROWS = 17;
    public final int COLS = 25;

    public Board()
    {
        board = new Cell[ROWS][COLS];
        initCellsByRowArray();

        // make all cells null
        for (Cell[] cell : board)
        {
            cell = null;
        }

        int root = 12;  // the middle column
        int maxJump = 1; // the jump from middle to left and right sides

        // create the relevant cells for checkers game
        for (int row = TOP; row < QUARTER; row++)
        {
            for (int col = 0; col < maxJump; col++)
            {
                createCellsInBoard(row, col);
            }
            maxJump++;
        }

        // Second part of construction
        for (int row = QUARTER; row < MIDDLE; row++)
        {
            for (int col = root; col >= 0; col--)
            {
                createCellsInBoard(row, col);
            }
            root--;
        }

        // Third part of construction
        root++;
        for (int row = MIDDLE; row < THREE_QUARTERS; row++)
        {
            root++;
            for (int col = root; col >= 0; col--)
            {
                createCellsInBoard(row, col);
            }
        }

        // Last part
        for (int row = THREE_QUARTERS; row < BUTTOM; row++)
        {
            maxJump--;
            for (int col = maxJump; col >= 0; col--)
            {
                createCellsInBoard(row, col);
            }
        }
        sortcellsByRowArray();
        
        //I need it for my smart AI
        reOrganizeCellsIndexs();
        CornersEnum.SortAllCornersByIndex();
    }

    private void createCellsInBoard(int row, int col)
    {
        int root = 12;
        if ((row % 2 != 0 && col % 2 == 0) || (row % 2 == 0 && col % 2 != 0))
        {
            return;
        }
        board[row][root + col] = new Cell(row, root + col);
        cellsByRow[row].add(board[row][root + col]);
        CornersEnum.connectCellToCorner(board[row][root + col]);
        if (col != 0)
        {
            board[row][root - col] = new Cell(row, root - col);
            cellsByRow[row].add(board[row][root - col]);
            CornersEnum.connectCellToCorner(board[row][root - col]);
        }
    }

    public Cell convertDirectionToCell(Cell fromCell, DirectionsEnum dir)
    {
        Cell destCell = null;
        
        try
        {
            switch (dir)
            {
                case LEFT_DOWN:
                    destCell = board[fromCell.getRow() + 1][fromCell.getCol() - 1];
                    break;
                case LEFT_UP:
                    destCell = board[fromCell.getRow() - 1][fromCell.getCol() - 1];
                    break;
                case RIGHT_DOWN:
                    destCell = board[fromCell.getRow() + 1][fromCell.getCol() + 1];
                    break;
                case RIGHT_UP:
                    destCell = board[fromCell.getRow() - 1][fromCell.getCol() + 1];
                    break;
                case LEFT:
                    destCell = board[fromCell.getRow()][fromCell.getCol() - 2];
                    break;
                case RIGHT:
                    destCell = board[fromCell.getRow()][fromCell.getCol() + 2];
                    break;
            }
        }
        finally
        {
            return destCell;
        }
    }

    public Cell[][] getBoard()
    {
        return board;
    }

    public int getRows()
    {
        return ROWS;
    }

    public int getCols()
    {
        return COLS;
    }

    private void sortcellsByRowArray()
    {
        for (int i = 0; i < 17; i++)
        {
            Collections.sort(cellsByRow[i], new Comparator<Cell>()
            {
                @Override
                public int compare(Cell c1, Cell c2)
                {
                    return c1.compareTo(c2);
                }
            });
        }
    }

    private void initCellsByRowArray()
    {
        for (int i = 0; i < 17; i++)
        {
            cellsByRow[i] = new ArrayList<>();
        }
    }

    public ArrayList<Cell>[] getCellsByRow()
    {
        return cellsByRow;
    }

    private void reOrganizeCellsIndexs()
    {
        //TOP
        cellsByRow[0].get(0).setCellIndexPerColor(0);
        cellsByRow[1].get(0).setCellIndexPerColor(1);
        cellsByRow[1].get(1).setCellIndexPerColor(2);
        cellsByRow[2].get(0).setCellIndexPerColor(3);
        cellsByRow[2].get(1).setCellIndexPerColor(4);
        cellsByRow[2].get(2).setCellIndexPerColor(5);
        cellsByRow[3].get(0).setCellIndexPerColor(6);
        cellsByRow[3].get(1).setCellIndexPerColor(7);
        cellsByRow[3].get(2).setCellIndexPerColor(8);
        cellsByRow[3].get(3).setCellIndexPerColor(9);

        //LEFT_UP
        cellsByRow[4].get(0).setCellIndexPerColor(0);
        cellsByRow[4].get(1).setCellIndexPerColor(1);
        cellsByRow[4].get(2).setCellIndexPerColor(3);
        cellsByRow[4].get(3).setCellIndexPerColor(6);
        cellsByRow[5].get(0).setCellIndexPerColor(2);
        cellsByRow[5].get(1).setCellIndexPerColor(4);
        cellsByRow[5].get(2).setCellIndexPerColor(7);
        cellsByRow[6].get(0).setCellIndexPerColor(5);
        cellsByRow[6].get(1).setCellIndexPerColor(8);
        cellsByRow[7].get(0).setCellIndexPerColor(9);

        //RIGHT_DOWN
        cellsByRow[12].get(12).setCellIndexPerColor(0);
        cellsByRow[12].get(11).setCellIndexPerColor(1);
        cellsByRow[12].get(10).setCellIndexPerColor(3);
        cellsByRow[12].get(9).setCellIndexPerColor(6);
        cellsByRow[11].get(11).setCellIndexPerColor(2);
        cellsByRow[11].get(10).setCellIndexPerColor(4);
        cellsByRow[11].get(9).setCellIndexPerColor(7);
        cellsByRow[10].get(10).setCellIndexPerColor(5);
        cellsByRow[10].get(9).setCellIndexPerColor(8);
        cellsByRow[9].get(9).setCellIndexPerColor(9);

        //DOWN
        cellsByRow[16].get(0).setCellIndexPerColor(0);
        cellsByRow[15].get(0).setCellIndexPerColor(1);
        cellsByRow[15].get(1).setCellIndexPerColor(2);
        cellsByRow[14].get(0).setCellIndexPerColor(3);
        cellsByRow[14].get(1).setCellIndexPerColor(4);
        cellsByRow[14].get(2).setCellIndexPerColor(5);
        cellsByRow[13].get(0).setCellIndexPerColor(6);
        cellsByRow[13].get(1).setCellIndexPerColor(7);
        cellsByRow[13].get(2).setCellIndexPerColor(8);
        cellsByRow[13].get(3).setCellIndexPerColor(9);
        
        //LEFT_DOWN
        cellsByRow[12].get(0).setCellIndexPerColor(0);
        cellsByRow[12].get(1).setCellIndexPerColor(1);
        cellsByRow[12].get(2).setCellIndexPerColor(3);
        cellsByRow[12].get(3).setCellIndexPerColor(6);
        cellsByRow[11].get(0).setCellIndexPerColor(2);
        cellsByRow[11].get(1).setCellIndexPerColor(4);
        cellsByRow[11].get(2).setCellIndexPerColor(7);
        cellsByRow[10].get(0).setCellIndexPerColor(5);
        cellsByRow[10].get(1).setCellIndexPerColor(8);
        cellsByRow[9].get(0).setCellIndexPerColor(9);
        
        //RIGHT_UP
        cellsByRow[4].get(12).setCellIndexPerColor(0);
        cellsByRow[4].get(11).setCellIndexPerColor(1);
        cellsByRow[4].get(10).setCellIndexPerColor(3);
        cellsByRow[4].get(9).setCellIndexPerColor(6);
        cellsByRow[5].get(11).setCellIndexPerColor(2);
        cellsByRow[5].get(10).setCellIndexPerColor(4);
        cellsByRow[5].get(9).setCellIndexPerColor(7);
        cellsByRow[6].get(10).setCellIndexPerColor(5);
        cellsByRow[6].get(9).setCellIndexPerColor(8);
        cellsByRow[7].get(9).setCellIndexPerColor(9);
    }
}
