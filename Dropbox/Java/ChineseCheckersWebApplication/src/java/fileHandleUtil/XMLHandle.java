/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileHandleUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import javax.xml.XMLConstants;
import javax.xml.validation.Validator;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import applicationLogic.Board;
import applicationLogic.Cell;
import applicationLogic.enums.ColorsEnum;
import applicationLogic.enums.CornersEnum;
import applicationLogic.enums.PlayerTypeEnum;
import applicationLogic.GameManager;
import applicationLogic.Player;

/**
 *
 * @author leon
 */
public class XMLHandle
{

    private final static String BOARD = "board";
    private final static String PLAYER = "player";
    private final static String PLAYERS = "players";
    private final static String TYPE = "type";
    private final static String NAME = "name";
    private final static String COLORDEF = "colorDef";
    private final static String COLOR = "color";
    private final static String TARGET = "target";
    private final static String CELL = "cell";
    private final static String ROW = "row";
    private final static String COL = "col";
    private final static String CHINESE_CHECKERS = "chinese_checkers";
    private final static String CURRENT_PLAYER = "current_player";
    private static String userFileSrc = "";
    private static String userFileName = "";
    private static String fileSource = "";
    public static final String RESOURCES_FOLDER_NAME = "Resources";
    public static final String SCHEMA_NAME = "chinese_checkers.xsd";
    private final static InputStream RELATIVE_PATH = XMLHandle.class.getResourceAsStream("/" + RESOURCES_FOLDER_NAME);
    public static final String START_GAME_CONFIG = "TEMP_StartGameConfig.xml";


    public static void setFileSource(String fileSource)
    {
        XMLHandle.fileSource = fileSource;
    }

    public static String getFileSource()
    {
        return fileSource;
    }

    public static InputStream getRelativePath()
    {
        return RELATIVE_PATH;
    }

    public static void saveGame(GameManager game) throws ParserConfigurationException, TransformerConfigurationException, TransformerException, UnsupportedEncodingException
    {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // create root elements
        Element rootElement = createRootElement(doc, game.getCurrentPlayer());

        // create Players elements
        createPlayersElement(doc, rootElement, game.getPlayerArray(), game.getBoard());

        //cells elements
        createCellsElement(doc, rootElement, game.getPlayerArray(), game.getBoard());

        //create xml file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);

        //Setup indenting to "pretty print"
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        StreamResult result = new StreamResult(new File(fileSource));

        //flush the DOMSource information into the sourse file
        transformer.transform(source, result);
    }

    private static Element createRootElement(Document doc, Player currentPlayer)
    {
        Element rootElement = doc.createElement(CHINESE_CHECKERS);
        Attr attr = doc.createAttribute(CURRENT_PLAYER);
        attr.setValue(currentPlayer.getName());
        rootElement.setAttributeNode(attr);
        doc.appendChild(rootElement);
        return rootElement;
    }

    private static void createPlayersElement(Document doc, Element rootElement, ArrayList<Player> playerArray, Board board)
    {
        Element players = doc.createElement(PLAYERS);
        for (Player player : playerArray)
        {
            Element playerNode = doc.createElement(PLAYER);

            Attr playerType = doc.createAttribute(TYPE);
            playerType.setValue(player.getPlayerType().toString().toUpperCase());
            playerNode.setAttributeNode(playerType);

            Attr playerName = doc.createAttribute(NAME);
            playerName.setValue(player.getName());
            playerNode.setAttributeNode(playerName);

            createColorDefElement(player, doc, playerNode, board);

            players.appendChild(playerNode);
        }
        rootElement.appendChild(players);
    }

    private static void createCellsElement(Document doc, Element rootElement, ArrayList<Player> playerArray, Board board)
    {
        Element boardNode = doc.createElement(BOARD);
        for (Player player : playerArray)
        {
            for (Cell cell : player.getPlayerCells())
            {
                Element cellNode = doc.createElement(CELL);

                Attr rowAtt = doc.createAttribute(ROW);
                rowAtt.setValue(String.valueOf(cell.getRow() + 1));
                cellNode.setAttributeNode(rowAtt);

                Attr colAtt = doc.createAttribute(COL);
                ArrayList<Cell> cellsListByRow = board.getCellsByRow()[cell.getRow()];
                int indexInRow = cellsListByRow.indexOf(cell) + 1;
                colAtt.setValue(String.valueOf(indexInRow));
                cellNode.setAttributeNode(colAtt);

                Attr colorAtt = doc.createAttribute(COLOR);
                colorAtt.setValue(cell.getColor().toString().toUpperCase());
                cellNode.setAttributeNode(colorAtt);

                boardNode.appendChild(cellNode);
            }
        }
        rootElement.appendChild(boardNode);
    }

