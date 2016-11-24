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

    public function __construct($db, $sql) {
        $this->db = $db;
        $this->sql = $sql;
    }
//http://flaven.fr/2014/11/slim-api-framework-les-bonnes-pratiques-pour-creer-une-api-avec-le-micro-framework-php-slim/
    public function lostPassword(Request $request, Response $response){
        $post = $request->getParsedBody();
        if (!empty($post['email'])){
            if ($id = fetchSQLReq($this->db, $this->sql["user"]["idFromLogin"],array(":user_email" => $post['email']),true, true)){
                // TODO Random string
                $resetToken = random_bytes(10);
                if (sendSQLReq($this->db,$this->sql["user"]["setResetToken"],array(":user_id"=>$id,":token"=>$resetToken))){
                    //TODO Send mail @Rémy
                    $response = $response->withStatus(201);
                    $responseBody = array(
                        "status"=>"200",
                        "data"=>"Reset password successful",
                    );
                }else{
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
                "error" => "Insuficient arguments",
                "data" => $post,
            );
        }
        $response = $response->withHeader('Content-type', 'application/json');
        $response->getBody()->write(json_encode($responseBody));
        return $response;
        

    }
    public function createUser(Request $request, Response $response){
        $post = $request->getParsedBody();

        if (!empty($post['email']) && !empty($post['password']) && !empty($post['lastName'])
            && !empty($post['name']) && !empty($post['locale']) && !empty($post['gender']) && !empty($post['language'])
            && !empty($post['password'])){
                $email = htmlspecialchars($post['email']);
                $password = htmlspecialchars($post['password']);
                $lastName = htmlspecialchars($post['lastName']);
                $name = htmlspecialchars($post['name']);
                $locale = htmlspecialchars($post['locale']);
                $gender = htmlspecialchars($post['gender']);
                $language = htmlspecialchars($post['language']);
            if (sendSQLReq($this->db, $this->sql["user"]["create"],array(
                ":email" => $email,
                ":password" => $password,
                ":lastName" => $lastName,
                ":name" => $name,
                ":locale" => $locale,
                ":gender" => $gender,
                ":language" => $language,
            ))){
                $userId = $this->userPasswordVerify($email, $password);
                if ($userId){
                    // User verified

                }else{
                    // return error user password not recognized due to account creation went wrong
                }
                
            }

        }
        else
            $returnBody = array(
                "status" => "401",
                "error" => "Insuficient arguments",
                "data" => $post,
            );
        $response->getBody()->write(json_encode($returnBody));
        return $response;
    }

    public function authenticateUser (Request $request, Response $response) {
        $post = $request->getParsedBody();

        if (!empty($post['login']) && !empty($post['password'])) {

            if ($userId = $this->userPasswordVerify($post['login'], $post['password'])) {
                $userInfos = fetchSQLReq($this->db, $this->sql["user"]["basicInfosById"],array(":user_id" => $userId), false, true);
                $payload = array(
                    "iss" => "https://intellifridge.ovh",
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

                $responseBody = array(
                    "Authorization"=>"Bearer ".$jwt,
                    "userId" => $userInfos["UserId"],
                    "username" => $userInfos["UserPrenom"]." ".$userInfos["UserNom"],
                    "test" => $userInfos,
                );
                $response->getBody()->write(json_encode($responseBody));
                $response = $response->withHeader('Content-type', 'application/json');
            } else {
                $response = $response->withStatus(422);
                $responseBody = array(
                    "Authorization"=>"Bearer ",
                    "userId" => "ee",
                    "username" => "rdjei",
                );
                $response->getBody()->write(json_encode($responseBody));
            }
        } else {
            $response = $response->withStatus(400);
            //$response->getBody()->write(json_encode(array("error" => "MissingParameter")));
            $response->getBody()->write(json_encode($post));

        }

        return $response;
    }
    private function userPasswordVerify($login="", $password="") {
        $login = filter_var($login, FILTER_SANITIZE_STRING, FILTER_FLAG_NO_ENCODE_QUOTES);
        $password = filter_var($password, FILTER_SANITIZE_STRING, FILTER_FLAG_NO_ENCODE_QUOTES);

        $dbPassword = fetchSQLReq($this->db, $this->sql["user"]["passwordByLogin"], array(":user_email" => $login), true, true);
        if (!$dbPassword) {
            // User does not exist
            // TODO Response user not exist 

            return false;
        } elseif ($password == $dbPassword) {
            //} elseif (password_verify($password, $dbPassword)) {
            // When passwords will be hashed
            return fetchSQLReq($this->db, $this->sql["user"]["idFromLogin"], array(":user_email" => $login), true, true);
        } else {
            return false;
        }
    }

}