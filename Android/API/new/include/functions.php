<?php
/**
 * Created by PhpStorm.
 * User: Sofiane
 * Date: 16-11-16
 * Time: 20:02
 */

use \Firebase\JWT\JWT;
require '../vendor/autoload.php';

/*
 * SQL related functions 
 */
function connectDB ($host="", $dbName="", $username="", $password="") {
    try
    {
        $opt = array(
            PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
            PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
        );
        return new PDO("mysql:host=$host;dbname=$dbName", $username, $password, $opt);
    }
    catch (Exception $e)
    {
        die('Erreur : ' . $e->getMessage());
        return false;
    }
}

/**
 * @author Alexis Georges
 * @param $db
 * @param $req
 * @param $params
 * @return bool
 */
function sendSQLReq ($db, $req, $params){
    $logger = new \Monolog\Logger('my_logger');
    $file_handler = new \Monolog\Handler\StreamHandler("../logs/app.log");
    $logger->pushHandler($file_handler);
    $logger->addDebug("treytstsgf");
    if (count($params) > 0){
        if (substr_count($req, ":") == count($params)){
            $stmt = $db -> prepare($req);
            

            $keys = array_keys($params);
            for ($i=0; $i<count($params); $i++){
                $params[$keys[$i]] = stripslashes(htmlspecialchars($params[$keys[$i]]));
            }
            try {
                if ($stmt -> execute($params))
                    return true;
            }
            catch (PDOException $e) {
                echo $e->getMessage();

                $logger->addError("SQL".$e->getMessage());
                $logger->addError("SQL COmmand: ".$stmt->debugDumpParams());

                foreach ($e->errorInfo as $k=>$value){
                    $logger->addError("SQL error Info ".$k." : ".$value);
                }
                $logger->addError("1");
                return false;
            }

            $logger->addError("2");
            return false;

        }
        $logger->addError("3");
        $logger->addDebug("req".$req);
        $logger->addError("nbparam, :".count($params));
        $logger->addError("nbp, :".substr_count($req, ":"));
        return false;


    }

    $logger->addError("4");
    return false;

}

function fetchSQLReq ($db, $req, $params=NULL, $isOneArg=false, $isOneRow=false){
    if ($params != NULL && count($params) > 0){
        if (substr_count($req, ":") == count($params)){
            $stmt = $db -> prepare($req);

            $keys = array_keys($params);
            /*
             * clean inputs in case of...
             */
            for ($i=0; $i<count($params); $i++){
                $params[$keys[$i]] = stripslashes(htmlspecialchars($params[$keys[$i]]));
            }
            try {
                if ($stmt -> execute($params)) {
                    if ($result = $stmt -> fetchAll()){
                        if (isset($isOneArg)){
                            if ($isOneArg){
                                if (count($result) == 1){
                                    $keys = array_keys($result[0]);
                                    if (count($keys)){
                                        return $result[0][$keys[0]];
                                    }
                                    return false;
                                }
                                return false;
                            }
                        }
                        if (isset($isOneRow)){
                            if ($isOneRow){
                                return $result[0];
                            }

                        }
                        return $result;
                    }
                    return -1;
                }
                return false;
            }
            catch (PDOException $e) {
                echo $e->getMessage();
                $logger = new \Monolog\Logger('my_logger');
                $file_handler = new \Monolog\Handler\StreamHandler("../logs/app.log");
                $logger->pushHandler($file_handler);
                $logger->addError("SQL".$e->getMessage());
                foreach ($e->errorInfo as $k=>$value){
                    $logger->addError("SQL error Info ".$k." : ".$value);
                }
                return false;
            }
            return false;
        }
        return false;
    }
    if (strpos($req, ":") !== false)
        return false;
    $stmt = $db -> prepare($req);
    $stmt -> execute();
    if ($result = $stmt -> fetchAll()) {
        if (isset($isOneArg)){
            if ($isOneArg){
                if (count($result) == 1){
                    $keys = array_keys($result[0]);
                    if (count($keys)){
                        return $result[0][$keys[0]];
                    }
                    return false;
                }
                return false;
            }
        }
        if (isset($isOneRow)){
            if ($isOneRow)
                return $result[0];
        }
        return $result;
    }
    else {
        return false;
    }
}

