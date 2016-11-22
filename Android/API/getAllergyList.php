<?php
require "api_functions.php";

$query_allergy_list = "SELECT `AllergieId`,`AllergieNom` FROM Allergie;";

$jsonReturn = array();
$jsonReturn["reponse-data"] = array();
$db = dbConnect();
if(!is_int($db)){
    $jsonReturn["server-status"] = "Database accessible";
    $answer =$db->query($query_allergy_list);
    if ($answer->rowCount() >0){
        $jsonReturn["reponse-status"] = "Allergy List found";
        while($data = $answer->fetch(PDO::FETCH_ASSOC)){
            $jsonReturn["reponse-data"] = $data;
        }
        echo  json_encode($jsonReturn);
    }else{
        $jsonReturn["reponse-status"] = "No allergy List";
        echo json_encode($jsonReturn);
    }
}else{
    $jsonReturn["server-status"] = "Database not accessible!";
    echo json_encode($jsonReturn);
}
