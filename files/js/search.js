

$('#almoxinput').on('keyup', function () {

    $.post("/items", {query: $(this).val()}, function (data) {
        $("#item-search tbody").html("");
        data = JSON.parse(data);
        $.each(data, function (i, item) {
            $("#item-search tbody").append("<tr><td>" + item.name + "</td><td>" + item.box + "</td><td>" + item.area + "</td><td>" + item.area + "</td><td><a href='#'>Em falta</a></td></tr>");
        });
    });
    $("#item-search").show();
}).on('click', function () {
    $('.item-search').fadeIn(0.5);
});

$("body").click(function (e) {
    if ((!$('div#item-search').has(e.target).length) && (e.target.id != "almoxinput")) {
        $('.item-search').fadeOut(0.5);
    }
});