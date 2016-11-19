<?php
require "api_functions.php";
$user_email= $_POST["email"];
$user_pass= $_POST["password"];

if (isset($_POST["email"]) && isset($_POST["password"])) {
    $mysql_query= "SELECT `UserId`, `UserPrenom`, `UserNom`, `UserLocalite`, `UserAdresseMail`, `UserGenre`, `UserLangue` FROM `User` WHERE UserAdresseMail like '$user_email' AND UserPassword like '$user_pass';";
    $jsonReturn = array();
    $jsonReturn["reponse-data"]= array();
    $db = dbConnect();
    if(!is_int($db)){
        $jsonReturn["server-status"] = "Database accessible";
        $answer =$db->query($mysql_query);
        if ($answer->rowCount() >0){
            $jsonReturn["reponse-status"] = "Login Successful!";
            while($data = $answer->fetch(PDO::FETCH_ASSOC)){
                $jsonReturn["reponse-data"] = $data;
            }
            echo  json_encode($jsonReturn);
        } else{
            $jsonReturn["reponse-status"] = "Login Unsuccessful!";
            echo json_encode($jsonReturn);
        }
        $answer->closeCursor();
    }else{
        $jsonReturn["server-status"] = "Database not accessible!";
        echo json_encode($jsonReturn);
    }
}
