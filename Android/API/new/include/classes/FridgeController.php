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
/*if (!empty($post["apiKey"]) && !empty($post["userId"])){
    if (checkApiKey($this->db,$this->sql,$post["apiKey"],$post["userId"])){

    }
    else{
        $responseBody = array(
            "status"=>"402",
            "error" =>"API KEY invalid for this user",
        );
    }
}else{
    $responseBody = array(
        "status"=>"402",
        "error" =>"API KEY or userId not sent",
    );
}*/


class FridgeController{
    private $db;
    private $sql;
    private $logger;


    public function __construct($db, $sql,$logger) {
        $this->db = $db;
        $this->sql = $sql;
        $this->logger = $logger;

    }
    //DONE
    public function addFridge(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        if (!empty($post["ApiKey"]) && !empty($post["UserId"]) && !empty($post["FrigoNom"])){
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                if ($isPresentFridge =fetchSQLReq($this->db, $this->sql["fridge"]["exists"], array(":FrigoNom"=>$post["FrigoNom"], ":UserId"=>$post['UserId']))){
                    if ($isPresentFridge == -1){
                        if (sendSQLReq($this->db,$this->sql["fridge"]["add"],array(":FrigoNom"=>$post["FrigoNom"], ":UserId"=>$post['UserId']))){
                            $responseBody = array(
                                "status"=>"200",
                                "data"=>"Frigo ".$post["FrigoNom"]." added!",
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
                            "status" => "202",
                            "error" => "Fridge already exists",
                            "data" => $post,
                        );
                    }
                }else{
                    $responseBody = array(
                        "status" => "500",
                        "error" => "unknown error in sql request = 'is present fridge'",
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
                "status"=>"402",
                "error" =>"Missing argument",
                "data" => $post
            );
        }


        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;
    }
    //TODO TEST V+ check if fridge not empty
    public function removeFridge(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        if (!empty($post["ApiKey"]) && !empty($post["UserId"]) && !empty($post["FrigoNom"])){
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                    if (sendSQLReq($this->db,$this->sql["fridge"]["remove"],array(":FrigoNom"=>$post["FrigoNom"], ":UserId"=>$post['UserId']))){
                        $responseBody = array(
                            "status"=>"200",
                            "data"=>"Frigo ".$post["FrigoNom"]." removed!",
                        );
                    }else{
                        $responseBody = array(
                            "status" => "500",
                            "error" => "unknown error in sql request",
                            "data" => $post,
                        );
                    }

            }
            else{
                $responseBody = array(
                    "status"=>"402",
                    "error" =>"API KEY invalid for this user",
                );
            }
        }else{
            $responseBody = array(
                "status"=>"402",
                "error" =>"Missing argument",
                "data" => $post
            );
        }


        //Return "encapsulation"
        $response =$response->withHeader('content-type','pplication/json' );
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;
    }
    // DONE
    public function listFridge(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));

