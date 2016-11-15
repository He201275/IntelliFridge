<?php
class Config{
    public $name;
    public $defaultName = "conf.ini.php";
    public $data;
    function __construct($nom = ""){
        if($nom == "")$this->name = $this->defaultName;
        $this->load();
    }
    function load(){
        $this->data=parse_ini_file($this->name, true);
    }
    public function getData($g, $n){
        if(isset($this->data[$g][$n])) return $this->data[$g][$n];
        else return "[$g][$n] inconnu";
    }

}
function dbConnect(){//ajouter un config.ini avec connexion bdd
    $conf = new Config();
    $host = $conf->getData("db", "host");
    $user = $conf->getData("db", "user");
    $pwd = $conf->getData("db", "pwd");
    $dbName = $conf->getData("db", "dbname");
    try{
        return new PDO("mysql:host=$host;dbname=$dbName", "$user", "$pwd",array(PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION));
    }catch (PDOException $e) {
        return $e;
    }
}
