<?php
# This function reads your DATABASE_URL configuration automatically set by Heroku
# the return value is a string that will work with pg_connect
function pg_connection_string() {
  return "dbname=daqck8ubf75kd0 host=ec2-54-243-49-204.compute-1.amazonaws.com port=5432 user=relsmlsizlydjv password=pbsuJpc0KBlya9anZ-VtJuTZ4Q sslmode=require";
}
 
# Establish db connection
$db = pg_connect(pg_connection_string());
if (!$db) {
    echo "Database connection error."
    exit;
}
 
$result = pg_query($db, "SELECT statement goes here");
?>

<html>
<head>
  <script src="https://cdn.firebase.com/v0/firebase.js"></script>
  <script src='https://ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js'></script>
  <link rel="stylesheet" type="text/css" href="https://www.firebase.com/css/example.css">
</head>
<body>
<h1>X Plays Y</h1>
<h3 id="usernum">Loading...</h3>
<h5 style="display:none" id='nameTitle'>Anonymous</h5>
<div style="display:none" id='messagesDiv'></div>
<input type='text' id='nameInput' placeholder='Name'>
<input style="display:none" type='text' id='messageInput' placeholder='Message...'>
<script>
  // Get a reference to the root of the chat data.
  var usersRef = new Firebase('https://amber-fire-9230.firebaseio.com/userlist');
  var messagesRef = new Firebase('https://amber-fire-9230.firebaseio.com/msg');
  
  usersRef.once('value', function(userlist) {
    $('#usernum').html(userlist.numChildren() + " users online");
  });
  
  usersRef.on('child_added', function(newUser) {
    $('#usernum').html(parseInt($('#usernum').html())+1 + " users online");
  });
  usersRef.on('child_removed', function(newUser) {
    $('#usernum').html(parseInt($('#usernum').html())-1 + " users online");
  });
  
  //input name -> you can now chat
  $('#nameInput').keypress(function (e) {
    if (e.keyCode == 13) {
	  $('#nameTitle').html($('#nameInput').val());
	  $('#nameInput').hide();
	  $('#nameTitle').show();
	  $('#messagesDiv').show();
	  $('#messageInput').show();
      
	  var myself = usersRef.push({name: $('#nameInput').val(), time: Firebase.ServerValue.TIMESTAMP});
	  // Remove ourselves when we disconnect.
	  myself.onDisconnect().remove();
	}
  });
  
  // When the user presses enter on the message input, write the message to firebase.
  $('#messageInput').keypress(function (e) {
    if (e.keyCode == 13) {
	  var name = $('#nameInput').val();
      var text = $('#messageInput').val();
      messagesRef.push({name:name, text:text, time: Firebase.ServerValue.TIMESTAMP});
      $('#messageInput').val('');
    }
  });

  // Add a callback that is triggered for each chat message.
  messagesRef.limit(40).on('child_added', function (snapshot) {
    var message = snapshot.val();
    $('<div/>').text(message.text).prepend($('<em/>')
      .text(message.name+': ')).appendTo($('#messagesDiv'));
    $('#messagesDiv')[0].scrollTop = $('#messagesDiv')[0].scrollHeight;
  });
</script>
</body>
</html>