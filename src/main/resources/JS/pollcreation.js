$(document).ready(function(){
    //verifica lo stato delle checkbox al caricamento della pagina
    var privateCheckBox = document.getElementById("classified");
    var doubleRoundCheckBox = document.getElementById("double-round");

    if(privateCheckBox.checked == true){
            $("#classified-email").show();
        } else {
            $("#classified-email").hide();
        }

    if(doubleRoundCheckBox.checked == true){
            $("#doubleRound").show();
        } else {
            $("#doubleRound").hide();
        }

    //set min attributes
    setMinAttributes();

    //set max attributes
    var formattedPollExpirationDate = document.getElementById("expirationPoll").getAttribute("value");
    if (formattedPollExpirationDate != null && formattedPollExpirationDate != ''){
        setMaxAttributeForQuestionExpirationDate(formattedPollExpirationDate);
    }
});

$("#expirationPoll").on("change", function(){
    var formattedPollExpirationDate = $("input[id=expirationPoll]").val();
    setMaxAttributeForQuestionExpirationDate(formattedPollExpirationDate);
});

//DATA MINIMA NEI DATE PICKER
//QUESTION DATE --> DATA E ORA AL MOMENTO DELLA CREAZIONE DEL POLL
//POLL DATE --> MEZZ'ORA DOPO LA DATA DELLA QUESTION
function setMinAttributes(){
    //millisecondi dal 1/1/1970 ad oggi, timezone UTC
    var dateUTCMillis = Date.now();
    //millisecondi dal 1/1/1970 ad oggi, timezone Europa Centrale
    var dateLocaleMillis = addHours(dateUTCMillis, 1);
    //data e ora in formato "yyyy-mm-ddTHH:mm" pari all'ora locale -> DATA MINIMA DI SCADENZA DELLA QUESTION
    var formattedQuestionExpirationDateMin = new Date(addHours(dateLocaleMillis, (1/6))).toISOString().slice(0, -8);
    //data e ora in formato "yyyy-mm-ddTHH:mm" mezz'ora avanti all'ora locale -> DATA MINIMA DI SCADENZA DEL POLL
    var formattedPollExpirationDateMin = new Date(addHours(dateLocaleMillis, 0.5)).toISOString().slice(0, -8);
    //inserimento valori nell'attributo min dell'input datetime
    document.getElementById("expirationQuestion").min = formattedQuestionExpirationDateMin;
    document.getElementById("expirationPoll").min = formattedPollExpirationDateMin;
}

//DATA MASSIMA PER LA SCADENZA DELLA DOMANDA: 10 MINUTI PRIMA DELLA DATA DI SCADENZA MASSIMA DEL POLL
function setMaxAttributeForQuestionExpirationDate(formattedPollExpirationDate) {
    /*
    var pollExpirationDateInMillis = addHours(new Date(formattedPollExpirationDate).getTime(), 1);
    var maxQuestionExpirationDateInMillis = addHours(pollExpirationDateInMillis, -(1/6));
    var formattedQuestionExpirationDateMax = new Date(maxQuestionExpirationDateInMillis).toISOString().slice(0, -8);
    */
    document.getElementById("expirationQuestion").max = formattedPollExpirationDate;
}

//function to add or substract hours from date in epoch millis format
//accept negative values as well
function addHours(date, h){
    return date + (h*60*60*1000);
}

function toggleEmailForm() {
    $("#classified-email").toggle();
}

function toggleDoubleRound() {
    $("#doubleRound").toggle();
}