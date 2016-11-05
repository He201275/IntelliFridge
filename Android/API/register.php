<?php
require "conn.php";

if (isset($_POST["fName"]) && isset($_POST["lName"]) && isset($_POST["email"]) && isset($_POST["langue"]) && isset($_POST["password"])) {
	$user_prenom = $_POST["fName"];
	$user_nom = $_POST["lName"];
	$user_mail = $_POST["email"];
	$user_lang = $_POST["langue"];
	//$user_lang_id = $_POST["langId"];
	$user_pass = $_POST["password"];

	$query= "insert into User (UserPrenom,UserNom,UserAdresseMail,UserLangue,UserPassword) values ('$user_prenom','$user_nom','$user_mail','$user_lang','$user_pass');";

	if($conn->query($query) === TRUE){
		echo "Registration Successful!";
	}else{
		echo "Registration Error!";
	}

	$conn->close();
}else {
	echo "Registration Error!";
}
?>
