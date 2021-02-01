$("#userProfile").hide();
$("#arrowProfile").hide();
$("#btnProfileHome").click(function(){
    $("#arrowProfile").hide();
    $("#logoProfile").show("slow");
    $("#userProfile").hide();
    $("#pollProfile").show("slow");
    $("#navPollProfile").show("slow");
});

$("#arrowProfile").click(function(){
    $("#arrowProfile").hide();
    $("#logoProfile").show("slow");
    $("#userProfile").hide();
    $("#pollProfile").show("slow");
    $("#navPollProfile").show("slow");
});

$("#btnProfileUser").click(function(){
    $("#logoProfile").hide();
    $("#arrowProfile").show("slow");
    $("#pollProfile").hide();
    $("#navPollProfile").hide("slow");
    $("#userProfile").show("slow");
});


$('.requirePopUpConfirmation').on('click', function () {
        return confirm("The operation you are about to perform is irreversible.\nAre you sure you want to continue?");
});

