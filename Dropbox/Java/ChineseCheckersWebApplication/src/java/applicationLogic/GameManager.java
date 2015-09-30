/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package applicationLogic;

import fileHandleUtil.XMLHandle;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import applicationLogic.enums.ColorsEnum;
import applicationLogic.enums.DirectionsEnum;
import applicationLogic.enums.PlayerTypeEnum;
import java.util.ArrayList;
import java.util.Random;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import applicationLogic.enums.CornersEnum;
import java.util.stream.Collectors;
import org.xml.sax.SAXException;
import servlets.TimerHandler;

/**
 *
 * @author leon
 */
public class GameManager
{

    private Board board;
    private ArrayList<Player> playerArray;
    private Player currentPlayer;
    private int currentPlayerIndex = 0;
    private static final int COMPUTER_DELAY = 600;
    private static boolean AFTER_LOAD;

    public GameManager()
    {
        initialize();
    }

    public void initialize()
    {
        board = new Board();
        playerArray = new ArrayList<>();
    }

    public boolean isGameOver()
    {
        boolean res;
        if (playerArray.size() == 1)
        {
            switchTurnToNextPlayer();
            res = true; // Only 1 player left
        } else // Check if current player reach the game goal
        {
            res = currentPlayer.hasWonTheGame();
        }
        return res;
    }

    public void removePlayerFromGame(Player playerToRemove)
    {
        for (Cell cellOfPlayer : playerToRemove.getPlayerCells())
        {
            cellOfPlayer.ClearCell();
        }
        playerArray.remove(playerToRemove);
        currentPlayerIndex--;
        if (playerToRemove == getCurrentPlayer())
        {
            switchTurnToNextPlayer();
        }
    }

    public ArrayList<DirectionsEnum> getLegalMoves(Cell fromCell, boolean onlyJumps, DirectionsEnum lastMoveDirection)
    {
        ArrayList<DirectionsEnum> res = new ArrayList<>();
        Cell destCell;
        Cell destJumpCell;

        for (DirectionsEnum dir : DirectionsEnum.values())
        {
            destCell = board.convertDirectionToCell(fromCell, dir);
            if (destCell != null && (lastMoveDirection == null || dir != lastMoveDirection.getOppositeDirection()))
            {
                if (destCell.getColor() == ColorsEnum.Empty)
                {
                    if (!onlyJumps)
                    {
                        res.add(dir);
                    }
                } else
                {
                    destJumpCell = board.convertDirectionToCell(destCell, dir);
                    if (destJumpCell != null && destJumpCell.IsEmpty())
                    {
                        res.add(dir);
                    }
                }
            }
        }
        return res;
    }

    public boolean hasLegalMoves(Cell fromCell, boolean onlyJumps, DirectionsEnum lastMoveDirection)
    {
        return getLegalMoves(fromCell, onlyJumps, lastMoveDirection).size() > 0;
    }

    public void switchTurnToNextPlayer()
    {
        currentPlayerIndex++;
        if (currentPlayerIndex == playerArray.size())
        {
            currentPlayerIndex = 0;
        }
        currentPlayer = playerArray.get((currentPlayerIndex));
        TimerHandler.StartTimer();
    }

    public boolean moveToLegalCell(Cell srcCell, DirectionsEnum choosenDirection)
    {
        boolean didJumped = false;
        Cell destCell = board.convertDirectionToCell(srcCell, choosenDirection);
        if (!destCell.IsEmpty())
        {
            destCell = getBoard().convertDirectionToCell(destCell, choosenDirection);
            didJumped = true;
        }
        srcCell.moveToDestCell(destCell);
        currentPlayer.changeCellPointerAtArrayList(srcCell, destCell);
        return didJumped;
    }

    public Board getBoard()
    {
        return board;
    }

    public void createPlayers(int numOfTotalPlayers, int numOfColorsPerPlayer, String[] humanNamesArr)
    {
        for (int i = 0; i < humanNamesArr.length; i++)
        {
            playerArray.add(new Player(humanNamesArr[i], ColorsEnum.assignAvailableColors(numOfColorsPerPlayer), PlayerTypeEnum.Human));
        }

        for (int i = 1; i <= numOfTotalPlayers - humanNamesArr.length; i++)
        {
            playerArray.add(new Player("Computer" + i, ColorsEnum.assignAvailableColors(numOfColorsPerPlayer), PlayerTypeEnum.Computer));
        }
        currentPlayer = playerArray.get(currentPlayerIndex);
    }

