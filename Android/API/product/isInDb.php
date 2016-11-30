<?php
require "api_functions.php";

$product_barcode = $_POST["barcode"];

if (isset($_POST["barcode"])) {
    $mysql_query = "SELECT * FROM `ProduitS` WHERE BarcodeNum LIKE '$product_barcode';";
    $jsonReturn = array();
    $db = dbConnect();
    if(!is_int($db)){
        $jsonReturn["server-status"] = "Database accessible";
        $answer =$db->query($mysql_query);
        if ($answer->rowCount() >0){
            $jsonReturn["reponse-status"] = "In local db";
            while($data = $answer->fetch(PDO::FETCH_ASSOC)){
                $jsonReturn["reponse-data"] = $data;
            }
            echo  json_encode($jsonReturn);
        }else{
            $jsonReturn["reponse-status"] = "Not in local db";
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