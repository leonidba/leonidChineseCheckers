/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import servlets.GameConfigurationServlet;

/**
 *
 * @author leon
 */
@WebServlet(name = "StartOrJoinServlet", urlPatterns =
{
    "/StartOrJoin"
})
public class StartOrJoinServlet extends HttpServlet
{

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
        String tryToJoin = request.getParameter("join");
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter())
        {
            if (WaitingRoomServlet.getNumOfConnectedPlayers() == 0)
            {
                  out.println("<button type=\"submit\" id=\"startOrJoinButton\" onclick=\"StratGame()\"> Start New Game</button>");
//                out.println("<a href =\"configuration_screen.html\">");
//                out.println("<img class =\"img-responsive\" name=\"start\" src=\"resources/chinese-checkers.jpg\" alt=\"\">");
//                out.println("</a>");
//                out.println("<div class=\"intro-text\">");
//                out.println("<span class=\"name\" id=\"startGameLabel\" >Start Game</span>");
            }
            else
            {
                if (canJoin())
                {
                    out.println("<button type=\"submit\" id=\"startOrJoinButton\" onclick=\"JoinGame()\"> Join Game</button>");
                }
                else
                {
                    out.println("<button type=\"submit\" id=\"startOrJoinButton\" onclick=\"CantJoinGame()\"> Join Game</button>");
                }
//                out.println("<img class =\"img-responsive\"  name=\"join\" src=\"resources/chinese-checkers.jpg\" alt=\"\">");
//                out.println("</a>");
//                out.println("<div class=\"intro-text\">");
//                out.println("<span class=\"name\" id=\"startGameLabel\">Join Game</span>");
            }
//            out.println("<hr class=\"star-light\">");
//            out.println("</div>");
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

    private boolean canJoin()
    {
        if (WaitingRoomServlet.getGameSettingServlet() == null)
        {
            return false;
        }
        return WaitingRoomServlet.getNumOfConnectedPlayers() < WaitingRoomServlet.getGameSettingServlet().getNumOfHumanPlayers();
    }

}