    public static String getUserFileSrc()
    {
        return userFileSrc;
    }

    public static String getUserFileName()
    {
        return userFileName;
    }

    public static void setUserFilePath(String userFileSrc)
    {
        XMLHandle.userFileSrc = userFileSrc;
    }

    public static void setUserFileName(String userFileName)
    {
        XMLHandle.userFileName = userFileName;
    }

    public static void loadGame(GameManager game) throws ParserConfigurationException, SAXException, IOException, Exception
    {
        File fXmlFile = new File(fileSource);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);

        validateXML();

        loadPlayers(doc, game);

        findCurrentPlayer(doc, game.getPlayerArray(), game);

        loadBoardCells(doc, game);

        //set each player number of stones and conect cell to corner
        initAditinalData(game);
    }

    private static void loadPlayers(Document doc, GameManager game) throws Exception
    {
        NodeList nPlayersList = doc.getElementsByTagName(PLAYER);
        for (int nodeIndex = 0; nodeIndex < nPlayersList.getLength(); nodeIndex++)
        {
            Element ePlayerNode = (Element) nPlayersList.item(nodeIndex);
            String playerName = ePlayerNode.getAttribute(NAME);
            PlayerTypeEnum playerType = PlayerTypeEnum.convertStringToPlayerTypeEnum(ePlayerNode.getAttribute(TYPE));

            NodeList nPlayerColors = ePlayerNode.getElementsByTagName(COLORDEF);
            ColorsEnum[] colors = new ColorsEnum[nPlayerColors.getLength()];

            loadTargetCornerDetails(nPlayerColors, colors, game);

            game.addPlayerToGame(new Player(playerName, colors, playerType));
        }
        game.checkSameNumOfColorsAfterLoad();
    }

    private static void findCurrentPlayer(Document doc, ArrayList<Player> playerArray, GameManager game) throws Exception
    {
        //find current player
        NodeList nCurrentPlayersList = doc.getElementsByTagName(CHINESE_CHECKERS);
        Element eCurrentPlayersList = (Element) nCurrentPlayersList.item(0);
        String currentPlayerName = eCurrentPlayersList.getAttribute(CURRENT_PLAYER);
        int currentPlayerIndex = 0;

        boolean found = false;
        for (Player player : playerArray)
        {
            if (player.getName().equals(currentPlayerName) == true)
            {
                game.setCurrentPlayer(player);
                game.setCurrentPlayerIndex(currentPlayerIndex);
                found = true;
                break;
            }
            currentPlayerIndex++;
        }

        if (found == false)
        {
            throw new Exception("The current player " + currentPlayerName + " was not found in players list");
        }

    }

    private static void loadBoardCells(Document doc, GameManager game) throws Exception
    {
        //get cells
        NodeList nCellsList = doc.getElementsByTagName(CELL);
        if (nCellsList.getLength() % 10 != 0)
        {
            throw new Exception("There are not 10 coins for each color");
        }

        int playerIndex = -1;
        for (int nodeIndex = 0; nodeIndex < nCellsList.getLength(); nodeIndex++)
        {
            if (nodeIndex % (10 * game.getCurrentPlayer().getColorsArray().length) == 0)
            {
                playerIndex++;
            }
            Element eCellNode = (Element) nCellsList.item(nodeIndex);
            int row = Integer.parseInt(eCellNode.getAttribute(ROW)) - 1;
            int colXML = Integer.parseInt(eCellNode.getAttribute(COL)) - 1;
            ColorsEnum color = ColorsEnum.convertStringToColorsEnum(eCellNode.getAttribute(COLOR));
            int col = game.getBoard().getCellsByRow()[row].get(colXML).getCol();

            game.getBoard().getBoard()[row][col].PaintCell(color);
            game.getBoard().getBoard()[row][col].setCellIndexPerColor(nodeIndex % 10);
            //add cells per player
            game.getPlayerArray().get(playerIndex).getPlayerCells().add(game.getBoard().getBoard()[row][col]);
        }
        game.checkNumOfCellsPerColor();
    }

    private static void initAditinalData(GameManager game) throws Exception
    {
        for (Player player : game.getPlayerArray())
        {
            for (Cell cell : player.getPlayerCells())
            {
                ColorsEnum movementColor = cell.getColor();
                if (cell.IsInTargetCorner(movementColor) == true)
                {
                    player.decreaseNumOfStonesLeft();
                }
            }
        }
    }

    private static void validateXML() throws SAXException, IOException
    {
        // create a SchemaFactory capable of understanding WXS schemas
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        // load a WXS schema, represented by a Schema instance
        Source schemaFile = new StreamSource(XMLHandle.class.getResourceAsStream("/" + RESOURCES_FOLDER_NAME + "/" + SCHEMA_NAME));
        Schema schema = factory.newSchema(schemaFile);

        // create a Validator instance, which can be used to validate an instance document
        Validator validator = schema.newValidator();

        // validate the DOM tree
        Source xmlFile = new StreamSource(new File(fileSource));

        validator.validate(xmlFile);
    }

    private static void createTargetNode(Element colorDef, Document doc, ColorsEnum playerColor, Board board)
    {
        Element target = doc.createElement(TARGET);
        Cell targetCellInBoard = playerColor.getTargetCorner().getEdgeCell();

        Attr rowTarget = doc.createAttribute(ROW);
        rowTarget.setValue(String.valueOf(targetCellInBoard.getRow() + 1));
        target.setAttributeNode(rowTarget);

        Attr colTarget = doc.createAttribute(COL);
        ArrayList<Cell> cellsListByRow = board.getCellsByRow()[targetCellInBoard.getRow()];
        int indexInRow = cellsListByRow.indexOf(targetCellInBoard) + 1;
        colTarget.setValue(String.valueOf(indexInRow));
        target.setAttributeNode(colTarget);

        colorDef.appendChild(target);
    }

    private static void createColorDefElement(Player player, Document doc, Element playerNode, Board board)
    {
        for (ColorsEnum colorNode : player.getColorsArray())
        {
            Element colorDef = doc.createElement(COLORDEF);
            Attr color = doc.createAttribute(COLOR);
            color.setValue(colorNode.toString().toUpperCase());
            colorDef.setAttributeNode(color);

            createTargetNode(colorDef, doc, colorNode, board);

            playerNode.appendChild(colorDef);
        }
    }

    private static void loadTargetCornerDetails(NodeList nPlayerColors, ColorsEnum[] colors, GameManager game)
    {
        int nPlayerColorsSize = nPlayerColors.getLength();
        for (int nodeColorIndex = 0; nodeColorIndex < nPlayerColorsSize; nodeColorIndex++)
        {
            Element eColor = (Element) nPlayerColors.item(nodeColorIndex);
            colors[nodeColorIndex] = ColorsEnum.convertStringToColorsEnum(eColor.getAttribute(COLOR));

            NodeList targetsList = eColor.getElementsByTagName(TARGET);
            loadTargetCoordinates(targetsList, colors, nodeColorIndex, game);
        }
    }

    private static void loadTargetCoordinates(NodeList targetsList, ColorsEnum[] colors, int nodeColorIndex, GameManager game)
    {
        for (int targetIndex = 0; targetIndex < targetsList.getLength(); targetIndex++)
        {
            String XMLtargetCol = ((Element) targetsList.item(targetIndex)).getAttribute(COL);
            String XMLtargetRow = ((Element) targetsList.item(targetIndex)).getAttribute(ROW);

            int RealTargetRow = Integer.parseInt(XMLtargetRow) - 1;
            ArrayList<Cell> cellsByRow = game.getBoard().getCellsByRow()[RealTargetRow];
            int RealTargetCol = cellsByRow.get(Integer.parseInt(XMLtargetCol) - 1).getCol();

            CornersEnum targetCorner = CornersEnum.getCornerFromEdgeCell(new Cell(RealTargetRow, RealTargetCol));
            colors[nodeColorIndex].setTargetCorner2(targetCorner);
        }

    }
}
