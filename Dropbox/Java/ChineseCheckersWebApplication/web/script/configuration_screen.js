/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


//Update num of human players list
$(function () {
    $("#numOfPlayers").change(function () {
        $("#numOfHumanPlayers").empty();

        for (var i = 1; i <= $("#numOfPlayers option:selected").val(); i++)
        {
            var newOption = $('<option value="' + i + '">' + i + '</option>');
            $("#numOfHumanPlayers").append(newOption);
        }
    });
});

//Update num of colors list
$(function () {
    $("#numOfPlayers").change(function () {
        $("#numOfColors").empty();

        var numOfPlayers = $("#numOfPlayers option:selected").val();
        if (numOfPlayers == 2)
        {
            $("#numOfColors").append($('<option value="' + 1 + '">' + 1 + '</option>'));
            $("#numOfColors").append($('<option value="' + 2 + '">' + 2 + '</option>'));
            $("#numOfColors").append($('<option value="' + 3 + '">' + 3 + '</option>'));
        }
        else if (numOfPlayers == 3)
        {
            $("#numOfColors").append($('<option value="' + 1 + '">' + 1 + '</option>'));
            $("#numOfColors").append($('<option value="' + 2 + '">' + 2 + '</option>'));
        }
        else
        {
            $("#numOfColors").append($('<option value="' + 1 + '">' + 1 + '</option>'));
        }
    });
});