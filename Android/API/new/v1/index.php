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
    return new UserController($c["db"], $c["sql"]);
};
$container["FridgeController"] = function($c) {
    return new FridgeController($c["db"], $c["sql"]);
};
$container["AllergyController"] = function($c) {
    return new AllergyController($c["db"], $c["sql"]);
};

/*
 * HTML GET requests handling
 */


$app->get('/hello/{name}', function (Request $request, Response $response) {
    $name = $request->getAttribute('name');
    $response->getBody()->write("Hello, $name");
    return $response;

});
$app->get('/tickets', function (Request $request, Response $response) {
    $this->logger->addInfo("Ticket list");
    $mapper = new TicketMapper($this->db);
    $tickets = $mapper->getTickets();

    $response->getBody()->write(var_export($tickets, true));
    return $response;
});


$app->group("/user",function (){
    $this->post("/login", "UserController:authenticateUser");
    $this->post("/newAccount", "UserController:createUser");
    $this->post("/editProfile", "UserController:createUser");
    $this->post("/lostPass", "UserController:createUser");
} );
$app->group("/fridges",function (){
    $this->get('/list','FridgeController:listFridge');
    $this->delete('/remove','FridgeController:removeFridge');
    $this->post('/add','FridgeController:addFridge');
} );
$app->group("/products",function (){
    $this->post("/addOne", 'FridgeController:addProduct');
    $this->post("/addMany", 'FridgeController:addMultiProduct');
    $this->get("/listFromFridge", 'FridgeController:listProductFromFridge');
    $this->get("/listAll", 'FridgeController:listAllProducts');
    //$this->get("/removeOne", 'FridgeController:listAllProducts');
    //$this->get("/removeMany", 'FridgeController:listAllProducts');
    //$this->get("/editProduct", 'FridgeController:listAllProducts');

} );

$app->post("/login", "UserController:authenticateUser");
$app->post("/newAccount", "UserController:createUser");




/*$app->post("/login", function (Request $request, Response $response){
    $response = $response->withHeader('Content-type', 'application/json');
    $response->getBody()->write(json_encode($request->getParsedBody()));
    return $response;
});
*/
/*
 * HTML POST requests handling
 */
$app->run();
