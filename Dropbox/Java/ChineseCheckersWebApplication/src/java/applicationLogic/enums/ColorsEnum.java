/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationLogic.enums;

import java.util.ArrayList;

/**
 *
 * @author leon
 */
public enum ColorsEnum
{
    Black,
    Red,
    Green,
    Blue,
    Yellow,
    White,
    Empty;

    private static ArrayList<ColorsEnum> availableColors;
    private CornersEnum targetCorner;

    public CornersEnum getTargetCorner()
    {
        return this.targetCorner;
    }

    public void setTargetCorner2(CornersEnum targetCorner)
    {
        this.targetCorner = targetCorner;
    }
    
    public void setTargetCorner(CornersEnum srcCorner)
    {
        switch (srcCorner)
        {
            case DOWN:
                targetCorner = CornersEnum.UP;
                break;
            case UP:
                targetCorner = CornersEnum.DOWN;
                break;
            case LEFT_DOWN:
                targetCorner = CornersEnum.RIGHT_UP;
                break;
            case LEFT_UP:
                targetCorner = CornersEnum.RIGHT_DOWN;
                break;
            case RIGHT_DOWN:
                targetCorner = CornersEnum.LEFT_UP;
                break;
            case RIGHT_UP:
                targetCorner = CornersEnum.LEFT_DOWN;
                break;
        }
    }

    static
    {
        initAvailableColors();
    }

    public static ColorsEnum[] assignAvailableColors(int amount)
    {
        ColorsEnum[] poped = new ColorsEnum[amount];
        for (int i = 0; i < amount; i++)
        {
            poped[i] = availableColors.remove(0);
        }
        return poped;
    }

    public static void initAvailableColors()
    {
        availableColors = new ArrayList<>();
        for (ColorsEnum color : ColorsEnum.values())
        {
            if (color != ColorsEnum.Empty)
            {
                availableColors.add(color);
            }
        }
    }

    public static ColorsEnum convertStringToColorsEnum(String color)
    {
        ColorsEnum res = null;
        switch (color)
        {
            case "BLACK":
                res = ColorsEnum.Black;
                break;
            case "BLUE":
                res = ColorsEnum.Blue;
                break;
            case "GREEN":
                res = ColorsEnum.Green;
                break;
            case "RED":
                res = ColorsEnum.Red;
                break;
            case "WHITE":
                res = ColorsEnum.White;
                break;
            case "YELLOW":
                res = ColorsEnum.Yellow;
                break;
        }
        return res;
    }
}