    public void setPlayersCorners()
    {
        int numOfPlayer = playerArray.size();
        int colorsPerPlayer = playerArray.get(0).getColorsArray().length;

        //Place first player in the Upper corner
        placePlayerInCorners(playerArray.get(0), CornersEnum.UP);
        if (numOfPlayer == 2)
        {
            placePlayerInCorners(playerArray.get(1), CornersEnum.DOWN);
        } else if (numOfPlayer == 3)
        {
            subFunctionSetPlayersCorners(colorsPerPlayer);
        } else if (numOfPlayer == 4)
        {
            subFunctionSetPlayersCorners();
        } else if (numOfPlayer >= 5)
        {
            subFunctionSetPlayersCorners2();
            if (numOfPlayer == 6)
            {
                placePlayerInCorners(playerArray.get(5), CornersEnum.LEFT_UP);
            }
        }
    }

    private void placePlayerInCorners(Player player, CornersEnum corner)
    {
        for (int offsetOfCorners = 0; offsetOfCorners < player.getColorsArray().length; offsetOfCorners++)
        {
            if (AFTER_LOAD == false)
            {
                player.addPlayerStartCells(CornersEnum.getCellsOfCorner(corner.ordinal() + offsetOfCorners));
            }
            placeColorInSpecificCorner(corner.ordinal() + offsetOfCorners, player.getColorsArray()[offsetOfCorners]);
        }
    }

    private void placeColorInSpecificCorner(int cornerNumber, ColorsEnum color)
    {
        color.setTargetCorner(CornersEnum.values()[cornerNumber]);
        if (AFTER_LOAD == false)
        {
            for (Cell cell : CornersEnum.getCellsOfCorner(cornerNumber))
            {
                cell.PaintCell(color);
            }
        }
    }

    public synchronized Cell[] computerAImovement()
    {
        try
        {
            Thread.sleep(COMPUTER_DELAY);
        } catch (InterruptedException ex)
        {
        }

        Cell cellToMove = getLegalCellToComputerMove();
        DirectionsEnum dir = getTheBestDirectionFromBFS(cellToMove);

        Cell[] srcAndDestComputerCells = new Cell[2];
        srcAndDestComputerCells[0] = cellToMove;
        srcAndDestComputerCells[1] = board.convertDirectionToCell(cellToMove, dir);
        if (!srcAndDestComputerCells[1].IsEmpty())
        {
            srcAndDestComputerCells[1] = board.convertDirectionToCell(srcAndDestComputerCells[1], dir);
        }

        this.moveToLegalCell(cellToMove, dir); // Make Move

        return srcAndDestComputerCells;
    }

    private Cell getLegalCellToComputerMove()
    {
        ColorsEnum randomPlayerColor = findColorToMove();
        Cell targetCellToStartBFS = getTargetCellToStartBFS(randomPlayerColor);
        BFS(targetCellToStartBFS, randomPlayerColor);
        return findCellToMove(randomPlayerColor);
    }

    private ColorsEnum findColorToMove()
    {
        ColorsEnum srcColor;
        do
        {
            srcColor = currentPlayer.getColorsArray()[getRandomInt(currentPlayer.getColorsArray().length)];
        } while (getTargetCellToStartBFS(srcColor) == null);
        return srcColor;
    }

    private Cell getTargetCellToStartBFS(ColorsEnum srcColor)
    {
        Cell res = null;
        final int FIRST_TIER = 1;
        final int LAST_TIER = 4;
        for (int tier = FIRST_TIER; tier <= LAST_TIER && res == null; tier++)
        {
            for (Cell cellInTargetTier : srcColor.getTargetCorner().getTierCells(tier))
            {
                if (cellInTargetTier.getColor() != srcColor)
                {
                    res = cellInTargetTier;
                    break;
                }
            }
        }
        return res;
    }

    //BFS from targetCell to cellToMove
    private void BFS(Cell targetCell, ColorsEnum srcColor)
    {
        Cell currCell, neighborCell;
        ArrayList<Cell> cellsToCheck = new ArrayList<>();

        setInfinityDistanceToAll();
        cellsToCheck.add(targetCell); //Enqeue first cell
        targetCell.setDistanceToTarget(0); // set the distance of the start cell to 0

        while (!cellsToCheck.isEmpty())
        {
            currCell = cellsToCheck.remove(0);
            for (DirectionsEnum legalDir : DirectionsEnum.values())
            {
                neighborCell = board.convertDirectionToCell(currCell, legalDir);
                if (neighborCell != null)
                {
                    if (neighborCell.getDistanceToTarget() > currCell.getDistanceToTarget() + 1 && neighborCell.getColor() != srcColor)
                    {
                        cellsToCheck.add(neighborCell);
                        neighborCell.setDistanceToTarget(currCell.getDistanceToTarget() + 1);
                    }
                }
            }
        }
    }

