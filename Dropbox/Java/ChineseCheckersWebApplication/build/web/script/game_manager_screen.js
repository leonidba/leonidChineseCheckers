///* 
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
var refreshRate = 500; //miliseconds
var ROWS = 17;
var COLS = 25;
var BrowserPlayer;
var currentPlayer;

function CreatePlayers()
{
    document.getElementById("gameOver").style.visibility = "hidden"; // hide this button
    var funcName = "setPlayers";
    jQuery.ajax({
        data: "funcName=" + funcName,
        async: false,
        url: "GameManagerServlet",
        timeout: 10000,
        error: function () {
            console.error("Failed to create players");
        },
        success: function (player) {
            BrowserPlayer = player;
        }
    });
}

function getPlayers()
{
    var funcName = "getPlayers";
    jQuery.ajax({
        data: "funcName=" + funcName,
        async: false,
        url: "GameManagerServlet",
        timeout: 10000,
        error: function () {
            console.error("Failed to create players");
        },
        //0 - logic current lpayer
        //1 - all players array
        success: function (playersArray) {
            $("#ConnectedPlayersNames").children().remove();
            currentPlayer = playersArray[0];
            $.each(playersArray[1] || [], setPlayersList);
        }
    });
    return currentPlayer; // need it to get the winner player in game over screen
}

function setPlayersList(index, player)
{
    var CurrentPlayerName = currentPlayer.name;
    CurrentPlayerName = CurrentPlayerName.replace(/\s/g, ""); // delete white spaces    
    var playerName = player.name;
    playerName = playerName.replace(/\s/g, "");
    if (player.playerType === "Human")
    {
        if (playerName === CurrentPlayerName)
        {
            $("#ConnectedPlayersNames").append("<tr id=" + playerName + "><td><img src=\"resources/human.png\" id=\"icon\"/></td><td id=\"playerName\" class=\"currentPlayer\">" + player.name + "</td> </tr>");
        }
        else
        {
            $("#ConnectedPlayersNames").append("<tr id=" + playerName + "><td><img src=\"resources/human.png\" id=\"icon\"/></td><td id=\"playerName\" class=" + playerName + ">" + player.name + "</td> </tr>");
        }
    }
    else
    {
        if (playerName === CurrentPlayerName)
        {
            $("#ConnectedPlayersNames").append("<tr id=" + playerName + "><td><img src=\"resources/computer.png\" id=\"icon\" /></td><td id=\"playerName\" class=\"currentPlayer\">" + player.name + "</td> </tr>");
        }
        else
        {
            $("#ConnectedPlayersNames").append("<tr id=" + playerName + "><td><img src=\"resources/computer.png\" id=\"icon\" /></td><td id=\"playerName\" class=" + playerName + ">" + player.name + "</td> </tr>");
        }

    }

    for (var i = 0; i < player.color.length; i++)
    {
        $("#" + playerName).append("<td><button class=" + player.color[i] + "></button></td>");
    }
}

function DrawBoard()
{
    var cellColor;
    var board = getBoard();
    for (var row = 0; row < ROWS; row++)
    {
        $("#StartID").append("<br>");
        for (var col = 0; col < COLS; col++)
        {
            if (board[row][col] === undefined || board[row][col] === null)
            {
                $("#StartID").append("<button type=\"submit\" class=\"white_space\"  id=\"B" + row + "_" + col + "\" disabled></button>&nbsp");
            }
            else
            {
                cellColor = board[row][col].color;
                $("#StartID").append("<button type=\"submit\" class=\"" + cellColor + "\" id=\"B" + row + "_" + col + "\" onClick=\"onClickButton(" + row + "," + col + ")\"></button>&nbsp");
            }
        }
    }
    document.getElementById("endTurn").style.visibility = "hidden";
    setInterval(updateBoard, refreshRate);
    setInterval(getPlayers, refreshRate);

    var colorClass;
    for (var i = 0; i < BrowserPlayer.color.length; i++)
    {
        colorClass = "." + BrowserPlayer.color[i];
        $(colorClass).mouseenter(function () {
            $(this).css("-webkit-transform", "translateY(-8px)");
        }).mouseleave(function () {
            $(this).css("-webkit-transform", "translateY(+8px)")
        });
    }
}

function clearLegalMoves()
{
    for (var row = 0; row < ROWS; row++)
    {
        for (var col = 0; col < COLS; col++)
        {
            var buttonID = $("#B" + row + "_" + col);
            if ($(buttonID).attr('class') === "legalMove")
            {
                $(buttonID).attr('class', "Empty");
            }
        }
    }
}

function endTurn()
{
    var funcName = "endTurn";
    jQuery.ajax({
        data: "funcName=" + funcName,
        async: false,
        url: "GameManagerServlet",
        timeout: 10000,
        error: function () {
            console.error("Failed to recieve end player turn");
        },
        complete: function () {
            clearLegalMoves();
            document.getElementById("endTurn").style.visibility = "hidden";
            checkIfComputerMove();
        }
    });
}
function updateBoard()
{
    var board = getBoard();
    var cellColor;
    for (var row = 0; row < ROWS; row++)
    {
        for (var col = 0; col < COLS; col++)
        {
            var buttonID = $("#B" + row + "_" + col);
            if (board[row][col] !== undefined && board[row][col] !== null && $(buttonID).attr('class') !== "legalMove")
            {
                cellColor = board[row][col].color;
                $(buttonID).attr('class', cellColor);
            }
        }
    }
    checkIsGameOver();
}