        if (!empty($post["ApiKey"]) && !empty($post["UserId"])){
            //$this->logger->addInfo(checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"]));
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                        if ($products=fetchSQLReq($this->db,$this->sql['fridge']['listFridges'],array(":user_id"=>$post['UserId']))){
                            if ($products==-1){
                                $responseBody = array(
                                    "status"=>"201",
                                    "warning"=>"No fridges",
                                );
                            }else{
                                $responseBody = array(
                                    "status"=>"200",
                                    "data"=>$products,
                                );
                            }

                        }else {
                            $responseBody = array(
                                "status" => "500",
                                "error" => "unknown error in sql request",
                                "data" => $post,
                            );
                        }
            }
            else{
                $responseBody = array(
                    "status"=>"402",
                    "error" =>"API KEY invalid for this user",
                );
            }
        }else{
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
    //DONE
    public function getFridgeContent(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        if (!empty($post["ApiKey"]) && !empty($post["UserId"]) && !empty($post['FrigoNom'])){
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                if ($fridgeId =fetchSQLReq($this->db,$this->sql['fridge']['getFridgeId'],array(":UserId"=>$post['UserId'],":FrigoNom"=>$post['FrigoNom']), true,true)){
                    if ($content =fetchSQLReq($this->db,$this->sql['fridge']['getFridgeContent'],array(":FrigoId"=>$fridgeId))){
                        if ($content==-1){
                            $responseBody = array(
                                "status"=>"201",
                                "warning"=>"fridge empty",
                                "data"=>$post,
                            );
                        }else{
                            $responseBody = array(
                                "status"=>"200",
                                "data"=>$content,
                            );
                        }

                    }else {
                        $test= false;
                        $responseBody = array(
                            "status" => "500",
                            "error" => "unknown error in sql request (fridge content)",
                            "data" => $post,
                        );
                    }
                }else {
                    $responseBody = array(
                        "status" => "500",
                        "error" => "unknown error in sql request for fridge ID",
                        "data" => $post,
                    );
                }
            }else{
                $responseBody = array(
                    "status"=>"402",
                    "error" =>"API KEY invalid for this user",
                    "data" => $post,
                );
            }
        }else{
            $responseBody = array(
                "status"=>"402",
                "error" =>"argument missing",
                "data" =>$post,
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;

    }
    // DONE
    public function searchById(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        if (!empty($post["ApiKey"]) && !empty($post["UserId"]) && !empty($post['ProductSId'])){
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                if ($product =fetchSQLReq($this->db,$this->sql['products']['getById'],array(":ProductSId"=>$post['ProductSId']), false,true)){
                        if ($product==-1){
                            $responseBody = array(
                                "status"=>"404",
                                "warning"=>"Product not found",
                            );
                        }else{
                            $responseBody = array(
                                "status"=>"200",
                                "data"=>$product,
                            );
                        }


                }else {
                    $responseBody = array(
                        "status" => "500",
                        "error" => "unknown error in sql request for product search",
                        "data" => $post,
                    );
                }
            }else{
                $responseBody = array(
                    "status"=>"402",
                    "error" =>"API KEY invalid for this user",
                    "data" => $post,
                );
            }
        }else{
            $responseBody = array(
                "status"=>"402",
                "error" =>"argument missing",
                "data" =>$post,
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;

    }
    // TODO TEST
    public function isInDB(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        if (!empty($post['ProduitSId'])){

            if ($content =fetchSQLReq($this->db,$this->sql['products']['isInDB'],array(":ProduitSId"=>$post['ProduitSId']),false, true)){
                if ($content==-1){
                    $responseBody = array(
                        "status"=>"201",
                        "warning"=>"No product",
                    );
                }else{
                    $responseBody = array(
                        "status"=>"200",
                        "data"=>true,
                    );
                }

            }else {
                $responseBody = array(
                    "status" => "500",
                    "error" => "unknown error in sql request",
                    "data" => $post,
                );
            }
        }else{
            $responseBody = array(
                "status" => "300",
                "error" => "Barcode not found",
            );
        }





        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;

    }
    /*
     * public function editFridge(Request $request, Response $response) ???
     */

    //Done
    public function addProduct(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));

        if (!empty($post["ApiKey"]) && !empty($post["UserId"])
            && !empty($post["ProduitSId"])
            && !empty($post["ProduitSNom"])
            && !empty($post["ProduitSMarque"])
            && !empty($post["FrigoNom"])
            && !empty($post["ProduitImageUrl"])
            && !empty($post["Contenance"])){
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                if ($fridgeId =fetchSQLReq($this->db,$this->sql['fridge']['getFridgeId'],array(":UserId"=>$post['UserId'],":FrigoNom"=>$post['FrigoNom']), true,true)){
                    if ($post['ProduitSId']<1000){
                        $produitSid =1;
                        $produitNSid =$post['ProduitSId'];
                        $isScannable =0;
                    }else{
                        $produitSid =$post['ProduitSId'];
                        $produitNSid =1;
                        $isScannable =1;
                    }
                    $date = new DateTime();
                    $dateNow = $date->format('Y-m-d');
                    if ($isProductPresent = fetchSQLReq($this->db,$this->sql['products']['existsInFridge'],array(":FrigoId"=>"$fridgeId",":ProduitSId"=>$produitSid,":ProduitNSId"=>$produitNSid),true, true)){

                        if($isProductPresent== -1){
                            if ($isScannable==1){
                                if ($isProductExists=fetchSQLReq($this->db,$this->sql['products']['isInDB'],array(":ProduitSId"=>$produitSid),true, true)){
                                    if ($isProductExists==-1){

                                        
                                        if (sendSQLReq($this->db,$this->sql['products']['newProductInfo'],array(
                                            ":ProduitSId"=>$post['ProduitSId'],
                                            ":ProduitSNom"=>$post['ProduitSNom'],
                                            ":ProduitSMarque"=>$post['ProduitSMarque'],
                                            ":Contenance"=>$post['Contenance'],
                                            ":ProduitImageUrl"=>$post['ProduitImageUrl']
                                        ))){
                                            if (sendSQLReq($this->db,$this->sql['products']['insertIntoFridge'],array(
                                                ":FrigoId"=>$fridgeId,
                                                ":ProduitNSId"=>$produitNSid,
                                                ":ProduitSId"=>$produitSid,
                                                ":isScannable"=>$isScannable,
                                                ":Quantite"=>1,
                                                ":DateAjout"=>$dateNow))){
                                                $responseBody = array(
                                                    "status"=>"200",
                                                );

                                            }else {
                                                $responseBody = array(
                                                    "status" => "500",
                                                    "error" => "unknown error in sql request Insert into fridge(product not exist)",
                                                    "data" => $post,
                                                );
                                            }

                                        }else{
                                            $responseBody = array(
                                                "status" => "500",
                                                "error" => "unknown error in sql request insertProductDB",
                                                "data" => $post,
                                            );
                                        }
                                        /*if (sendSQLReq($this->db,$this->sql['products']['insertProductDB'],$addProduct)){
                                            $this->logger->addInfo("ccoool");

                                        }else{
                                            $this->logger->addInfo("MERDE");
                                            $responseBody = array(
                                                "status" => "500",
                                                "error" => "unknown error in sql request insertProductDB",
                                                "data" => $post,
                                            );
                                        }*/
                                    }else{
                                        if (sendSQLReq($this->db,$this->sql['products']['insertIntoFridge'],array(
                                            ":FrigoId"=>$fridgeId,
                                            ":ProduitNSId"=>$produitNSid,
                                            ":ProduitSId"=>$produitSid,
                                            ":isScannable"=>$isScannable,
                                            ":Quantite"=>1,
                                            ":DateAjout"=>$dateNow))){
                                            $responseBody = array(
                                                "status"=>"200",
                                            );

                                        }else {
                                            $responseBody = array(
                                                "status" => "500",
                                                "error" => "unknown error in sql request Insert into fridge(productexists)",
                                                "data" => $post,
                                            );
                                        }
                                    }
                                }else{
                                    $responseBody = array(
                                        "status" => "500",
                                        "error" => "unknown error in sql request check if existsProduct",
                                        "data" => $post,
                                    );
                                }
                            }


                        }else{
                            if (sendSQLReq($this->db,$this->sql['products']['addIntoFridge'],array(":FrigoId"=>"$fridgeId",":ProduitNSId"=>$produitNSid,":ProduitSId"=>$produitSid,"DateAjout"=>$dateNow))){
                                $responseBody = array(
                                    "status"=>"200",
                                );

                            }else {
                                $responseBody = array(
                                    "status" => "500",
                                    "error" => "unknown error in sql request addIntoFridge",
                                    "data" => $post,
                                );
                            }
                        }
                    }else{
                        $responseBody = array(
                            "status" => "500",
                            "error" => "unknown error in sql request existsInFridge",
                            "data" => $post,
                        );
                    }
                    

                }else{
                    $responseBody = array(
                        "status" => "500",
                        "error" => "unknown error in sql for retrieving fridge ID",
                        "data" => $post,
                    );
                }

            }
            else{
                $responseBody = array(
                    "status"=>"402",
                    "error" =>"API KEY invalid for this user",
                );
            }
        }else{
            $responseBody = array(
                "status"=>"405",
                "error" =>"Missing arguments",
                "data"=>$post
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;
    }
    public function addProductNS(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));

        if (!empty($post["ApiKey"]) && !empty($post["UserId"])
            && !empty($post["ProduitSId"])
            && !empty($post["ProduitSNom"])
            && !empty($post["FrigoId"])){
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                    if ($post['ProduitSId']<1000){
                        $produitSid =1;
                        $produitNSid =$post['ProduitSId'];
                        $isScannable =0;
                    }else{
                        $produitSid =$post['ProduitSId'];
                        $produitNSid =1;
                        $isScannable =1;
                    }
                    $date = new DateTime();
                    $dateNow = $date->format('Y-m-d');
                    if ($isProductPresent = fetchSQLReq($this->db,$this->sql['products']['existsInFridge'],array(":FrigoId"=>$post['FrigoId'],":ProduitSId"=>$produitSid,":ProduitNSId"=>$produitNSid),true, true)){

                        if($isProductPresent== -1){
                            if (sendSQLReq($this->db,$this->sql['products']['insertIntoFridge'],array(":FrigoId"=>$post['FrigoId'],":isScannable"=>$isScannable,":Quantite"=>1,":ProduitNSId"=>$produitNSid,":ProduitSId"=>$produitSid,"DateAjout"=>$dateNow))){
                                $responseBody = array(
                                    "status" => "200"
                                );
                            }else{
                                $responseBody = array(
                                    "status" => "500",
                                    "error" => "unknown error in sql request",
                                    "data" => $post,
                                );
                            }


                        }else{
                            if (sendSQLReq($this->db,$this->sql['products']['addIntoFridge'],array(":FrigoId"=>$post['FrigoId'],":ProduitNSId"=>$produitNSid,":ProduitSId"=>$produitSid,"DateAjout"=>$dateNow))){
                                $responseBody = array(
                                    "status"=>"200",
                                );

                            }else {
                                $responseBody = array(
                                    "status" => "500",
                                    "error" => "unknown error in sql request addIntoFridge",
                                    "data" => $post,
                                );
                            }
                        }
                    }else{
                        $responseBody = array(
                            "status" => "500",
                            "error" => "unknown error in sql request existsInFridge",
                            "data" => $post,
                        );
                    }




            }
            else{
                $responseBody = array(
                    "status"=>"402",
                    "error" =>"API KEY invalid for this user",
                );
            }
        }else{
            $responseBody = array(
                "status"=>"405",
                "error" =>"Missing arguments",
                "data"=>$post
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;
    }
    // TODO a laisser de cote pour le moment
    public function listAllProducts(Request $request, Response $response){
        $post = checkJWT($request->getBody());
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
    public function removeOneProductFromFridge(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        if (!empty($post["ApiKey"]) && !empty($post["UserId"])
            && !empty($post["ProduitSId"] && !empty($post['FrigoId']))){
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                if ($post['ProduitSId']<1000){
                    $produitSid =1;
                    $produitNSid =$post['ProduitSId'];
                    $isScannable =0;
                }else{
                    $produitSid =$post['ProduitSId'];
                    $produitNSid =1;
                    $isScannable =1;
                }
                if ($nbProducts=fetchSQLReq($this->db,$this->sql['fridge']['getProductNbFromFridge'],array(":ProduitSId"=>$produitSid,"FrigoId"=>$post["FrigoId"],":ProduitNSId"=>$produitNSid),true,true)){
                    if ($nbProducts==0){
                        $responseBody = array(
                            "status" => "203",
                            "data" => "Product already empty",
                        );
                    }else{
                        if (sendSQLReq($this->db,$this->sql['fridge']['removeOneProductFromFridge'],array(":ProduitSId"=>$produitSid,"FrigoId"=>$post["FrigoId"],":ProduitNSId"=>$produitNSid))){
                            $responseBody = array(
                                "status"=>"200",
                                "data"=>"Remaining products :".$nbProducts-1,
                            );

                        }else {
                            $responseBody = array(
                                "status" => "500",
                                "error" => "unknown error in sql request",
                                "data" => $post,
                            );
                        }
                    }
                }else{
                    $responseBody = array(
                        "status" => "500",
                        "error" => "unknown error in sql request getProductNbFromFridge",
                        "data" => $post,
                    );
                }
            }
            else{
                $responseBody = array(
                    "status"=>"402",
                    "error" =>"API KEY invalid for this user",
                );
            }
        }else{
            $responseBody = array(
                "status"=>"405",
                "error" =>"Missing arguments",
                "data"=>$post
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;
    }
    public function removeProductFromFridge(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        if (!empty($post["ApiKey"]) && !empty($post["UserId"])
            && !empty($post["ProduitSId"] && !empty($post['FrigoId']))){
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                if ($post['ProduitSId']<1000){
                    $produitSid =1;
                    $produitNSid =$post['ProduitSId'];
                }else{
                    $produitSid =$post['ProduitSId'];
                    $produitNSid =1;
                }
                if ($nbProducts=fetchSQLReq($this->db,$this->sql['fridge']['getProductNbFromFridge'],array(":ProduitSId"=>$produitSid,"FrigoId"=>$post["FrigoId"],":ProduitNSId"=>$produitNSid),true,true)){
                    if ($nbProducts==0){
                        $responseBody = array(
                            "status" => "203",
                            "data" => "Product already empty",
                        );
                    }else{
                        if (sendSQLReq($this->db,$this->sql['fridge']['removeProductFromFridge'],array(":ProduitSId"=>$produitSid,"FrigoId"=>$post["FrigoId"],":ProduitNSId"=>$produitNSid))){
                            $responseBody = array(
                                "status"=>"200",
                            );

                        }else {
                            $responseBody = array(
                                "status" => "500",
                                "error" => "unknown error in sql request",
                                "data" => $post,
                            );
                        }
                    }
                }else{
                    $responseBody = array(
                        "status" => "500",
                        "error" => "unknown error in sql request getProductNbFromFridge",
                        "data" => $post,
                    );
                }
            }
            else{
                $responseBody = array(
                    "status"=>"402",
                    "error" =>"API KEY invalid for this user",
                );
            }
        }else{
            $responseBody = array(
                "status"=>"405",
                "error" =>"Missing arguments",
                "data"=>$post
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;
    }
    // TODO a laisser de cote
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
    // TODO a laisser
    public function searchProduct(Request $request, Response $response){
        
    }
    public function getBuyList(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"]) && !empty($post["UserId"])){
            /*
             * Check API KEY correlation check
             */
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */

                //IF REQUEST
                if ($list=fetchSQLReq($this->db,$this->sql['list']['getList'],array(":UserId"=>$post['UserId']))){
                    if ($list==-1){
                        $responseBody = array(
                            "status"=>"201",
                            "warning"=>"No List",
                        );
                    }else{
                        $responseBody = array(
                            "status"=>"200",
                            "data"=>$list,
                        );
                    }

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
    public function getFridgeName(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"]) && !empty($post["UserId"]) && !empty($post["FrigoId"])){
                /*
                 * Check API KEY correlation check
                 */
                if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */

                //IF REQUEST
                if ($fridgeName=fetchSQLReq($this->db,$this->sql['fridge']['getFridgeName'],array(":FrigoId"=>$post['FrigoId']),true,true)){
                    if ($fridgeName==-1){
                        $responseBody = array(
                            "status"=>"201",
                            "warning"=>"No fridge for this name",
                        );
                    }else{
                        $responseBody = array(
                            "status"=>"200",
                            "data"=>array("FridgeNom"=>$fridgeName),
                        );
                    }

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

    public function buyListPlusOne(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"])
            && !empty($post["UserId"])
            && !empty($post["ProduitSId"])){
                /*
                 * Check API KEY correlation check
                 */
                if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */
                if ($post['ProduitSId']<1000){
                    $produitSid =1;
                    $produitNSid =$post['ProduitSId'];
                }else{
                    $produitSid =$post['ProduitSId'];
                    $produitNSid =1;
                }
                $date = new DateTime();
                $dateNow = $date->format('Y-m-d');
                //IF REQUEST
                if (sendSQLReq($this->db,$this->sql['list']['plusOne'],array(
                    ":UserId"=>$post['UserId'],
                    ":ProduitSId"=>$produitSid,
                    ":ProduitNSId"=>$produitNSid,
                    ":ListeDateAjout"=>$dateNow,
                    ))){
                    $responseBody = array(
                        "status" => "200",
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
    public function buyListMinusOne(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"])
            && !empty($post["UserId"])
            && !empty($post["ProduitSId"])){
            /*
             * Check API KEY correlation check
             */
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */
                if ($post['ProduitSId']<1000){
                    $produitSid =1;
                    $produitNSid =$post['ProduitSId'];
                }else{
                    $produitSid =$post['ProduitSId'];
                    $produitNSid =1;
                }
                $date = new DateTime();
                $dateNow = $date->format('Y-m-d');
                if($nbProduct = fetchSQLReq($this->db,$this->sql['list']['productQuantity'],array(
                    ":UserId"=>$post['UserId'],
                    ":ProduitSId"=>$produitSid,
                    ":ProduitNSId"=>$produitNSid,
                    
                ),true, true)){
                    if ($nbProduct==0){
                        $responseBody = array(
                            "status" => "200",
                            "data" => "Product already empty"
                        );
                    }
                }else{
                    //IF REQUEST
                    if (sendSQLReq($this->db,$this->sql['list']['minusOne'],array(
                        ":UserId"=>$post['UserId'],
                        ":ProduitSId"=>$produitSid,
                        ":ProduitNSId"=>$produitNSid,
                        ":nbQuantiteInf"=>$post['nbQuantiteInf'],
                        ":ListeNote"=>$post['ListeNote'],
                        ":ListeDateAjout"=>$dateNow,
                    ))){
                        $responseBody = array(
                            "status" => "200",
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
    public function buyListRemoveAll(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"])
            && !empty($post["UserId"])){
            /*
             * Check API KEY correlation check
             */
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */
                if (sendSQLReq($this->db,$this->sql['list']['removeAll'],array(
                    ":UserId"=>$post['UserId'],
                ))){
                    $responseBody = array(
                        "status" => "200",
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

    public function setListQuantity(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"])
            && !empty($post["UserId"])
            && !empty($post["ProduitSId"])
            && isset($post["ListeQuantite"])){
                /*
                 * Check API KEY correlation check
                 */
                if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */
                    if ($post['ProduitSId']<1000){
                        $produitSid =1;
                        $produitNSid =$post['ProduitSId'];
                    }else{
                        $produitSid =$post['ProduitSId'];
                        $produitNSid =1;
                    }
                //IF REQUEST
                if ($post["ListeQuantite"]==-1){
                    if (sendSQLReq($this->db,$this->sql['list']['removeProduct'],array(
                        ":UserId"=>$post['UserId'],
                        "ProduitSId"=>$produitSid,
                        "ProduitNSId"=>$produitNSid
                    ))){
                        $responseBody = array(
                            "status" => "200",
                        );

                    }else{
                        $responseBody = array(
                            "status" => "500",
                            "error" => "unknown error in sql delete product from list",
                            "data" => $post,
                        );
                    }
                }else{
                    if (sendSQLReq($this->db,$this->sql['list']['setQuantity'],array(
                        ":ListeQuantite"=>$post['ListeQuantite'],
                        ":UserId"=>$post['UserId'],
                        ":ProduitSId"=>$produitSid,
                        ":ProduitNSId"=>$produitNSid))){

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
                "error" =>"Missing argument",
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;

    }
    public function setListNote(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"])
            && !empty($post["UserId"])
            && !empty($post["ProduitSId"])
            && !empty($post["ListeNote"])){
                /*
                 * Check API KEY correlation check
                 */
                if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */
                    if ($post['ProduitSId']<1000){
                        $produitSid =1;
                        $produitNSid =$post['ProduitSId'];
                    }else{
                        $produitSid =$post['ProduitSId'];
                        $produitNSid =1;
                    }
                //IF REQUEST
                if (sendSQLReq($this->db,$this->sql['list']['setNote'],array(
                    ":ListeNote"=>$post['ListeNote'],
                    ":UserId"=>$post['UserId'],
                    ":ProduitSId"=>$produitSid,
                    ":ProduitNSId"=>$produitNSid))){

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
                "error" =>"Missing arguments",
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;

    }
    public function addListProduct(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        if (!empty($post["ApiKey"]) && !empty($post["UserId"])
            && !empty($post["ProduitSId"])
            && !empty($post["ListeNote"])
            && !empty($post['Quantite'])){
            if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                if ($post['ProduitSId']<1000){
                    $produitSid =1;
                    $produitNSid =$post['ProduitSId'];
                    $isScannable =0;
                }else{
                    $produitSid =$post['ProduitSId'];
                    $produitNSid =1;
                    $isScannable =1;
                }
                $date = new DateTime();
                $dateNow = $date->format('Y-m-d');
                if (sendSQLReq($this->db,$this->sql['list']['insert'],array(
                    ":UserId"=>$post['UserId'],
                    ":ProduitSId"=>$produitSid,
                    ":ProduitNSId"=>$produitNSid,
                    ":IsScannable"=>$isScannable,
                    ":ListeQuantite"=>$post['Quantite'],
                    ":ListeNote"=>$post['ListeNote'],
                    ":ListeDateAjout"=>$dateNow,
                ))){
                    $responseBody = array(
                        "status"=>"200",
                    );
                }else{
                    $responseBody = array(
                        "status"=>"500",
                        "error" =>"Unknown error in sql request list/addnewproduct",
                    );
                }

            }
            else{
                $responseBody = array(
                    "status"=>"402",
                    "error" =>"API KEY invalid for this user",
                );
            }
        }else{
            $responseBody = array(
                "status"=>"405",
                "error" =>"Missing arguments",
                "data"=>$post
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;
    }

    public function getProductNS(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"]) && !empty($post["UserId"]) && is_int($post["offset"])){
                /*
                 * Check API KEY correlation check
                 */
                if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */
                    switch (intval($post['offset'])){
                        case 0: $req = $this->sql['products']['listNS0'];
                            break;
                        case 1: $req = $this->sql['products']['listNS1'];
                            break;
                        case 2: $req = $this->sql['products']['listNS2'];
                            break;
                        case 3: $req = $this->sql['products']['listNS3'];
                            break;
                        case 4: $req = $this->sql['products']['listNS4'];
                            break;
                        case 5: $req = $this->sql['products']['listNS5'];
                            break;
                        case 6: $req = $this->sql['products']['listNS6'];
                            break;
                        case 7: $req = $this->sql['products']['listNS7'];
                            break;
                        case 8: $req = $this->sql['products']['listNS8'];
                            break;
                        case 9: $req = $this->sql['products']['listNS9'];
                            break;
                        default: $req = $this->sql['products']['listNS0'];
                    }
                    //IF REQUEST
                    if ($products=fetchSQLReq($this->db,$req)){
                        if ($products==-1){
                            $responseBody = array(
                                "status"=>"201",
                                "warning"=>"No products",
                            );
                        }else{
                            $responseBody = array(
                                "status"=>"200",
                                "data"=>$products,
                            );
                        }

                    }else {
                        /*
                         * GENERIC SQL REQUEST ERROR
                         */
                        $responseBody = array(
                            "status" => "500",
                            "error" => "unknown error in sql requesttt",
                            "data" => $post,
                        );
                    }
            }else{
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
                "error" =>"missing arguments",
                "data"=>$post,
            );
        }


        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;

    }
    public function listNS(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"]) && !empty($post["UserId"])){
                /*
                 * Check API KEY correlation check
                 */
                if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */

                //IF REQUEST
                if ($products=fetchSQLReq($this->db,$this->sql['products']['listNS'])){
                    if ($products==-1){
                        $responseBody = array(
                            "status"=>"201",
                            "warning"=>"No products",
                        );
                    }else{
                        $responseBody = array(
                            "status"=>"200",
                            "data"=>$products,
                        );
                    }

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
                "error" =>"Missing arguments",
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;

    }
    public function plusOneProduct(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"]) && !empty($post["UserId"]) && !empty($post["ProduitSId"]) && !empty($post["FrigoId"])){
                /*
                 * Check API KEY correlation check
                 */
                if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */
                    if ($post['ProduitSId']<1000){
                        $produitSid =1;
                        $produitNSid =$post['ProduitSId'];
                    }else{
                        $produitSid =$post['ProduitSId'];
                        $produitNSid =1;
                    }
                    $date = new DateTime();
                    $dateNow = $date->format('Y-m-d');
                //IF REQUEST

                if (sendSQLReq($this->db,$this->sql['products']['addIntoFridge'],array(
                    ":FrigoId"=>$post['FrigoId'],
                    ":ProduitSId"=>$produitSid,
                    ":ProduitNSId"=>$produitNSid,
                    ":DateAjout"=>$dateNow))){

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
                "error" =>"Missing arguments",
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;

    }
    public function minusOneProduct(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"]) && !empty($post["UserId"]) && !empty($post["ProduitSId"]) && !empty($post["FrigoId"])){
                /*
                 * Check API KEY correlation check
                 */
                if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */
                    if ($post['ProduitSId']<1000){
                        $produitSid =1;
                        $produitNSid =$post['ProduitSId'];
                    }else{
                        $produitSid =$post['ProduitSId'];
                        $produitNSid =1;
                    }
                //IF REQUEST
                    $date = new DateTime();
                    $dateNow = $date->format('Y-m-d');
                if (sendSQLReq($this->db,$this->sql['products']['minusOneProduct'],array(
                    ":FrigoId"=>$post['FrigoId'],
                    ":ProduitSId"=>$produitSid,
                    ":ProduitNSId"=>$produitNSid,
                    ":DateAjout"=>$dateNow))){

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
                "error" =>"Missing arguments",
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;

    }
    public function getRecentProduct(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"]) && !empty($post["UserId"])){
                /*
                 * Check API KEY correlation check
                 */
                if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */

                //IF REQUEST
                if ($products=fetchSQLReq($this->db,$this->sql['products']['getRecent'], array(":UserId"=>$post['UserId']))){
                    if ($products==-1){
                        $responseBody = array(
                            "status"=>"201",
                            "warning"=>"No fridges",
                        );
                    }else{
                        $responseBody = array(
                            "status"=>"200",
                            "data"=>$products,
                        );
                    }

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
                "error" =>"Missing arguments",
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;

    }
    public function getProductSInfo(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"]) && !empty($post["UserId"]) && !empty($post["ProduitSId"])){
                /*
                 * Check API KEY correlation check
                 */
                if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */

                //IF REQUEST
                if ($products=fetchSQLReq($this->db,$this->sql['products']['getInfo'],array(":ProduitSId"=>$post['ProduitSId']))){
                    if ($products==-1){
                        $responseBody = array(
                            "status"=>"201",
                            "warning"=>"No products",
                        );
                    }else{
                        $responseBody = array(
                            "status"=>"200",
                            "data"=>$products,
                        );
                    }

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
                "error" =>"Missing arguments",
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;

    }
    public function template(Request $request, Response $response){
        $post = checkJWT($request->getParam("jwt"));
        /*
         * API KEY presence verification
         */
        if (!empty($post["ApiKey"]) && !empty($post["UserId"])){
                /*
                 * Check API KEY correlation check
                 */
                if (checkApiKey($this->db,$this->sql,$post["ApiKey"],$post["UserId"])){
                /*
                 * SENDING SQL REQUEST
                 */

                //IF REQUEST
                if ($products=fetchSQLReq($this->db,$this->sql['fridge']['listFridges'],array(":user_id"=>$post['UserId']))){
                    if ($products==-1){
                        $responseBody = array(
                            "status"=>"201",
                            "warning"=>"No fridges",
                        );
                    }else{
                        $responseBody = array(
                            "status"=>"200",
                            "data"=>$products,
                        );
                    }

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
                "error" =>"Missing arguments",
            );
        }



        //Return "encapsulation"
        $response = $response->withHeader('Access-Control-Allow-Origin', '*');
        $response->getBody()->write(encodeJWT($responseBody));
        return $response;

    }
}