    private Cell findCellToMove(ColorsEnum colorToMove)
    {
        Cell res = null;
        ArrayList<Cell> relevantCell = currentPlayer.getPlayerCellsWithColor(colorToMove);
//        ArrayList<Cell> relevantCell2 = currentPlayer.getPlayerCells().stream()
//                                                    .filter(cell -> cell.getColor() == colorToMove)
//                                                            .collect(Collectors.toCollection(ArrayList::new));
        do
        {
            res = relevantCell.get(getRandomInt(relevantCell.size()));
        } while (!hasBetterLocation(res));
        return res;
    }

    private boolean hasBetterLocation(Cell cellToMove)
    {
        final DirectionsEnum NO_PREV_MOVE_DIRECTION = null;
        final boolean SHOW_ONLY_JUMPS = true;
        Cell destCell;
        boolean res = false;
        CornersEnum targetCorner = cellToMove.getColor().getTargetCorner();
        double minDistance = Double.POSITIVE_INFINITY;

        for (DirectionsEnum legalDir : getLegalMoves(cellToMove, !SHOW_ONLY_JUMPS, NO_PREV_MOVE_DIRECTION))
        {
            destCell = board.convertDirectionToCell(cellToMove, legalDir);
            if (!destCell.IsEmpty()) //if over jump
            {
                destCell = board.convertDirectionToCell(destCell, legalDir);
            }
            if (isBothCellsInTargetCornerAndDestIsBetterAndCellToMoveNotInDestination(targetCorner, cellToMove, destCell, minDistance))
            {
                res = true;
                break;
            }
        }
        return res;
    }

    public Player getCurrentPlayer()
    {
        return currentPlayer;
    }

    private DirectionsEnum getTheBestDirectionFromBFS(Cell cellToMove)
    {
        final DirectionsEnum NO_PREV_MOVE_DIRECTION = null;
        final boolean SHOW_ONLY_JUMPS = true;
        Cell destCell, minDestCell = null;
        DirectionsEnum res = null;
        CornersEnum targetCorner = cellToMove.getColor().getTargetCorner();
        double minDistance = Double.POSITIVE_INFINITY;

        //find the minimum distance direction from the empty arount the src cell
        for (DirectionsEnum legalDir : getLegalMoves(cellToMove, !SHOW_ONLY_JUMPS, NO_PREV_MOVE_DIRECTION))
        {
            destCell = board.convertDirectionToCell(cellToMove, legalDir);
            if (!destCell.IsEmpty()) //if over jump
            {
                destCell = board.convertDirectionToCell(destCell, legalDir);
            }
            if (isBothCellsInTargetCornerAndDestIsBetterAndCellToMoveNotInDestination(targetCorner, cellToMove, destCell, minDistance))
            {
                minDistance = destCell.getDistanceToTarget();
                minDestCell = destCell;
                res = legalDir;
            }
        }
        if (minDistance == 0)
        {
            minDestCell.setIsInCellTarget();
        }
        return res;
    }

    private boolean isBothCellsInTargetCornerAndDestIsBetterAndCellToMoveNotInDestination(CornersEnum targetCorner, Cell cellToMove, Cell destCell, double minDistance)
    {
        boolean res;
        if (cellToMove.IsInTargetCorner(cellToMove.getColor()) && destCell.IsInTargetCorner(cellToMove.getColor()))
        {
            res = targetCorner.getTierOfCell(destCell) < targetCorner.getTierOfCell(cellToMove) && destCell.getDistanceToTarget() < minDistance;
        } else
        {
            res = destCell.getDistanceToTarget() < minDistance;
        }
        return res && !cellToMove.isInCellTarget();
    }

    void setInfinityDistanceToAll()
    {
        for (int row = 0; row < board.ROWS; row++)
        {
            for (int col = 0; col < board.COLS; col++)
            {
                if (board.getBoard()[row][col] != null)
                {
                    board.getBoard()[row][col].setDistanceToTarget((int) Double.POSITIVE_INFINITY);
                }
            }
        }
    }

    int getRandomInt(int min, int max)
    {
        if (min < max)
        {
            return new Random().nextInt(max) + min;
        } else
        {
            return 0;
        }
    }

    int getRandomInt(int max)
    {
        return getRandomInt(0, max);
    }

    public ArrayList<Player> getPlayerArray()
    {
        return playerArray;
    }

    public void addPlayerToGame(Player player)
    {
        this.playerArray.add(player);
    }

    public void setCurrentPlayer(Player currentPlayer)
    {
        this.currentPlayer = currentPlayer;
    }

    public void saveGame() throws ParserConfigurationException, TransformerException, TransformerConfigurationException, UnsupportedEncodingException
    {
        XMLHandle.saveGame(this);
    }

    public void loadGame() throws ParserConfigurationException, SAXException, IOException, Exception
    {
        AFTER_LOAD = true;
        try
        {
            XMLHandle.loadGame(this);
        } finally
        {
            AFTER_LOAD = false;
        }
    }