function getBoard()
{
    var Board;
    var funcName = "getBoard";
    jQuery.ajax({
        data: "funcName=" + funcName,
        async: false,
        url: "GameManagerServlet",
        timeout: 10000,
        error: function () {
            console.error("Failed to recieve board");
        },
        success: function (i_board) {
            Board = i_board;
        }
    });
    return Board;
}

function checkIsGameOver()
{
    var funcName = "isGameOver";
    jQuery.ajax({
        data: "funcName=" + funcName,
        async: false,
        url: "GameManagerServlet",
        timeout: 10000,
        error: function () {
            console.error("Failed to ask if game is over");
        },
        success: function (isGameOver) {
            if(isGameOver == true)
            {
                document.getElementById("gameOver").style.visibility = "visible";
                //$('.button').attr("disabled", true);// TODO disable all buttons
            }
        }
    });    
}

function onClickButton(row, col)
{
    var funcName = "onButtonClick";
    var className = $("#B" + row + "_" + col).attr('class');
    jQuery.ajax({
        data: "funcName=" + funcName + "&row=" + row + "&col=" + col + "&buttonClass=" + className,
        async: false,
        url: "GameManagerServlet",
        timeout: 10000,
        error: function () {
            console.error("Failed to handle button click");
        },
        // 0 - returned type
        // 1,2 - pairs or row+col to paint as "legal Moves"
        // 3 - isJumped
        success: function (dataArr) {
            ClearLegalMoves();
            if (dataArr[0] === "legalMoves")
            {
                if (dataArr[1].length === 0)
                {
                    document.getElementById("endTurn").style.visibility = "hidden";
                }
                clearInterval(updateBoard);
                $.each(dataArr[1] || [], PaintButtonAsLegalMove);
            }
            else if (dataArr[0] === "handleSelectDestCell")
            {
                var rowAndColSrc = dataArr[1];
                var rowAndColSDest = dataArr[2];
                rePaint(rowAndColSrc, rowAndColSDest);
                if (dataArr[3] == true) // if isJump move
                {
                    document.getElementById("endTurn").style.visibility = "visible";
                    onClickButton(rowAndColSDest.row, rowAndColSDest.col);
                }
                else // not jump - end of turn
                {
                    document.getElementById("endTurn").style.visibility = "hidden";
                    setInterval(updateBoard, refreshRate);
                }
                checkIfComputerMove();
            }
        }
    });
}

function checkIfComputerMove()
{
    var funcName = "computerMove";
    jQuery.ajax({
        data: "funcName=" + funcName,
        datatype: "json",
        url: "GameManagerServlet",
        timeout: 10000,
        error: function () {
            console.error("Failed to to computer move");
        },
        complete: function () {
            updateBoard(); //TODO not necassery
        }
    });
}

function PaintButtonAsLegalMove(index, rowAndCol)
{
    var row = rowAndCol.row;
    var col = rowAndCol.col;
    var ButtonId = "#B" + row + "_" + col;
    $(ButtonId).attr('class', "legalMove");
}

function rePaint(srcCell, destCell)
{
    //Clear srcCell
    var row = srcCell.row;
    var col = srcCell.col;
    var ButtonId = "#B" + row + "_" + col;
    $(ButtonId).attr('class', "Empty");
    //Paint destCell according to logic part
    row = destCell.row;
    col = destCell.col;
    var color = destCell.color;
    ButtonId = "#B" + row + "_" + col;
    $(ButtonId).attr('class', color);
}

function ClearLegalMoves()
{
    for (var row = 0; row < ROWS; row++)
    {
        for (var col = 0; col < COLS; col++)
        {
            var ButtonId = "#B" + row + "_" + col;
            if ($(ButtonId).attr('class') === "legalMove")
            {
                $(ButtonId).attr('class', "Empty");
            }
        }
    }
}

function quitGame()
{
    var funcName = "quitGame";
    jQuery.ajax({
        data: "funcName=" + funcName,
        datatype: "json",
        async: false,
        url: "GameManagerServlet",
        timeout: 10000,
        complete: function () {
            document.getElementById("endTurn").style.visibility = "hidden";
            ClearLegalMoves(); // in case player quit after choose a cell to move
            updateBoard(); // TODO not neccassey due to auto refresh after second
            checkIfComputerMove();
        }
    });
}

//function OnCloseTab()
//{
//    var funcName = "removeFromConnectedPlayers";
//    jQuery.ajax({
//        data: "funcName=" + funcName,
//        async: false,
//        url: "GameManagerServlet",
//        timeout: 2000,
//        error: function () {
//            console.error("Failed to to remove player from connected player on closing tab");
//        }
//    });
//}


var toScreen = "out";
$(window).unload(function () {
    var funcName = "removeFromConnectedPlayers";
    jQuery.ajax({
        data: "funcName=" + funcName + "&fromScreen=" + "game_screen" + "&toScreen=" + toScreen,
        async: false,
        url: "GameManagerServlet",
        timeout: 10000,
        error: function () {
            console.error("Failed to to remove player from connected player on closing tab");
        }
    });
});