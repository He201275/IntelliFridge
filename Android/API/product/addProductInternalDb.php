<?php
$product_barcode = "123456789";
$product_brand = "Brand";
$product_quantity = "1g";
$product_imageUrl = "http://intellifridge.franmako.com"

$jsonReturn = array();
if (isset($_POST["barcode"])){
    echo "Success!";
}else{
    $jsonReturn["reponse-status"] = "Required field(s) empty!";
    $jsonReturn["server-status"] = "N/A";
    echo  json_encode($jsonReturn);
}
