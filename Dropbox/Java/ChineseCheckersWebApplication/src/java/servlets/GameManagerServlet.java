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
import applicationLogic.*;
import applicationLogic.enums.ColorsEnum;
import applicationLogic.enums.CornersEnum;
import applicationLogic.enums.DirectionsEnum;
import applicationLogic.enums.PlayerTypeEnum;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author leon
 */
@WebServlet(name = "GameManagerServlet", urlPatterns =
{
    "/GameManagerServlet"
})
public class GameManagerServlet extends HttpServlet
{

    public static boolean iSFirstTimeLoad = true;
    private GameManager model;
    private Boolean isJumped;
    private Cell cellToMove;
    private ArrayList<Cell> avaliableMovesAfterFirstJump;
    private DirectionsEnum lastMoveDirection;

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
        response.setContentType("application/json");
        String functionToRun = request.getParameter("funcName");

        try (PrintWriter out = response.getWriter())
        {
            switch (functionToRun)
            {
                case "getCellColor":
                   // getCellColor(request, out); erease
                    break;
                case "setPlayers":
                    if (iSFirstTimeLoad == true)
                    {
                        createPlayersInLogic();
                        iSFirstTimeLoad = false;
                    }
                    getBroserPlayer(request, out); // save player json in each client
                    break;
                case "onButtonClick":
                    if (isThisPlayerTurn(request) == true)
                    {
                        onButtonClick(request, out);
                    }
                    break;
                case "computerMove":
                    makeComputerTurn();
                    break;
                case "getBoard":
                    getBoard(out);
                    break;
                case "quitGame":
                    quitFromGame(request);
                    break;
                case "removeFromConnectedPlayers":
                    removePlayerOnClosingBrowser(request);
                    break;
                case "getCurrentPlayer":
                    out.print(new Gson().toJson(model.getCurrentPlayer()));
                    break;
                case "endTurn":
                    handleEndTurn();
                    break;
                case "getPlayers":
                    getPlayers(request, out);
                    break;
                case "isGameOver":
                    out.print(new Gson().toJson(model.isGameOver()));
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

    //return color Json or null if there is no cell
    private void getCellColor(HttpServletRequest request, PrintWriter out)
            throws ServletException, IOException
    {
        int row = (int) Integer.parseInt(request.getParameter("row"));
        int col = (int) Integer.parseInt(request.getParameter("col"));

        Cell cell = model.getBoard().getBoard()[row][col];

        Gson gson = new Gson();
        String jsonResponse;

        if (cell == null)
        {
            jsonResponse = gson.toJson(null);
        } else
        {
            jsonResponse = gson.toJson(cell.getColor());
        }
        out.print(jsonResponse);
        out.flush();
    }

    private void createPlayersInLogic()
    {
        String numOfTotalPlayers = this.getServletConfig().getServletContext().getAttribute("numOfPlayers").toString();
        String numOfColorsPerPlayer = this.getServletConfig().getServletContext().getAttribute("numOfColorsPerPlayer").toString();
        String[] humanNamesArr = new String[WaitingRoomServlet.getConnectedNames().size()];
        for (int i = 0; i < WaitingRoomServlet.getConnectedNames().size(); i++)
        {
            humanNamesArr[i] = WaitingRoomServlet.getConnectedNames().get(i);
        }
        ColorsEnum.initAvailableColors();
        CornersEnum.initCornerCellsArray();
        model = new GameManager();
        TimerHandler.SetModel(model, this);
        avaliableMovesAfterFirstJump = new ArrayList();
        isJumped = false;
        model.createPlayers(Integer.parseInt(numOfTotalPlayers), Integer.parseInt(numOfColorsPerPlayer), humanNamesArr);
        model.setPlayersCorners();
        TimerHandler.StartTimer();
    }

    //Button Clicked
    private void onButtonClick(HttpServletRequest request, PrintWriter out)
    {
        if (model.getCurrentPlayer().getPlayerType() == PlayerTypeEnum.Human && !model.isGameOver())
        {
            int row = (int) Integer.parseInt(request.getParameter("row"));
            int col = (int) Integer.parseInt(request.getParameter("col"));
            Cell chosenCellToMove = model.getBoard().getBoard()[row][col];

            if (isPlayerChoseAfterJumpOtherCell(chosenCellToMove))
            {
                return;
            }

            if (isPressedOnLegalCellToMove(chosenCellToMove))
            {
                TimerHandler.StartTimer();
                handleSelectCellToMove(chosenCellToMove, lastMoveDirection, out);
            } else if (isDestButton(request))
            {
                handleSelectDestCell(chosenCellToMove, out);
            }
        }
    }

    private boolean isPlayerChoseAfterJumpOtherCell(Cell i_cellToMove)
    {
        return cellToMove != null && isJumped == true && avaliableMovesAfterFirstJump.contains(i_cellToMove) == false;
    }

    private boolean isPressedOnLegalCellToMove(Cell i_CellToMove)
    {
        return model.getCurrentPlayer().getPlayerCells().contains(i_CellToMove)
                && Arrays.asList(model.getCurrentPlayer().getColorsArray()).contains(i_CellToMove.getColor());
    }

    private boolean isDestButton(HttpServletRequest request)
    {
        String buttonClass = request.getParameter("buttonClass");
        return buttonClass.equals("legalMove");
    }

    private boolean isThisPlayerTurn(HttpServletRequest request)
    {
        String userNameFromSession = SessionUtils.getUsername(request);
        String logicCorrentPlayerName = model.getCurrentPlayer().getName();

        return logicCorrentPlayerName.equals(userNameFromSession);
    }

    public void makeComputerTurn()
    {
        while (model.getCurrentPlayer().getPlayerType() == PlayerTypeEnum.Computer && !model.isGameOver()) // to support few computers playing 
        {
            model.computerAImovement();
            if (model.isGameOver())
            {
                //gameOver(); TODO
            } else
            {
                model.switchTurnToNextPlayer();
            }
        }
    }

    private void getBoard(PrintWriter out)
    {
        Gson gson = new Gson();
        String jsonResponse = gson.toJson(model.getBoard().getBoard());
        out.print(jsonResponse);
        out.flush();
    }

    private void quitFromGame(HttpServletRequest request)
    {
        String quitterUserName = SessionUtils.getUsername(request);
        Player PlayerToRemove = null;
        for (Player player : model.getPlayerArray()) // find the Player by name
        {
            if (player.getName().equals(quitterUserName))
            {
                PlayerToRemove = player;
                break;
            }
        }

        if (PlayerToRemove != null)
        {
            SessionUtils.clearSession(request);
            WaitingRoomServlet.RemovePlayer(quitterUserName);
            model.removePlayerFromGame(PlayerToRemove);
        }
    }

    private void removePlayerOnClosingBrowser(HttpServletRequest request)
    {
        String fromScreen = request.getParameter("fromScreen");
        String toScreen = request.getParameter("toScreen");
        if (fromScreen.equalsIgnoreCase("game_screen"))
        {
            quitFromGame(request);
        } else if (isMovingToLegalScreen(fromScreen, toScreen) == false)
        {
            String LeavingPlayer = SessionUtils.getUsername(request);
            SessionUtils.clearSession(request);
            WaitingRoomServlet.RemovePlayer(LeavingPlayer);
        }
    }

    private boolean isMovingToLegalScreen(String fromScreen, String toScreen)
    {
        boolean res = false;
        if (fromScreen.equals("waiting_room") && toScreen.equals("game_screen"))
        {
            res = true;
        } else if (fromScreen.equals("joiner_screen") && toScreen.equals("waiting_room"))
        {
            res = true;
        }
        return res;
    }

    private void getPlayers(HttpServletRequest request, PrintWriter out)
    {
        Gson gson = new Gson();
        String logicCurrentPlayerJson = gson.toJson(model.getCurrentPlayer());
        String PlayersArrayJson = gson.toJson(model.getPlayerArray());
        String outJson = "[" + logicCurrentPlayerJson + "," + PlayersArrayJson + "]";
        out.print(outJson);
        out.flush();
    }

    private void getBroserPlayer(HttpServletRequest request, PrintWriter out)
    {
        String BrowserPlayerName = SessionUtils.getUsername(request);
        Player BrowserPlayerToReturn = null;
        for (Player player : model.getPlayerArray()) // find the Player by name
        {
            if (player.getName().equals(BrowserPlayerName))
            {
                BrowserPlayerToReturn = player;
                break;
            }
        }
        String outJson = new Gson().toJson(BrowserPlayerToReturn);
        out.print(outJson);
        out.flush();
    }

    private void handleEndTurn()
    {
        model.switchTurnToNextPlayer();
        avaliableMovesAfterFirstJump.clear();
        isJumped = false;
    }

    class rowAndCol
    {

        private int row;
        private int col;
        private String color;

        public rowAndCol(int i_row, int i_col)
        {
            row = i_row;
            col = i_col;
        }

        public void SetColor(String colorToSet)
        {
            color = colorToSet;
        }
    }

    private void handleSelectCellToMove(Cell i_CellToMove, DirectionsEnum lastMovedirection, PrintWriter out)
    {
        ArrayList<rowAndCol> buttonsToPaint = new ArrayList<>();

        this.cellToMove = i_CellToMove;
        for (DirectionsEnum legalDirection : model.getLegalMoves(cellToMove, isJumped, lastMovedirection))
        {
            Cell destCell = model.getBoard().convertDirectionToCell(cellToMove, legalDirection);
            if (destCell.getColor() != ColorsEnum.Empty)
            {
                destCell = model.getBoard().convertDirectionToCell(destCell, legalDirection);
            }
            buttonsToPaint.add(new rowAndCol(destCell.getRow(), destCell.getCol())); // the client will paint it as "Legal move"

            if (isJumped == true)
            {
                avaliableMovesAfterFirstJump.add(destCell);
            }
        }

        Gson gson = new Gson();
        String jsonResponse;
        jsonResponse = gson.toJson(buttonsToPaint);
        String respondType = gson.toJson("legalMoves");
        String bothJson = "[" + respondType + "," + jsonResponse + "]"; //Put both objects in an array of 2 elements
        out.print(bothJson);
        out.flush();

        if (isFinishToJump(lastMovedirection))
        {
            finishJumps();
        }
    }

    private boolean isFinishToJump(DirectionsEnum lastMovedirection)
    {
        return isJumped == true && model.getLegalMoves(cellToMove, isJumped, lastMovedirection).isEmpty();
    }

    private void finishJumps()
    {
        model.switchTurnToNextPlayer();
        avaliableMovesAfterFirstJump.clear();
        isJumped = false;
    }

    private void handleSelectDestCell(Cell destCell, PrintWriter out)
    {
        lastMoveDirection = DirectionsEnum.getDirection(cellToMove, destCell);

        isJumped = model.moveToLegalCell(cellToMove, lastMoveDirection);
        if (isJumped) // to make sure next choose will be from that list of cells
        {
            avaliableMovesAfterFirstJump.add(destCell);
        }

        Gson gson = new Gson();
        String respondType = gson.toJson("handleSelectDestCell");
        String jsonResponseSrc = gson.toJson(new rowAndCol(cellToMove.getRow(), cellToMove.getCol()));
        rowAndCol dest = new rowAndCol(destCell.getRow(), destCell.getCol());
        dest.SetColor(destCell.getColor().toString());
        String jsonResponseSrcDest = gson.toJson(dest);
        String isJumpedJson = gson.toJson(isJumped);
        String bothJson = "[" + respondType + "," + jsonResponseSrc + "," + jsonResponseSrcDest + "," + isJumpedJson + "]"; //Put both objects and "isJamped" in an array of 3 elements
        out.print(bothJson);
        out.flush();

        if (model.isGameOver())
        {
            //gameOver(); TODO
        } else
        {
            if (!isJumped)
            {
                model.switchTurnToNextPlayer();
                lastMoveDirection = null; // must to initialize for next other player move
                //setCurrentPlayerLabel(); TODO
            }
        }
    }
}
