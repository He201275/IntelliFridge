<?php
  require "conn.php";
  $user_email= $_POST["email"];
  $user_pass= $_POST["password"];

  $mysql_query= "select * from tbl_login where email like '$user_email' and password like '$user_pass';";
  $result = mysqli_query($conn,$mysql_query);

  if(mysqli_num_rows($result) > 0){
    echo "Login Success";
  }else {
    echo "Login Unsuccesful";
  }
?>
