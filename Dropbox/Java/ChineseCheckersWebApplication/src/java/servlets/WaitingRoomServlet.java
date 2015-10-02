/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import applicationLogic.GameManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * @author leon
 */
@WebServlet(name = "WaitingRoomServlet", urlPatterns =
{
    "/WaitingRoomServlet"
})
public class WaitingRoomServlet extends HttpServlet
{

    private static GameConfigurationServlet gameSettingsServlet;
    private static int numOfConnectedPlayers = 0;
    private static ArrayList<String> connectedNames = new ArrayList<>();

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
        String functionToRun = request.getParameter("funcName");
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter())
        {

            switch (functionToRun)
            {
                case "NumOfCurrentConnectedPlayers":
                    setNumOfCurrentConnectedPlayers(out);
                    break;
                case "numOfPlayersLeft":
                    setNumOfPlayersLeft(out);
                    break;
                case "PlayersNames":
                    getPlayersNames(out);
                    break;
                case "startGame":
                    out.print("");
                    if (AllPlayersHere())
                    {
                        out.print("game_screen.html");
                    }
                    break;
                case "checkName":
                    isLegalName(out, request.getParameter("nameToCheck"), request, response);
                    break;
            }
        }
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

    private void getPlayersNames(PrintWriter out)
    {
        out.println("<ul id=\"playersName\" type=\"circle\">");
        for (String name : connectedNames)
        {
            out.println("<li>" + name + "</li>");
        }
        out.println("</ul>");
    }

    private boolean AllPlayersHere()
    {
        if (WaitingRoomServlet.getGameSettingServlet() == null)
        {
            return false;
        }
        return WaitingRoomServlet.getNumOfConnectedPlayers() == WaitingRoomServlet.getGameSettingServlet().getNumOfHumanPlayers();
    }

    private void isLegalName(PrintWriter out, String nameToCheck, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String[] names = new String[connectedNames.size()];
        for (int i = 0; i < connectedNames.size(); i++)
        {
            names[i] = connectedNames.get(i);
        }
        if (GameManager.isUniqueNameAmongHumans(names, nameToCheck) == true)
        {
            addJoinerName(nameToCheck);
            incNumOfConnectedPlayers();
            SessionUtils.setUserName(nameToCheck, request);
            out.print("true");
        } else
        {
            out.print("false");
        }
    }

    public static ArrayList<String> getConnectedNames()
    {
        return connectedNames;
    }

    public static int getNumOfConnectedPlayers()
    {
        return numOfConnectedPlayers;
    }

    public static void incNumOfConnectedPlayers()
    {
        WaitingRoomServlet.numOfConnectedPlayers++;
    }

    public static void RemovePlayer(String playerNameToRemove)
    {
        connectedNames.remove(playerNameToRemove);
        WaitingRoomServlet.numOfConnectedPlayers = connectedNames.size();
        if (numOfConnectedPlayers == 0)
        {
            GameManagerServlet.iSFirstTimeLoad = true; // for next game load to have new settings
        }
    }

    public static void SetFirstPlayerSettings(GameConfigurationServlet gameSettings)
    {
        gameSettingsServlet = gameSettings;
    }

    public static void addJoinerName(String joinerName)
    {
        connectedNames.add(joinerName);
    }

    public static GameConfigurationServlet getGameSettingServlet()
    {
        return gameSettingsServlet;
    }

    private void setNumOfCurrentConnectedPlayers(PrintWriter out)
    {
        out.print(numOfConnectedPlayers);
    }

    private void setNumOfPlayersLeft(PrintWriter out)
    {
        out.print(gameSettingsServlet.getNumOfHumanPlayers() - numOfConnectedPlayers);
    }
}
