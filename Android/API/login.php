<?php
require "api_functions.php";

$jsonReturn = array();

//if (isset($_POST["email"]) && isset($_POST["password"])) {
    /*$user_email= $_POST["email"];
    $user_pass= $_POST["password"];*/
$user_email= "john@doe.net";
$user_pass= "Passw0rd";

    $mysql_query= "SELECT `UserId`, `UserPrenom`, `UserNom`, `CommuneLocalite`, `UserAdresseMail`, `UserGenre`, `LangueCode` FROM `User` WHERE UserAdresseMail like ? AND UserPassword like ?;";
    $db = dbConnect();

    if(!is_int($db)){
        $jsonReturn["server-status"] = "Database accessible";
        //$answer =$db->query($mysql_query);
        $stmt = $db->prepare($mysql_query);
        $stmt->execute([$user_email,$user_pass]);
        $user = $stmt->fetch();
        //if (){
            echo "Success ";
            $jsonReturn["reponse-status"] = "Login Successful!";
            /*while($data = $answer->fetch(PDO::FETCH_ASSOC)){
                $jsonReturn["reponse-data"] = $data;
            }*/
        $jsonReturn["reponse-data"] = $stmt->fetch(PDO::FETCH_ASSOC);
            echo  json_encode($jsonReturn);
        /*} else{
            $jsonReturn["reponse-status"] = "Login Unsuccessful!";
            echo json_encode($jsonReturn);
        }*/
        $answer->closeCursor();
    }else{
        $jsonReturn["server-status"] = "Database not accessible!";
        echo json_encode($jsonReturn);
    }
/*}else{
    $jsonReturn["reponse-status"] = "Required field(s) empty!";
    $jsonReturn["server-status"] = "N/A";
    echo  json_encode($jsonReturn);
}*/