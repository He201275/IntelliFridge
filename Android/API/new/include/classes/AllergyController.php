<?php

/**
 * Created by PhpStorm.
 * User: Sofiane
 * Date: 24-11-16
 * Time: 19:38
 */
use Slim\Http\Request;
use Slim\Http\Response;
use \Firebase\JWT\JWT;

class AllergyController
{
    private $db;
    private $sql;

    public function __construct($db, $sql) {
        $this->db = $db;
        $this->sql = $sql;
    }

    public function getAllergies(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        if (!empty($post["ApiKey"]) && !empty($post["UserId"])
            && !empty($post["ProduitSId"])){
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                    if ($allergy =fetchSQLReq($this->db,$this->sql['allergy']['getAllergyFromProductId'],array(":ProduitSId"=>$post["ProduitSId"]))){
                        $responseBody = array(
                            "status"=>"200",
                            "data"=>$allergy
                        );

                    }else {
                        $responseBody = array(
                            "status" => "500",
                            "error" => "unknown error in sql request",
                            "data" => $post,
                        );
                    }


            }else{
                $responseBody = array(
                    "status"=>"402",
                    "error" =>"API KEY invalid for this user",
                );
            }
        }else{
            $responseBody = array(
                "status"=>"403",
                "error" =>"Missing arguments",
                "data"=>$post
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;
    }
    public function setAllergies(Request $request, Response $response){
        /*$post = $request->getParsedBody();
       //TODO retrieve user id from JWT TOKEN
       $id = 4; // fake id
       if (!empty($post['fridgeId'])){

       }else{
           $responseBody = array(
               "status" => "401",
               "error" => "Insuficient arguments",
               "data" => $post,
           );
       }

       //Return "encapsulation"
       $response =$response->withHeader('content-type','pplication/json' );
       $response->getBody()->write(json_encode($responseBody));
       return $response;
*/
    }
     public function checkAllergies(Request $request, Response $response){
        /*$post = $request->getParsedBody();
       //TODO retrieve user id from JWT TOKEN
       $id = 4; // fake id
       if (!empty($post['fridgeId'])){

       }else{
           $responseBody = array(
               "status" => "401",
               "error" => "Insuficient arguments",
               "data" => $post,
           );
       }

       //Return "encapsulation"
       $response =$response->withHeader('content-type','pplication/json' );
       $response->getBody()->write(json_encode($responseBody));
       return $response;
*/
    }
    
}