function encodeJWT($data){
    $key = "wAMxBauED07a4GurMpuD";
    /*$payload = array(
        "iss" => "http://api.intellifridge.ovh",
        "iat" => time(),
        "exp" => time()+ (120 * 60),
        "response" => $data
    );*/
    return JWT::encode($data, $key , "HS256");
}
function checkJWT($encoded){
        $key = "wAMxBauED07a4GurMpuD";
    try{
        $decoded = JWT::decode($encoded, $key, array("HS256"));
        $response = json_decode(json_encode($decoded), true);
        return $response;
    }catch (Exception $e)
    {
        return false;
    }
}
function checkApiKey($db, $sql,$apiKey, $userID){
    $dbApi = fetchSQLReq($db, $sql["user"]["checkAPI"], array(":user_id"=>$userID), true, true );
    if ($dbApi==$apiKey){
        return true;
    }else{
        return false;
    }
}

/** Gives a string with random chars
 * @param $nbChar length of the returned string
 * @return string string with random chars
 */
function randomChar($nbChar){
    $str='abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    $shuf=str_shuffle($str);
    $random = substr($shuf, 0, 1);
    for($i = 0;$i<$nbChar-1;$i++){
        $shuf=str_shuffle($str);
        $random .= substr($shuf, 0, 1);
    }
    return $random;
}

/**
 * Sends a mail with all the given informations
 * @param $msg txt of the message
 * @param $subject subject of the mail
 * @param $to email adress of the receiver
 * @param $fromMail email adress of the sender
 * @param $fromName name of the sender to display
 * @return bool true if the mail got send
 */
