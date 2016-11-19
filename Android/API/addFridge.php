<?php
require "api_functions.php";
$user_id = $_POST["userId"];
$fridge_name = $_POST["fridgeName"];

$mysql_query= "insert into Frigo (FrigoNom,UserId) values ('$fridge_name','$user_id');";
$jsonReturn = array();
$db = dbConnect();

if(!is_int($db)){
    $jsonReturn["server-status"] = "Database accessible";
    if ($db->exec($mysql_query)){
        $jsonReturn["reponse-status"] = "Fridge added";
        echo  json_encode($jsonReturn);
    }else{
        $jsonReturn["reponse-status"] = "Fridge add error";
        echo  json_encode($jsonReturn);
    }
}else{
    $jsonReturn["server-status"] = "Database not accessible!";
    echo  json_encode($jsonReturn);
}
