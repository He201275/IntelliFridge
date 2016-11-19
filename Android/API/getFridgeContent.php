<?php
require "api_functions.php";

$user_id = $_POST["userId"];
$fridge_name = $_POST["fridgeName"];

if (isset($_POST["userId"]) && $_POST["fridgeName"]){
  $query_fridgeId = "SELECT `FrigoId` FROM Frigo WHERE UserId LIKE '$user_id' AND FrigoNom LIKE '$fridge_name';";
  $jsonReturn = array();
  $db = dbConnect();
  if(!is_int($db)){
    $jsonReturn["server-status"] = "Database accessible";
    $answer =$db->query($query_fridgeId);
    if ($data = $answer->rowCount() >0){
      $fridge_id = $data;
      $query_content = "SELECT "
    }
  }else {
    $jsonReturn["server-status"] = "Database not accessible!";
    echo json_encode($jsonReturn);
  }
}
