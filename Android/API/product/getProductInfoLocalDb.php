<?php
require "api_functions.php";

$product_id = $_POST["productId"];
$jsonReturn = array();
if (isset($_POST["productId"])){
    $mysql_query = "SELECT * FROM `ProduitS_Info` WHERE ProduitSId LIKE '$product_id';";
    $db = dbConnect();
    if(!is_int($db)){
        $jsonReturn["server-status"] = "Database accessible";
        $answer =$db->query($mysql_query);
        if ($answer->rowCount() >0){
            $jsonReturn["reponse-status"] = "Product info found";
            while($data = $answer->fetch(PDO::FETCH_ASSOC)){
                $jsonReturn["reponse-data"] = $data;
            }
            echo  json_encode($jsonReturn);
        }else{
            $jsonReturn["reponse-status"] = "Product info not found";
            echo json_encode($jsonReturn);
        }
    }else{
        $jsonReturn["server-status"] = "Database not accessible!";
        echo json_encode($jsonReturn);
    }
}else{
    $jsonReturn["reponse-status"] = "Required field(s) empty!";
    $jsonReturn["server-status"] = "N/A";
    echo  json_encode($jsonReturn);
}
