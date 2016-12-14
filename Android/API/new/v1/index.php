<?php
/**
 * Created by PhpStorm.
 * User: Sofiane
 * Date: 15-11-16
 * Time: 23:05
 */
use \Psr\Http\Message\ServerRequestInterface as Request;
use \Psr\Http\Message\ResponseInterface as Response;
use \Slim\App;
use \Firebase\JWT\JWT;


require '../vendor/autoload.php';

require "../include/classes/UserController.php";
require "../include/classes/FridgeController.php";
require "../include/classes/AllergyController.php";
require '../include/Config/Config.php';
require '../include/functions.php';


/*
 * Slim Framework initialization
 */
$app = new \Slim\App(["settings" => $config]);
$container = $app->getContainer();


/*
 * All the SQL queries are in this variable. For sql queries, see sqlRequests.json
 */
$sql = json_decode(file_get_contents("../include/Config/sqlRequests.json"), true);
$container["sql"] = $sql;

/*
 * Db credidentials set in Slim container, retrieved from config.ini.php
 */
$container['db'] = function ($c) {
    $db = $c['settings']['db'];
    return connectDB($db['host'], $db['dbname'], $db['user'], $db['pass']);
};


/*
 * Logger
 */
$container['logger'] = function($c) {
    $logger = new \Monolog\Logger('my_logger');
    $file_handler = new \Monolog\Handler\StreamHandler("../logs/app.log");
    $logger->pushHandler($file_handler);
    return $logger;
};

$container["UserController"] = function($c) {
    return new UserController($c["db"], $c["sql"], $c["logger"]);
};
$container["FridgeController"] = function($c) {
    return new FridgeController($c["db"], $c["sql"],$c["logger"]);
};
$container["AllergyController"] = function($c) {
    return new AllergyController($c["db"], $c["sql"]);
};

/*


$app->get('/hello/{name}', function (Request $request, Response $response) {
    $name = $request->getAttribute('name');
    //$response->getBody()->write("Hello, $name");
    $this->logger->addDebug("dededef");
    $msg=contentMail(1,"sofiane","ayoutesofiane@gmail.com", "dflkjnfdjnjdsfnfs");
    $response->getBody()->write(sendMail("test","inscription","ayoutesofiane@gmail.com","noreply@intellifridge.ovh","Intelli"));

    return $response;

});

/*
 * Define each route 
 */

$app->group("/user",function (){
    $this->post("/login", "UserController:authenticateUser");
    $this->post("/register", "UserController:createUser");
    $this->post("/editProfile", "UserController:editProfile");
    $this->post("/lostPass", "UserController:createUser");
} );
$app->group("/fridges",function (){
    $this->get("/list","FridgeController:listFridge");
    $this->post("/remove","FridgeController:removeFridge");
    $this->post('/add','FridgeController:addFridge');
    $this->post('/getFridgeContent','FridgeController:getFridgeContent');
    $this->get('/getName','FridgeController:getFridgeName');
    $this->post('/plusOneProduct','FridgeController:plusOneProduct');
    $this->post('/minusOneProduct','FridgeController:minusOneProduct');
} );
$app->group("/products",function (){
    $this->post("/add", 'FridgeController:addProduct');
    $this->post("/addNS", 'FridgeController:addProductNS');
    $this->get("/listAll", 'FridgeController:listAllProducts');
    $this->get("/searchById", 'FridgeController:searchById');
    $this->post("/isInDB", 'FridgeController:isInDB');
    $this->post("/removeOneFromFridge", 'FridgeController:removeOneProductFromFridge');
    $this->post("/removeFromFridge", 'FridgeController:removeProductFromFridge');
    $this->post("/listNS", 'FridgeController:listNS');
    $this->post("/getProductNS",'FridgeController:getProductNS');
    $this->post("/getProductSInfo",'FridgeController:getProductSInfo');
    $this->post("/getRecentProduct",'FridgeController:getRecentProduct');
    //$this->get("/removeMany", 'FridgeController:listAllProducts');
    //$this->get("/editProduct", 'FridgeController:listAllProducts');
} );
$app->group("/allergy",function (){
    $this->get("/get",'AllergyController:getAllergies');
    $this->post("/set",'AllergyController:setAllergies');
    $this->get("/check",'AllergyController:checkAllergies');
});
$app->group("/list",function (){
    $this->get("/get",'FridgeController:getBuyList');
    $this->post("/removeAll",'FridgeController:buyListRemoveAll');
    $this->post("/plusOne",'FridgeController:buyListPlusOne');
    $this->post("/minusOne",'FridgeController:buyListMinusOne');
    $this->post("/setQuantity",'FridgeController:setListQuantity');
    $this->post("/setNote",'FridgeController:setListNote');
    $this->post("/addProduct",'FridgeController:addListProduct');
});

$app->get("/test",function (Request $request, Response $response){
    $encoded = $request->getBody();
    $this->logger->addInfo("TEST :".$encoded);
});


$app->run();
