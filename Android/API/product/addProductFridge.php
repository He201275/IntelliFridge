<?php
require "api_functions.php";

$product_id = $_POST["productId"];
$jsonReturn = array();
if (isset($_POST["productId"])){

}else{
    $jsonReturn["reponse-status"] = "Required field(s) empty!";
    $jsonReturn["server-status"] = "N/A";
    echo  json_encode($jsonReturn);
}