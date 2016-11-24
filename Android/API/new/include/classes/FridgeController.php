<?php

/**
 * Created by PhpStorm.
 * User: Sofiane
 * Date: 24-11-16
 * Time: 17:08
 */
use Slim\Http\Request;
use Slim\Http\Response;
use \Firebase\JWT\JWT;



class FridgeController{
    private $db;
    private $sql;

    public function __construct($db, $sql) {
        $this->db = $db;
        $this->sql = $sql;
    }
    public function addFridge(Request $request, Response $response){
        $post = $request->getParsedBody();
        if (!empty($post['fridgeName'])&& !empty($post['fridgeType'])){
            if (sendSQLReq($this->db,$this->sql["fridge"]["add"],array(":fridgeName"=>$post["fridgeName"],":fridgeType"=>$post["fridgeType"]))){
                $responseBody = array(
                    "status"=>"200",
                    "data"=>"Frigo ".$post["fridgeName"]." added!",
                );
            }else{
                $responseBody = array(
                    "status" => "500",
                    "error" => "unknown error in sql request",
                    "data" => $post,
                );
            }
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
    }
    public function removeFridge(Request $request, Response $response){
        $post = $request->getParsedBody();
        //TODO retrieve user id from JWT TOKEN
        $id = 4; // fake id
        if (!empty($post['fridgeId'])){
            //TODO Check product in this fridge ? what are u doing with them?

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

    }
    public function listFridge(Request $request, Response $response){
        $post = $request->getParsedBody();
        //TODO retrieve user id from JWT TOKEN
        $id = 4; // fake id
        if ($id == $id){
            if (!empty($post['fridgeId'])){
                if ($products =fetchSQLReq($this->db,$this->sql['fridge']['listFridges'],array(":fridge_id"=>$post['fridgeId'], ":user_id"=>$id))){
                    $responseBody = array(
                        "status"=>"200",
                        "data"=>$products,
                    );
                }else{
                    $responseBody = array(
                        "status" => "500",
                        "error" => "unknown error in sql request",
                        "data" => $post,
                    );
                }
            }else{
                $responseBody = array(
                    "status" => "401",
                    "error" => "Insuficient arguments",
                    "data" => $post,
                );
            }
        }else{
            $responseBody = array(
                "status" => "402",
                "error" => "unauthorised",
                "data" => "You are not authorized to view this fridge",
            );
        }


        //Return "encapsulation"
        $response =$response->withHeader('content-type','pplication/json' );
        $response->getBody()->write(json_encode($responseBody));
        return $response;

    }
    /*
     * public function editFridge(Request $request, Response $response) ???
     */
    public function addProduct(Request $request, Response $response){
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
    public function addMultiProduct(Request $request, Response $response){
         $post = $request->getParsedBody();
        /*
         * TODO retrieve user id from JWT TOKEN
         */
        $id = 4; // fake id
        if (!empty($post['fridgeId'])&& !empty($post['products'])){
            $nbProducts = count($post['products']);
            for ($i = 0; $i > $nbProducts; $i++){
                /*
                 * TODO sort of verification for each product who as failed
                 */
                if (sendSQLReq($this->db,$this->sql['products']['addProduct'],array(
                    "name"=>$post['products'][$i]['name'],

                    /*
                     * TODO list elements to insert in DB
                     */
                ) )){
                 // APPEND name of failed element
                }
            }

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
    }
    public function listProductFromFridge(Request $request, Response $response){
        $post = $request->getParsedBody();
        /*
         * TODO retrieve user id from JWT TOKEN
         */        $id = 4; // fake id
        if ($id == $id){
            if (!empty($post['fridgeId'])){
                if ($products =fetchSQLReq($this->db,$this->sql['fridge']['listProductFromFridge'],array(":fridge_id"=>$post['fridgeId'], ":user_id"=>$id))){
                    $responseBody = array(
                        "status"=>"200",
                        "data"=>$products,
                    );
                }else{
                    $responseBody = array(
                        "status" => "500",
                        "error" => "unknown error in sql request",
                        "data" => $post,
                    );
                }
            }else{
                $responseBody = array(
                    "status" => "401",
                    "error" => "Insuficient arguments",
                    "data" => $post,
                );
            }
        }else {
            $responseBody = array(
                "status" => "402",
                "error" => "unauthorised",
                "data" => "You are not authorized to view this fridge",
            );
        }

        //Return "encapsulation"
        $response =$response->withHeader('content-type','pplication/json' );
        $response->getBody()->write(json_encode($responseBody));
        return $response;
    }
    public function listAllProducts(Request $request, Response $response){
        $post = $request->getParsedBody();
        /*
         * TODO retrieve user id from JWT TOKEN
         */        $id = 4; // fake id
        if ($id== $id){
            if ($allProducts= fetchSQLReq($this->db,$this->sql['products']['listAll'],array(":user_id"=>$id))){
                $responseBody = array(
                    "status" => "200",
                    "data" => $allProducts,
                );
            }else{
                $responseBody = array(
                    "status" => "500",
                    "error" => "unknown error in sql request",
                    "data" => $post,
                );
            }
        }else{
            $responseBody = array(
                "status" => "402",
                "error" => "unauthorised",
                "data" => "You are not authorized to view this fridge",
            );
        }

        //Return "encapsulation"
        $response =$response->withHeader('content-type','pplication/json' );
        $response->getBody()->write(json_encode($responseBody));
        return $response;
    }
    public function removeProduct(Request $request, Response $response){
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
    public function removeMultiProduct(Request $request, Response $response){
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
    public function editProduct(Request $request, Response $response){
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