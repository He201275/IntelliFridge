<?php
require "conn.php";
if(isset($_POST["email"])){
    $user_email = $_POST["email"];
    $mysql_query = "SELECT `UserPrenom`, `UserNom`, `UserLocalite`, `UserAdresseMail`, `UserGenre`, `UserLangue` FROM `user` WHERE UserAdresseMail like '$user_email';";
    $result = mysqli_query($conn,$mysql_query);
    $response = array();

    while ($row = mysqli_fetch_array($result)){
        $response[] = $row;
    }
    echo json_encode(array("server_response"=>$response));
    $conn->close();
}
?>
