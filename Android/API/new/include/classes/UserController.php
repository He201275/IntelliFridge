<?php
use Slim\Http\Request;
use Slim\Http\Response;
use \Firebase\JWT\JWT;

/**
 * Created by PhpStorm.
 * User: Sofiane
 * Date: 17-11-16
 * Time: 16:28
 */
class userController{
    private $db;
    private $sql;
    private $logger;

    public function __construct($db, $sql,$logger) {
        $this->db = $db;
        $this->sql = $sql;
        $this->logger = $logger;

    }
//http://flaven.fr/2014/11/slim-api-framework-les-bonnes-pratiques-pour-creer-une-api-avec-le-micro-framework-php-slim/

    public function lostPassword(Request $request, Response $response){
        $post = checkJWT($request->getBody());
        if (!empty($post['email'])){
            if ($id = fetchSQLReq($this->db, $this->sql["user"]["idFromLogin"],array(":user_email" => $post['email']),true, true)){
                $resetToken = randomChar(25);
                $date = new DateTime();
                $int = new DateInterval('P0Y2DT0H0M');
                $date->add($int);
                $exp = $date->format('Y-m-d');
                if (sendSQLReq($this->db,$this->sql["user"]["setResetToken"],array(":user_id"=>$id,":token"=>$resetToken, ":exp_date"=>$exp))){
                    //TODO Send mail @Rémy
                    //sendMail($mail,$title ,$email,"noreply@intellifridge.ovh", $name);
                    $response = $response->withStatus(201);
                    $responseBody = array(
                        "status"=>"200",
                        "data"=>"Reset password successful",
                    );
                }else{
                    $this->
                    $response = $response->withStatus(422);
                    $responseBody = array(
                        "status" => "422",
                        "error" => "unknown error in sql request",
                        "data" => $post,
                    );

                }
            }else {
                $responseBody = array(
                    "status" => "404",
                    "error" => "eMail :\"".$post['email']."\" not found",
                );
            }
        }else{
            $responseBody = array(
                "status" => "401",
                "error" => "Insufficient arguments",
                "data" => $post,
            );
        }
        $response = $response->withHeader('Content-type', 'text/plain');
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;
        

    }
    public function createUser(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));

        if (!empty($post['UserAdresseMail']) && !empty($post['UserPassword']) && !empty($post['UserNom'])
            && !empty($post['UserPrenom']) && !empty($post['LangueCode'])){ //&& !empty($post['UserGenre'])
            $apiKey = randomChar(15);
            $email = htmlspecialchars($post['UserAdresseMail']);
            $password = htmlspecialchars($post['UserPassword']);
            $lastName = htmlspecialchars($post['UserNom']);
            $name = htmlspecialchars($post['UserPrenom']);
            $locale = htmlspecialchars($post['LangueCode']);
            //$gender = htmlspecialchars($post['UserGenre']);
            $mailAlreadyInDB = fetchSQLReq($this->db, $this->sql['user']['idFromLogin'], array("user_email"=>$email),true, true);
            if ($mailAlreadyInDB > 0){
                $returnBody = array(
                    "status" => "401",
                    "error" => "Email already in database",
                );
            }else{
                if (!empty($post['CommuneCode']) && !empty($post['CommuneLocalite'])){
                    $CommuneCode = htmlspecialchars($post['CommuneCode']);
                    $CommuneLocalite = htmlspecialchars($post['CommuneLocalite']);
                    $sqlInsert = sendSQLReq($this->db, $this->sql["user"]["createWithLocale"],array(
                        ":UserAdresseMail" => $email,
                        ":UserPassword" => $password,
                        ":UserNom" => $lastName,
                        ":UserPrenom" => $name,
                        ":LangueCode" => $locale,
                        ":CommuneCode" => $CommuneCode,
                        ":CommuneLocalite" => $CommuneLocalite,
                        ":ApiKey"=>$apiKey,
                    ));
                }else{
                    $sqlInsert = sendSQLReq($this->db, $this->sql["user"]["create"],array(
                        ":UserAdresseMail" => $email,
                        ":UserPassword" => $password,
                        ":UserNom" => $lastName,
                        ":UserPrenom" => $name,
                        ":LangueCode" => $locale,
                        ":ApiKey"=>$apiKey,
                    ));
                }
                if ($sqlInsert){
                    $this->logger->addInfo("mail :".$email);

                    $activationToken = randomChar(15);
                    $user_id = fetchSQLReq($this->db,$this->sql['user']['idFromLogin'],array(":user_email"=>$email),true,true);
                    $this->logger->addInfo("user id:'".$user_id."'");

                    if ($user_id >0){
                        $date = new DateTime();
                        $int = new DateInterval('P0Y2DT0H0M');
                        $date->add($int);
                        $expDate = $date->format('Y-m-d');
                        if (sendSQLReq($this->db,$this->sql['user']['accountActivation'],array(":token"=> $activationToken,"exp_date"=>$expDate, "user_id"=>$user_id) )){
                            $this->logger->addInfo("mail Send test at :'".$email."'");
                            //$msg=contentMail(1,"sofiane","ayoutesofiane@gmail.com", "dflkjnfdjnjdsfnfs");
                            //$var = sendMail("test", "inscription",$email, "noreply@intellifridge.ovh", "IntelliFridge");

                            //$this->logger->addInfo("dsjnjsndjsdds".$var);

                            //sendMail("Inscription", $email, "noreply@intellifridge.ovh", "IntelliFridge", 1, $activationToken);
                        }else{
                            // TODO Activation Key creation went wrong
                        }
                    }else{
                        // TODO user not created error
                    }


                    $userId = $this->userPasswordVerify($email, $password);
                    if ($userId >= 0){
                        $returnBody = array(
                            "status" => "200",
                        );
                        // User verified

                    }else{
                        // return error user password not recognized due to account creation went wrong
                    }

                }else{
                    $returnBody = array(
                        "status" => "401",
                        "error" => "Creation in SQL went wrong",
                        "data" => $post,
                    );
                }
            }


        } else{
            $returnBody = array(
                "status" => "401",
                "error" => "Insuficient arguments",
                "data" => $post,
            );
        }
        $response = $response->withHeader('Content-type', 'text/plain');
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($returnBody));
        return $response;
    }

    public function authenticateUser (Request $request, Response $response) {
        $post = checkJWT($request->getParam("jwt"));
       /* $response->getBody()->write($post['login']);
        return $response;*/
        if ($post){
            if (!empty($post['UserAdresseMail']) && !empty($post['UserPassword'])) {
                $userId = $this->userPasswordVerify($post['UserAdresseMail'], $post['UserPassword']);
                if ($userId >= 0) {
                    $userInfos = fetchSQLReq($this->db, $this->sql["user"]["basicInfosById"],array(":user_id" => $userId), false, true);
                    /*$payload = array(
                        "iss" => "http://api.intellifridge.ovh",
                        "iat" => time(),
                        "nbf" => time()+ (120 * 60),
                        "data" => [
                            "userId" => $userInfos["UserId"],
                            "username" => $userInfos["UserPrenom"],
                            "userlastname" => $userInfos["UserNom"],
                            "usermail" => $userInfos["UserAdresseMail"],
                            "userlang" => $userInfos["UserLangue"],
                        ]
                    );
                    $key = "putain";

                    $jwt = JWT::encode($payload, $key, "HS256");
                    */
                    $responseBody = array(
                        "status" => "200",
                        "data" => [
                            "UserId" => $userInfos["UserId"],
                            "UserPrenom" => $userInfos["UserPrenom"],
                            "UserNom" => $userInfos["UserNom"],
                            "UserAdresseMail" => $userInfos["UserAdresseMail"],
                            "LangueCode" => $userInfos["LangueCode"],
                            "ApiKey" => $userInfos["ApiKey"],
                            "CommuneLocalite" => $userInfos["CommuneLocalite"],
                            "UserGenre" => $userInfos["UserGenre"],
                            "LangueCode" => $userInfos["LangueCode"],
                            "ApiKey" => $userInfos["ApiKey"],
                        ]
                    );


                } else {
                    if ($userId == -1){
                        $responseBody = array(
                            "status" => "401",
                            "error" => "user does not exists",
                            "data" => $post,
                        );
                    }elseif ($userId == -2){
                        $responseBody = array(
                            "status" => "500",
                            "error" => "password is incorrect",
                            "data" => $post,
                        );
                    }
                }
            } else {
                //$response = $response->withStatus(400);
                $responseBody = array(
                    "status" => "500",
                    "error" => "password is incorrect",
                    "data" => $post,
                );
                //$response->getBody()->write(json_encode(array("error" => "MissingParameter")));


            }
        }else{
            $responseBody = array(
                "status" => "500",
                "error" => "JSON TOKEN INCORRECT",
                "data" => $post,
            );
        }

        $response->getBody()->write(encodeJWT($responseBody));
        $response = $response->withHeader('Content-type', 'text/plain');
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        return $response;
    }
    private function userPasswordVerify($login="", $password="") {
        $login = filter_var($login, FILTER_SANITIZE_STRING, FILTER_FLAG_NO_ENCODE_QUOTES);
        $password = filter_var($password, FILTER_SANITIZE_STRING, FILTER_FLAG_NO_ENCODE_QUOTES);
        $dbPassword = fetchSQLReq($this->db, $this->sql["user"]["passwordByLogin"], array(":user_email" => $login), true, true);

        if (!$dbPassword) {
            //user do not exist

            return -1;
        }
        if ($password == $dbPassword) {
            //} elseif (password_verify($password, $dbPassword)) {
            // When passwords will be hashed

            $err =fetchSQLReq($this->db, $this->sql["user"]["idFromLogin"], array(":user_email" => $login), true, true);
            return $err;
        }else{
            return -2;
        }
    }
    public function editProfile(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"])
            && !empty($post["UserId"])
            && !empty($post["UserPrenom"])
            && !empty($post["UserNom"])
            && !empty($post["CommuneLocalite"])
            && !empty($post["UserAdresseMail"])){
            /*
             * Check API KEY correlation check
             */
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */

                //IF REQUEST
                if (sendSQLReq($this->db,$this->sql['user']['editProfile'],array(
                    ":UserId"=>$post['UserId'],
                    ":UserPrenom"=>$post['UserPrenom'],
                    ":UserNom"=>$post['UserNom'],
                    ":CommuneLocalite"=>$post['CommuneLocalite'],
                    ":UserAdresseMail"=>$post['UserAdresseMail']))){

                    $responseBody = array(
                        "status"=>"200",
                    );

                }else {
                    /*
                     * GENERIC SQL REQUEST ERROR
                     */
                    $responseBody = array(
                        "status" => "500",
                        "error" => "unknown error in sql request",
                        "data" => $post,
                    );
                }
            }
            else{
                /*
                 * API KEY ERROR
                 */
                $responseBody = array(
                    "status"=>"402",
                    "error" =>"API KEY invalid for this user",
                );
            }
        }else{
            /*
             * API KEY MISSING
             */
            $responseBody = array(
                "status"=>"403",
                "error" =>"API KEY or userId not sent",
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;

    }


}