function sendMail($msg, $subject, $to, $fromMail, $fromName){
    $replyMail = "noRepply";
    $replyName=null;
    $type = "html";
    if (!preg_match("#^[a-z0-9._-]+@(hotmail|live|msn).[a-z]{2,4}$#", $to)){
        $passage_ligne = "\r\n";
    } else{
        $passage_ligne = "\n";
    }
    //Header of the mail
    if($fromName!=null)$fromAdress = "\"$fromName\"<$fromMail>";
    else $fromAdress = $fromMail;
    if($replyName!=null)$replyAdress = "\"$replyName\" <$replyMail>";
    else $replyAdress = $replyMail;
    $headers = 'MIME-Version: 1.0' . $passage_ligne;
    $headers .= "Content-type: text/$type; charset=iso-8859-1" . $passage_ligne;
    $headers .= "From: $fromAdress" . $passage_ligne .
        "Reply-To: $replyAdress" . $passage_ligne .
        "X-Mailer: PHP/" . phpversion();
    $msg .= $passage_ligne.$passage_ligne.strtolower(dirname($_SERVER['SERVER_PROTOCOL'])) . "://" . $_SERVER['HTTP_HOST'] . explode("?", $_SERVER['REQUEST_URI'])[0];
    $msg = wordwrap($msg, 70, $passage_ligne);
    return mail($to,$subject,$msg, $headers);
}
function contentMail($mailType,$name, $email, $token){
    if ($mailType==1){
        $msg = "Hello";
        $title = "Inscription";
        $link = "http://app.intellifridge.ovh/inscription.php?token=$token";
        $content = "<div align='left' style='color: #474747;' class='article-content'>
                <p> Bonjour $name,</p>
                <p>Bienvenue chez IntelliFridge.</p>
                <p>
                    Nous vous remercions de vous être inscrit chez nous.
                </p>
                <p>
                    Votre adresse mail est : <a href='#'>". strip_tags($email) . "</a>.
                </p>
                <p>
                    Veuillez cliquer sur ce lien pour confirmer votre inscription :
                </p>
                <blockquote style='border-left: medium solid grey;padding-left : 10px;font-size : 100%;background-color : #d1d1d1;padding:10px;'>
                    <a href='$link'>$link</a>
                </blockquote>
                <br/>
                <p>Cordialement,</p>
                <p>L'équipe d'IntelliFridge</p>
                <br/>
            </div>";
    }else{
        $msg = "Hello";
        $title = "Mot de passe perdu";
        $link = "http://app.intellifridge.ovh/inscription.php?token=$token";
        $content = "<div align='left' style='color: #474747;' class='article-content'>
                <p> Bonjour $name,</p>
                <p>Bienvenue chez IntelliFridge.</p>
                <p>
                    Nous vous remercions de vous être inscrit chez nous.
                </p>
                <p>
                    Votre adresse mail est : <a href='#'>". strip_tags($email) . "</a>.
                </p>
                <p>
                    Veuillez cliquer sur ce lien pour confirmer votre inscription :
                </p>
                <blockquote style='border-left: medium solid grey;padding-left : 10px;font-size : 100%;background-color : #d1d1d1;padding:10px;'>
                    <a href='$link'>$link</a>
                </blockquote>
                <br/>
                <p>Cordialement,</p>
                <p>L'équipe d'IntelliFridge</p>
                <br/>
            </div>";
    }
    $hostName="http://www.intellifridge.ovh/";
    $mail = "<table width='100%' cellpadding='0' cellspacing='0' border='0' style='background-color:rgb(42, 55, 78);font-family: Georgia'>
    <tbody>
    <tr>
        <td align='center' bgcolor='#2A374E'>
            <table cellpadding='0' cellspacing='0' border='0'>
                <tbody>
                <tr>
                    <td class='w640' width='640' height='10'></td>
                </tr>
                <tr>
                    <td class='w640' width='640' height='10'></td>
                </tr>


                <!-- entete -->
                <tr class='pagetoplogo'>
                    <td class='w640' width='640'>
                        <table class='w640' width='640' cellpadding='0' cellspacing='0' border='0' bgcolor=''#d9534f'>
                            <tbody>
                            <tr>
                                <td style='text-align: center' class='w30' width='30'><a style='color:#255D5C;' href='$hostName'><img style='width: 70%' src='$hostName/view/img/logo/intelliFridge.png' alt='intellifridge logo'></a></td>
                            </tr>
                            </tbody>
                        </table>
                        <br/>
                    </td>
                </tr>

                <!-- separateur horizontal -->
                <tr>
                    <td class='w640' width='640' height='1' bgcolor='white'></td>
                </tr>

                <!-- contenu -->
                <tr class='content'>
                    <td class='w640' width='640' style='background-color: white' bgcolor=''#ffffff'>
                        <table class='w640' width='640' cellpadding='0' cellspacing='0' border='0'>
                            <tbody>
                            <tr>
                                <td class='w30' width='30'></td>
                                <td class='w580' width='580'>
                                    <!-- une zone de contenu -->
                                    <table class='w580' width='580' cellpadding='0' cellspacing='0' border='0'>
                                        <tbody>
                                        <tr>
                                            <td class='w580' width='580'>
                                                <h2 style='color:#0E7693; font-size:30px; padding-top:12px;font-family: Lato Black, sans-serif'>
                                                    $title  </h2>
                                                    $content
                                            </td>
                                        </tr>
                                        <tr>
                                            <td class='w580' width='580' height='1' bgcolor=''#c7c5c5'></td>
                                        </tr>
                                        </tbody>
                                    </table>
                                    <!-- fin zone -->


                                </td>
                                <td class='w30' width='30'></td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>

                <!--  separateur horizontal de 15px de haut -->
                <tr>
                    <td class='w640' width='640' height='15' bgcolor=''#ffffff'></td>
                </tr>

                <!-- pied de page -->
                <tr class='pagebottom'>
                    <td class='w640' width='640'>
                        <table class='w640' width='640' cellpadding='0' cellspacing='0' border='0' bgcolor=''#c7c7c7'>
                            <tbody>
                            <tr>
                                <td colspan='5' height='10'></td>
                            </tr>
                            <tr>
                                <td class='w30' width='30'></td>
                                <td class='w580' width='580' valign='top'>
                                    <p align='right' class='pagebottom-content-left'>
                                        <a style='color:#255D5C;' href='$hostName'><span style='color:white;'>Notre Site web</span></a>
                                    </p>
                                </td>

                                <td class='w30' width='30'></td>
                            </tr>
                            <tr>
                                <td colspan='5' height='10'></td>
                            </tr>
                            </tbody>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td class='w640' width='640' height='60'></td>
                </tr>
                </tbody>
            </table>
        </td>
    </tr>
    </tbody>
</table>";
    return $mail;
}

