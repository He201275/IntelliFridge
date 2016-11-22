<?php
require "api_functions.php";

if (isset($_POST["fName"]) && isset($_POST["lName"]) && isset($_POST["email"]) && isset($_POST["langue"]) && isset($_POST["password"])) {
    $user_prenom = $_POST["fName"];
    $user_nom = $_POST["lName"];
    $user_mail = $_POST["email"];
    $user_lang = $_POST["langue"];
    $user_pass = $_POST["password"];
    switch ($user_lang){
        case "Français":
            $user_lang_id = 1;
            break;
        case "Nederlands":
            $user_lang_id = 2;
            break;
        case "English":
            $user_lang_id = 3;
            break;
    }

    $mysql_query= "insert into User (UserPrenom,UserNom,UserAdresseMail,UserLangue,UserPassword,LangueId) values ('$user_prenom','$user_nom','$user_mail','$user_lang','$user_pass','$user_lang_id');";
    $jsonReturn = array();
    $db = dbConnect();

    if(!is_int($db)){
        $jsonReturn["server-status"] = "Database accessible";
        if ($db->exec($mysql_query)){
            $jsonReturn["reponse-status"] = "Registration Successful!";
            echo  json_encode($jsonReturn);
        }else{
            $jsonReturn["reponse-status"] = "Registration Error!";
            echo  json_encode($jsonReturn);
        }
    }else{
        $jsonReturn["server-status"] = "Database not accessible!";
        echo  json_encode($jsonReturn);
    }
}else {
    $jsonReturn["reponse-status"] = "Required field(s) empty!";
    echo  json_encode($jsonReturn);
}
