<?php
require "api_functions.php";

$user_id = $_POST["userId"];

if (isset($_POST["userId"])){
    $query = "SELECT `FrigoId`,`FrigoNom` FROM Frigo WHERE UserId LIKE '$user_id';";

    $jsonReturn = array();
    $jsonReturn["reponse-data"] = array();
    $db = dbConnect();
    if(!is_int($db)){
        $jsonReturn["server-status"] = "Database accessible";
        $answer =$db->query($query);
        if ($answer->rowCount() >0){
            $jsonReturn["reponse-status"] = "Fridges found";
            while($data = $answer->fetch(PDO::FETCH_ASSOC)){
                $jsonReturn["reponse-data"][] = $data;
            }
            echo  json_encode($jsonReturn);
        }else{
            $jsonReturn["reponse-status"] = "No Fridges";
            echo json_encode($jsonReturn);
        }
    }else{
        $jsonReturn["server-status"] = "Database not accessible!";
        echo json_encode($jsonReturn);
    }
}
