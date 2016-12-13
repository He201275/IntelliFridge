<?php
require "api_functions.php";
$phone_version = $_POST["version"];
$latest_version = "0.8.8";

if ($phone_version != $latest_version){
    echo "Update available";
}else{
    echo "App up-to-date";
}

