/* global doorID */

var doorID = doorID;

$(document).on('click', '#opendoor', function () {
    webSocket.send("o;o");
});


$(document).on('click', '.light', function () {
    webSocket.send("l;" + $(this).attr('lid'));
    if (!($(this).hasClass("btn-success"))) {
        $(this).addClass("btn-success");
    } else {
        $(this).removeClass("btn-success");
    }
});

var webSocket;

connect();

function connect() {
    webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/room");
    webSocket.onmessage = function (data) {
        newCommand(data);
    };
    webSocket.onclose = function () {
        $('#loader-message').html("Conex&atilde;o perdida, tentando reconectar..");
        // $('.loader').show();
        setTimeout(function () {
            if (webSocket != null)
                connect();
        }, 5000);
    };

    webSocket.onopen = function () {
        auth();
        $('.loader').hide();
    };
}


function auth() {
    webSocket.send("id;" + doorID);
}

function newCommand(msg) {
    var data = JSON.parse(msg.data);
    var args = data.message;
    switch (data.event) {
        case "webcam":
            $('.webcam').attr('src', args.message);
            break;
        case "light" :
            alert(args.message);
            break;
        case "serverUptime" :
            $("#serverUptime").html(args.uptime);
            break;
        case "uptime" :
            $(".timealive").html(args.uptime);
            break;
            
        case "consumo" :
            $(".consumoDeEnergiaPreco").html(args.message + " reais");
            break;
        case "consumoW" :
            $(".consumoDeEnergia").html(args.message + " watts");
            break;
        case "light_changed" :
            var lid = args.lightID;
            var state = args.lightState;
            if (state == "on") {
                if (!($("*[lid=" + lid + "]").hasClass("btn-success"))) {
                    $("*[lid=" + lid + "]").addClass("btn-success");
                }
            } else {
                $("*[lid=" + lid + "]").removeClass("btn-success");
            }
            break;
        default :
            console.log(data);
    }
}
