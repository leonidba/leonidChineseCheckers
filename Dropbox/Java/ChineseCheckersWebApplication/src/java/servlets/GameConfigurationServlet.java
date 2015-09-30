/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author leon
 */
@WebServlet(name = "NewServlet", urlPatterns =
{
    "/create_game_screen"
})
public class GameConfigurationServlet extends HttpServlet
{

    private String firstPlayerName;
    private int numOfTotalPlayers;
    private int numOfColorsPerPlayer;
    private int numOfHumanPlayers;
    private static boolean isFirstPlayer = true;

    public static boolean isIsFirstPlayer()
    {
        return isFirstPlayer;
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        isFirstPlayer = false; // To prevent next players from also being here
        response.setContentType("text/html;charset=UTF-8");
        firstPlayerName = request.getParameter("player_name");
        String isNotFirstPlayer = request.getParameter("join_game_button");

        if (isNotFirstPlayer == null) // means is first player:
        {
            numOfTotalPlayers = Integer.parseInt(request.getParameter("numOfPlayers"));
            numOfHumanPlayers = Integer.parseInt(request.getParameter("numOfHumanPlayers"));

            //shared data
            this.getServletConfig().getServletContext().setAttribute("numOfPlayers", request.getParameter("numOfPlayers"));
            this.getServletConfig().getServletContext().setAttribute("numOfHumanPlayers", request.getParameter("numOfHumanPlayers"));
            this.getServletConfig().getServletContext().setAttribute("numOfColorsPerPlayer", request.getParameter("numOfColos"));

            WaitingRoomServlet.SetFirstPlayerSettings(this);
            WaitingRoomServlet.incNumOfConnectedPlayers();
            WaitingRoomServlet.addJoinerName(firstPlayerName);
            SessionUtils.setUserName(firstPlayerName, request);
        }
        else
        {
            WaitingRoomServlet.incNumOfConnectedPlayers();
            WaitingRoomServlet.addJoinerName(firstPlayerName);
        }
        response.sendRedirect("waiting_room_screen.html");
//        RequestDispatcher rd = request.getRequestDispatcher("waiting_room_screen.html"); TODO need?
//        rd.forward(request, response);
    }

    public String getFirstPlayerName()
    {
        return firstPlayerName;
    }

    public int getNumOfTotalPlayers()
    {
        return numOfTotalPlayers;
    }

    public int getNumOfColorsPerPlayer()
    {
        return numOfColorsPerPlayer;
    }

    public int getNumOfHumanPlayers()
    {
        return numOfHumanPlayers;
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo()
    {
        return "Short description";
    }// </editor-fold>

}
