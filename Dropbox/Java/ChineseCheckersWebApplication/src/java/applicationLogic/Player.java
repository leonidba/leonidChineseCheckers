/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationLogic;

import applicationLogic.enums.ColorsEnum;
import applicationLogic.enums.PlayerTypeEnum;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author leon
 */
public class Player
{
    static final int NUM_OF_STONES_PER_PLAYER = 10;
    private final String name;
    private final ColorsEnum[] color;
    private final PlayerTypeEnum playerType;
    private int numOfStonesLeft;
    private final ArrayList<Cell> playerCells;

    public Player(String name, ColorsEnum[] colors, PlayerTypeEnum playerType)
    {
        this.name = name;
        this.color = colors;
        this.playerType = playerType;
        playerCells = new ArrayList<>();
        numOfStonesLeft = NUM_OF_STONES_PER_PLAYER * colors.length;
    }

    public void changeCellPointerAtArrayList(Cell currCell, Cell newCell)
    {
        int indexOfCellToChange = playerCells.indexOf(currCell);
        playerCells.set(indexOfCellToChange, newCell);
        manageTargetMovement(currCell, newCell);
    }

    private void manageTargetMovement(Cell srcCell, Cell destCell)
    {
        ColorsEnum movementColor = destCell.getColor();
        if (!srcCell.IsInTargetCorner(movementColor) && destCell.IsInTargetCorner(movementColor)) //entering target
        {
            numOfStonesLeft--;
        }
        else if (srcCell.IsInTargetCorner(movementColor) && !destCell.IsInTargetCorner(movementColor)) // leaving target
        {
            numOfStonesLeft++;
        }
    }

    public void decreaseNumOfStonesLeft()
    {
        this.numOfStonesLeft--;
    }

    public ArrayList<Cell> getPlayerCells()
    {
        return playerCells;
    }

    public ArrayList<Cell> getPlayerCellsWithColor(ColorsEnum color)
    {
        ArrayList<Cell> playersCells = getPlayerCells();
        ArrayList<Cell> fillteredCells = new ArrayList<>();
        for (Cell cell : playersCells)
        {
            if (cell.getColor() == color)
            {
                fillteredCells.add(cell);
            }
        }
        return fillteredCells;
    }

    public ColorsEnum[] getColorsArray()
    {
        return color;
    }

    public void addPlayerStartCells(Cell[] cellsArr)
    {
        playerCells.addAll(Arrays.asList(cellsArr));
    }

    public String getName()
    {
        return this.name;
    }

    public int getOffsetOfFirstCellOfPlayerWithColor(ColorsEnum color)
    {
        int offset = -1;
        for (int i = 0; i < playerCells.size(); i++)
        {
            if (playerCells.get(i).getColor() == color)
            {
                offset = i;
                break;
            }
        }
        return offset;
    }

    public boolean hasWonTheGame()
    {
        return numOfStonesLeft == 0;
    }

    public PlayerTypeEnum getPlayerType()
    {
        return this.playerType;
    }
}