    public void checkSameNumOfColorsAfterLoad() throws Exception
    {
        int numOfColorsPerPlayer = playerArray.get(0).getColorsArray().length;
        for (Player player : playerArray)
        {
            if (player.getColorsArray().length != numOfColorsPerPlayer)
            {
                throw new Exception("Colors array of player " + player.getName() + " does not match");
            }
        }
    }

    public void checkNumOfCellsPerColor() throws Exception
    {
        for (Player player : playerArray)
        {
            if (player.getPlayerCells().size() != 10 * player.getColorsArray().length)
            {
                throw new Exception("Player " + player.getName() + " amount of coins doesnt match to amount of colors.");
            }
        }
    }

    public boolean checkInRange(int beginRange, int endRange, int valueTocheck)
    {
        boolean res = false;
        if (valueTocheck > endRange || valueTocheck < beginRange)
        {
            res = true;
        }
        return res;
    }

    public boolean checkIfDirectionInRange(int choosenDirectionIndex, int BEGIN_RANGE, int END_RANGE, Cell choosenCell, boolean onlyJumps, DirectionsEnum lastMoveDirection)
    {
        boolean res = false;
        ArrayList<DirectionsEnum> legalMoves = getLegalMoves(choosenCell, onlyJumps, lastMoveDirection);
        boolean isContains = legalMoves.contains(DirectionsEnum.getName(choosenDirectionIndex));

        if (choosenDirectionIndex < BEGIN_RANGE || choosenDirectionIndex > END_RANGE || isContains == false)
        {
            res = true;
        }
        return res;
    }

    private void subFunctionSetPlayersCorners(int colorsPerPlayer)
    {
        if (colorsPerPlayer == 1)
        {
            placePlayerInCorners(playerArray.get(1), CornersEnum.DOWN);
            placePlayerInCorners(playerArray.get(2), CornersEnum.LEFT_DOWN);
        } else if (colorsPerPlayer == 2)
        {
            placePlayerInCorners(playerArray.get(1), CornersEnum.RIGHT_DOWN);
            placePlayerInCorners(playerArray.get(2), CornersEnum.LEFT_DOWN);
        }
    }

    private void subFunctionSetPlayersCorners()
    {
        placePlayerInCorners(playerArray.get(1), CornersEnum.RIGHT_UP);
        placePlayerInCorners(playerArray.get(2), CornersEnum.DOWN);
        placePlayerInCorners(playerArray.get(3), CornersEnum.LEFT_DOWN);
    }

    private void subFunctionSetPlayersCorners2()
    {
        placePlayerInCorners(playerArray.get(1), CornersEnum.RIGHT_UP);
        placePlayerInCorners(playerArray.get(2), CornersEnum.RIGHT_DOWN);
        placePlayerInCorners(playerArray.get(3), CornersEnum.DOWN);
        placePlayerInCorners(playerArray.get(4), CornersEnum.LEFT_DOWN);
    }

    //////////////////////////////////new methods///////////////////////////////////
    //new function for application 
    public ArrayList<Integer> GetListOfNumOfColors(int numOfPlayers)
    {
        ArrayList<Integer> res = new ArrayList<>();
        if (numOfPlayers == 2)
        {
            res.add(1);
            res.add(2);
            res.add(3);
        } else if (numOfPlayers == 3)
        {
            res.add(1);
            res.add(2);
        } else
        {
            res.add(1);
        }
        return res;
    }

    public static boolean isUniqueNameAmongHumans(String[] playerNames, String playerName)
    {
        boolean res = true;
        int size = playerNames.length;
        for (int playerIndex = 0; playerIndex < size; playerIndex++)
        {
            if (playerNames[playerIndex] != null && playerNames[playerIndex].equals(playerName) == true)
            {
                res = false;
                break;
            }
        }
        return res;
    }

    public boolean checkUniqueNameAmongComputers(String[] playerNames, String playerName, int numOfPlayers)
    {
        boolean res = true;
        int len = numOfPlayers - playerNames.length;
        for (int j = 1; j <= len; j++)
        {
            if (playerName.equalsIgnoreCase("Computer" + j) == true)
            {
                res = false;
                break;
            }
        }
        return res;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndexToSet)
    {
        this.currentPlayerIndex = currentPlayerIndexToSet;
    }

    public void saveStartGameConfig()
    {
        XMLHandle.setFileSource(XMLHandle.START_GAME_CONFIG);
        try
        {
            saveGame();
        } catch (Exception ex)
        {
        }
    }

    public void loadLastGameConfig() throws SAXException, IOException, Exception
    {
        XMLHandle.setFileSource(XMLHandle.START_GAME_CONFIG);
        loadGame();
    }
}
