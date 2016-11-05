<?php
require "conn.php";

$user_prenom = $_POST["prenom"];
$user_mail = $_POST["email"];
$user_lang = $_POST["langue"];
$user_lang_id = $_POST["langId"];
$user_pass = $_POST["password"];

$query= "insert into User (UserPrenom,UserAdresseMail,UserLangue,LangueId,UserPassword) values ('$user_prenom','$user_mail','$user_lang','$user_lang_id','$user_pass');";

if($conn->query($query) === TRUE){
	echo "Registration Successful!";
}else{
	echo "Registration Error!";
}

$conn->close();
?>
