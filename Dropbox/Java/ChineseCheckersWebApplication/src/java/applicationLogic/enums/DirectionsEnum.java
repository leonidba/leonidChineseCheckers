/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationLogic.enums;

import applicationLogic.Cell;

/**
 *
 * @author yaronla
 */
public enum DirectionsEnum
{

    RIGHT,
    RIGHT_UP,
    RIGHT_DOWN,
    LEFT,
    LEFT_DOWN,
    LEFT_UP;

    public static DirectionsEnum getName(int enumIndex)
    {
        DirectionsEnum retVal = DirectionsEnum.LEFT;
        switch (enumIndex)
        {
            case 0:
                retVal = DirectionsEnum.RIGHT;
                break;
            case 1:
                retVal = DirectionsEnum.RIGHT_UP;
                break;
            case 2:
                retVal = DirectionsEnum.RIGHT_DOWN;
                break;
            case 3:
                retVal = DirectionsEnum.LEFT;
                break;
            case 4:
                retVal = DirectionsEnum.LEFT_DOWN;
                break;
            case 5:
                retVal = DirectionsEnum.LEFT_UP;
                break;
        }
        return retVal;
    }

    public static int getInteger(DirectionsEnum dir)
    {
        int retVal = -1;
        switch (dir)
        {
            case LEFT:
                retVal = 3;
                break;
            case LEFT_DOWN:
                retVal = 4;
                break;
            case LEFT_UP:
                retVal = 5;
                break;
            case RIGHT:
                retVal = 0;
                break;
            case RIGHT_UP:
                retVal = 1;
                break;
            case RIGHT_DOWN:
                retVal = 2;
                break;
        }
        return retVal;
    }

    public DirectionsEnum getOppositeDirection()
    {
        DirectionsEnum retVal = null;
        switch (this)
        {
            case LEFT:
                retVal = DirectionsEnum.RIGHT;
                break;
            case LEFT_DOWN:
                retVal = DirectionsEnum.RIGHT_UP;
                break;
            case LEFT_UP:
                retVal = DirectionsEnum.RIGHT_DOWN;
                break;
            case RIGHT:
                retVal = DirectionsEnum.LEFT;
                break;
            case RIGHT_UP:
                retVal = DirectionsEnum.LEFT_DOWN;
                break;
            case RIGHT_DOWN:
                retVal = DirectionsEnum.LEFT_UP;
                break;
        }
        return retVal;
    }

    public static DirectionsEnum getDirection(Cell src, Cell dest)
    {
        DirectionsEnum res = null;
        if (src.getRow() == dest.getRow() && dest.getCol() > src.getCol())
        {
            res = RIGHT;
        }
        else if (src.getRow() == dest.getRow() && dest.getCol() < src.getCol())
        {
            res = LEFT;
        }
        else if (dest.getRow() > src.getRow() && dest.getCol() > src.getCol())
        {
            res = RIGHT_DOWN;
        }
        else if (dest.getRow() < src.getRow() && dest.getCol() < src.getCol())
        {
            res = LEFT_UP;
        }
        else if (dest.getRow() > src.getRow() && dest.getCol() < src.getCol())
        {
            res = LEFT_DOWN;
        }
        else if (dest.getRow() < src.getRow() && dest.getCol() > src.getCol())
        {
            res = RIGHT_UP;
        }
        return res;
    }
}
