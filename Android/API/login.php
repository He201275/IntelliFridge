<?php
require "conn.php";
$user_email= $_POST["email"];
$user_pass= $_POST["password"];

if (isset($_POST["email"]) && isset($_POST["password"])) {
    $mysql_query= "select * from User where UserAdresseMail like '$user_email' and UserPassword like '$user_pass';";
    $result = mysqli_query($conn,$mysql_query);

    if(mysqli_num_rows($result) > 0){
        echo "Login Success!";
    }else {
        echo "Login Unsuccessful!";
    }

    $conn->close();
}
?>
