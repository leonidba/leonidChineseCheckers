/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationLogic.enums;

/**
 *
 * @author Yaron
 */
public enum PlayerTypeEnum
{

    Human,
    Computer;

    public static PlayerTypeEnum convertStringToPlayerTypeEnum(String type)
    {
        PlayerTypeEnum res = null;
        switch(type)
        {
            case "HUMAN":
                res = PlayerTypeEnum.Human;
                break;
            case "COMPUTER":
                res = PlayerTypeEnum.Computer;
                break;
        }
        return res;
    }
}
