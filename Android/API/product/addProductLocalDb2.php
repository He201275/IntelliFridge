<?php
require "api_functions.php";
$product_barcode = $_POST["barcode"];
$product_brand = $_POST["brand"];
$product_quantity = $_POST["quantity"];
$product_imageUrl = $_POST["imageUrl"];

$jsonReturn = array();
if (isset($_POST["barcode"]) && isset($_POST["brand"]) && isset($_POST["quantity"]) && $_POST["imageUrl"]){
    $mysql_query= "INSERT";
    $db = dbConnect();
    if(!is_int($db)){
        $jsonReturn["server-status"] = "Database accessible";

    }else{
        $jsonReturn["server-status"] = "Database not accessible!";
        echo json_encode($jsonReturn);
    }
}else{
    $jsonReturn["reponse-status"] = "Required field(s) empty!";
    $jsonReturn["server-status"] = "N/A";
    echo  json_encode($jsonReturn);
}