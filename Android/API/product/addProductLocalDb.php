<?php
require "api_functions.php";

$product_barcode = "123456789";
$product_brand = "Brand";
$product_quantity = "1g";
$product_imageUrl = "http://intellifridge.franmako.com"
/*$product_barcode = $_POST["barcode"];
$product_brand = $_POST["brand"];
$product_quantity = $_POST["quantity"];
$product_imageUrl = $_POST["imageUrl"];*/

$jsonReturn = array();
if (isset($_POST["barcode"])){
    echo "$product_barcode";
    $mysql_query = "INSERT INTO ProduitS_Info (Marque,Quantite,ImageUrl) VALUES ('$product_brand','$product_quantity','$product_imageUrl');";
    $db = dbConnect();

    if(!is_int($db)){
        $jsonReturn["server-status"] = "Database accessible";
        if ($db->exec($mysql_query)){
            print_r($jsonReturn);
            /*$mysql_query_produitId = "SELECT `ProduitSId` FROM `ProduitS_Info` WHERE `Marque` LIKE '$product_brand' AND `Quantite` LIKE '$product_quantity' AND `ImageUrl` LIKE '$product_imageUrl';";
            $answer =$db->query($mysql_query_produitId);
            print_r($answer);

            $mysql_query_fridge = "INSERT INTO ProduitS (ProduitSId,BarcodeNum) VALUES ('$product_id','$product_barcode');";
            if ($db->exec($mysql_query_fridge)){
                $jsonReturn["reponse-status"] = "Product insert db successful";
                echo  json_encode($jsonReturn);
            }*/
        }else{
            $jsonReturn["reponse-status"] = "Product insert db successful error";
            echo  json_encode($jsonReturn);
        }
    }else{
        $jsonReturn["server-status"] = "Database not accessible!";
        echo  json_encode($jsonReturn);
    }
}else{
    $jsonReturn["reponse-status"] = "Required field(s) empty!";
    $jsonReturn["server-status"] = "N/A";
    echo  json_encode($jsonReturn);
}
