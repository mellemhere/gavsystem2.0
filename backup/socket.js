$(document).on('click', '#opendoor', function () {
    webSocket.send("o;o"); 
});



$(document).on('click', '.light', function () {
});

var webSocket;

connect();

function connect(){
    webSocket = new WebSocket("ws://" + location.hostname + ":" + location.port + "/room");
    webSocket.onmessage = function (data) { newCommand(data); };
    webSocket.onclose = function(){
        $('#loader-message').html("Conex&atilde;o perdida, tentando reconectar..");
        $('.loader').show();
        setTimeout(function(){if(webSocket != null) connect();}, 5000);
    };
    webSocket.onopen = function () { 
        auth();
        $('.loader').hide();
    };
}


function auth() {
    
}

function newCommand(msg) {
    var data = JSON.parse(msg.data);
    console.log(data);
}
