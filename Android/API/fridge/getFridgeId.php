<?php
require "api_functions.php";

$user_id = $_POST["userId"];
$fridge_name = $_POST["fridgeName"];

$jsonReturn = array();

if (isset($_POST["userId"]) && $_POST["fridgeName"]){
    $query = "SELECT `FrigoId` FROM Frigo WHERE UserId LIKE '$user_id' AND FrigoNom LIKE '$fridge_name';";
    $db = dbConnect();
    if(!is_int($db)){
        $jsonReturn["server-status"] = "Database accessible";
        $answer =$db->query($query_fridgeId);
        if ($data = $answer->rowCount() >0){
            while($data = $answer->fetch(PDO::FETCH_ASSOC)){
                $jsonReturn["reponse-data"] = $data;
            }
            echo  json_encode($jsonReturn);
        }
    }else {
        $jsonReturn["server-status"] = "Database not accessible!";
        echo json_encode($jsonReturn);
    }
}else{
    $jsonReturn["reponse-status"] = "Required field(s) empty!";
    $jsonReturn["server-status"] = "N/A";
    echo  json_encode($jsonReturn);
}