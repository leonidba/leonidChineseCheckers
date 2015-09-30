/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import applicationLogic.GameManager;
import applicationLogic.Player;
import applicationLogic.enums.PlayerTypeEnum;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Yaron
 */
public final class TimerHandler
{

    private static final int TIME_COUNTER = 30000; // Seconds TODO regular time
    private static GameManager model;
    private static GameManagerServlet servlet;
    private static Timer timer = new Timer();
    private static KickPlayerTask kickPlayerTask;

    private TimerHandler()
    {
    }

    public static void SetModel(GameManager Model, GameManagerServlet Servlet)
    {
        model = Model;
        servlet = Servlet;
    }

    private static class KickPlayerTask extends TimerTask
    {

        Player currentPlayer = model.getCurrentPlayer();

        @Override
        public void run()
        {
            WaitingRoomServlet.RemovePlayer(currentPlayer.getName());
            model.removePlayerFromGame(currentPlayer);
            if (model.getCurrentPlayer().getPlayerType() == PlayerTypeEnum.Computer)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        servlet.makeComputerTurn();
                    }
                }).start();
            }
        }
    }

    public static void StartTimer()
    {
        StopTimer();
        kickPlayerTask = new KickPlayerTask();
        timer = new Timer();
        if (model.getCurrentPlayer().getPlayerType() == PlayerTypeEnum.Human)
        {
            timer.schedule(kickPlayerTask, TIME_COUNTER * 1000);
        }
    }

    public static void StopTimer()
    {
        timer.cancel();
        timer.purge();
    }
}
