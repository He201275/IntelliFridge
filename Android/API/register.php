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
    $db = dbConnect();

    if(!is_int($db)){
        if ($db->exec($mysql_query)){
            echo "Registration Successful!";
        }else{
            echo "Registration Error!";
        }
    }else{
        echo "Database not accessible!";
    }
}else {
    echo "Required field(s) empty!";
}
