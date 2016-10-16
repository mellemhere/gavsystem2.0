/*
 * 
 * Handle to open and close left bar helper
 */

var moving = false;
var open = false;
var time_to_open = 100;

function openLeftBar() {
    if (!moving) {
        moving = true;
        $(".side-menu").animate({
            left: '0px'
        }, time_to_open, function () {
            moving = false;
            open = true;
        });
    }
}

function closeLeftBar() {
    if (!moving) {
        moving = true;
        $(".side-menu").animate({
            left: '-255px'
        }, time_to_open, function () {
            moving = false;
            open = false;
        });
    }
}

function toggleLeftBar() {
    if (open) {
        closeLeftBar();
    } else {
        openLeftBar();
    }
}


$(document).ready(function () {


    $(".hm").on('click', function () {
        toggleLeftBar();
    });

});