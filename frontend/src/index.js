/* eslint-disable */
import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { Router, Route, Link, browserHistory } from 'react-router';
import SkyLight from 'react-skylight'; 
import logo from './logo.svg';
import $ from "jquery";
import jwtDecode from "jwt-decode";
import jwt from "jsonwebtoken";
/**
 * Bouton déconnexion
 */
class TopHome extends Component {
	render() {
		return (
			<div className="top">
				<a href="deconnect.php" className="logout"><i className="fa fa-sign-out" aria-hidden="true"></i></a>
				<div id="logo">
					<img src={logo}/>
				</div>
			</div>
		);
	}
}

class TopPages extends Component {
	render() {
		return (
			<div className="top">
				<a href="deconnect.php" className="logout"><i className="fa fa-sign-out" aria-hidden="true"></i></a>
				<div id="logo"><img src="./assets/images/dark-red/logo.svg"/></div>
				<div id="home">
					<a href="/">
						<img src="./assets/images/dark-red/home.svg"/>
					</a>
				</div>
			</div>
		);
	}
}

class Left extends Component {
	constructor(props){
		super(props);
	}

	render() {

		var itemsInOutPopupStyle = {
			backgroundColor: '#d6d6d6',
			borderRadius: '20px',
			boxShadow: 'inset 0 -5px #ff3131, inset 0 -8px #0d0d0d, 0 0 5px #0f0f0f',
			height: '270px',
			width: '380px',
			margin: '0',
		    top: '160px',
    		left: '322px',
    		padding: '0'
		}

		return (
			<div id="left-part" className="side-part main-part">
				<h2>Ajouter/retirer<br/>des aliments</h2>
				<div id="left-block" className="side-block">
					<div className="separator"></div>
					<a href="#" onClick={() => this.refs.popupAddItems.show()}>
						<img id="fridge-add" className="left-button" src="./assets/images/add-to-fridge.svg"/>
					</a>
					<a href="#" onClick={() => this.refs.popupRemoveItems.show()}>
						<img id="fridge-remove" className="left-button" src="./assets/images/remove-from-fridge.svg"/>
					</a>
				</div>
				<SkyLight hideOnOverlayClicked dialogStyles={itemsInOutPopupStyle} ref="popupAddItems" id="add-items-popup" className="popup items-in-out">
					<h1>Comment ?</h1>
					<div className="methods-buttons">
						<a href="#" className="method">
							<span>
								<img src="./assets/images/barcode.svg"/>
								<h3>Scanner</h3>
							</span>
						</a>
						<a href="#" className="method">
							<span>
								<img src="./assets/images/hand.svg"/>
								<h3>Ajouter manuellement</h3>
							</span>
						</a>
					</div>
				</SkyLight>
				<SkyLight hideOnOverlayClicked dialogStyles={itemsInOutPopupStyle} ref="popupRemoveItems" id="remove-items-popup" className="popup items-in-out">
					<h1>Comment ?</h1>
					<div className="methods-buttons">
						<a href="#" className="method">
							<span>
								<img src="./assets/images/barcode.svg"/>
								<h3>Scanner</h3>
							</span>
						</a>
						<a href="#" className="method">
							<span>
								<img src="./assets/images/hand.svg"/>
								<h3>Retirer manuellement</h3>
							</span>
						</a>
					</div>
				</SkyLight>
			</div>  
		);
	}
}

class RightHome extends Component {
	constructor(props){
		super(props);
	}

	render() {

		var sendPopupStyle = {
			backgroundColor: '#d6d6d6',
			borderRadius: '20px',
			boxShadow: 'inset 0 -5px #ff3131, inset 0 -8px #0d0d0d, 0 0 5px #0f0f0f',
			height: '200px',
			width: '250px',
			margin: '0',
		    top: '200px',
    		left: '387px'
		}

		return (
			<div id="right-part" className="side-part main-part">
				<h2>Ma liste de<br/>courses</h2>
				<div id="right-block" className="side-block">
					<div className="separator"></div>
					<div className="separator"></div>
					<a href="/list">
						<div id="view-list" className="right-button">
						    <i className="fa fa-search fa-4x" aria-hidden="true"></i>
						    <h3>Voir</h3>
						</div>
					</a>
					<a href="#" onClick={() => this.refs.popupSend.show()}>
						<div id="send-list" className="right-button">
						    <i className="fa fa-mobile fa-5x" aria-hidden="true"></i>
						    <h3>Envoyer</h3>
						</div>
					</a>
					<div id="print-list" className="right-button">
						<i className="fa fa-print fa-5x" aria-hidden="true"></i>
						<h3>Imprimer</h3>
					</div>
				</div>
				<SkyLight hideOnOverlayClicked dialogStyles={sendPopupStyle} ref="popupSend" id="send-method-popup" className="popup">
					<h1>Comment ?</h1>
					<div className="send-fields">
						<div>
							<input type="tel" name="tel-number" placeholder="sms"/> <img src="./assets/images/dark-red/go-button.svg"/>
						</div>
						<div>
							<input type="email" name="email-address" placeholder="email"/> <img src="./assets/images/dark-red/go-button.svg"/>
						</div>
					</div>
				</SkyLight>
			</div>
		);
	}
}

class RightList extends Component {
	constructor(props){
		super(props);
	}

	render() {

		var emptyPopupStyle = {
			backgroundColor: '#d6d6d6',
			borderRadius: '20px',
			boxShadow: 'inset 0 -5px #ff3131, inset 0 -8px #0d0d0d, 0 0 5px #0f0f0f',
			height: '200px',
			width: '300px',
			margin: '0',
		    top: '200px',
    		left: '362px'
		}

		var sendPopupStyle = {
			backgroundColor: '#d6d6d6',
			borderRadius: '20px',
			boxShadow: 'inset 0 -5px #ff3131, inset 0 -8px #0d0d0d, 0 0 5px #0f0f0f',
			height: '200px',
			width: '250px',
			margin: '0',
		    top: '200px',
    		left: '387px'
		}

		return (
			<div id="right-part" className="side-part main-part">
				<h2>Ma liste de<br/>courses</h2>
				<div id="right-block" className="side-block">
					<div className="separator"></div>
					<div className="separator"></div>
					<a href="#" onClick={() => this.refs.popupEmpty.show()}>
						<div id="empty-list" className="right-button">
						    <i className="fa fa-trash fa-4x" aria-hidden="true"></i>
						    <h3>Vider</h3>
						</div>
					</a>
					<a href="#" onClick={() => this.refs.popupSend.show()}>
						<div id="send-list" className="right-button">
							<i className="fa fa-mobile fa-5x" aria-hidden="true"></i>
							<h3>Envoyer</h3>
						</div>
					</a>
					<a href="#">
						<div id="print-list" className="right-button">
							<i className="fa fa-print fa-5x" aria-hidden="true"></i>
							<h3>Imprimer</h3>
						</div>
					</a>
				</div>
				<SkyLight hideOnOverlayClicked dialogStyles={emptyPopupStyle} ref="popupEmpty" id="empty-list-popup" className="popup">
					<h1>Vider la liste ?</h1>
					<div id="confirm-buttons">
						<a href="#">
							<img src="./assets/images/dark-red/confirm-button.svg"/>
						</a>
					</div>
				</SkyLight>
				<SkyLight hideOnOverlayClicked dialogStyles={sendPopupStyle} ref="popupSend" id="send-method-popup" className="popup">
					<h1>Comment ?</h1>
					<div className="send-fields">
						<div>
							<input type="tel" name="tel-number" placeholder="sms"/> <img src="./assets/images/dark-red/go-button.svg"/>
						</div>
						<div>
							<input type="email" name="email-address" placeholder="email"/> <img src="./assets/images/dark-red/go-button.svg"/>
						</div>
					</div>
				</SkyLight>
			</div>
		);
	}
}

class FridgeList extends Component {
	render() {
		return (
			<div id="fridges">
				{fridgesList.map((dynamicComponent, i) => <Fridge
					key = {i} componentData = {dynamicComponent}/>)}
			</div>
		);
	}
}

class Fridge extends Component {
	render() {
		return (
			<div className="fridge">
				<a href={"/fridgeContent/"+this.props.componentData.FrigoId}>
					<img src="./assets/images/fridge.svg"/>
					<h3>{this.props.componentData.FrigoNom}</h3>
				</a>
			</div>
		);
	}
}

class MiddleHome extends Component {
	render() {
		return (
			<div id="middle-block" className="main-part">
				<h1>Mes frigos</h1>
				<FridgeList />
				<div id="buttons">
					<img src="./assets/images/dark-red/plus-button.svg"/>
					<img src="./assets/images/dark-red/gear-button.svg"/>
				</div>
			</div>
		);
	}
}

class FridgeContent extends Component {
/*
	TO-DO :
		* GET Liste de courses
			-> Chaque Item dans
				<li>**ITEM** <a href="#"><i className="fa fa-times remove-item" aria-hidden="true"></i></a></li>	
*/
	constructor(props){
		super(props);
	}

	render() {

		var removeItemPopupStyle = {
			backgroundColor: '#d6d6d6',
			borderRadius: '20px',
			boxShadow: 'inset 0 -5px #ff3131, inset 0 -8px #0d0d0d, 0 0 5px #0f0f0f',
			height: '220px',
			width: '300px',
			margin: '0',
		    top: '190px',
    		left: '362px'
		}

		return (
			<ul id="list">
				<li></li>
				<li>Tomates</li>
				<li>Jus de pomme</li>
				<li>Crème fraîche</li>
				<li>Gouda</li>
				<li>Confiture</li>
				<li>Lait</li>
				<li>Jambon</li>
				<li>Bacon</li>
				<li>Margarine</li>
				<li>Oeufs</li>
			</ul>
		);
	}
}

class ShoppingList extends Component {
	/*
	 TO-DO :
	 * GET Liste de courses
	 -> Chaque Item dans
	 <li>**ITEM** <a href="#"><i className="fa fa-times remove-item" aria-hidden="true"></i></a></li>
	 */
	constructor(props){
		super(props);
	}

	render() {

		var removeItemPopupStyle = {
			backgroundColor: '#d6d6d6',
			borderRadius: '20px',
			boxShadow: 'inset 0 -5px #ff3131, inset 0 -8px #0d0d0d, 0 0 5px #0f0f0f',
			height: '220px',
			width: '300px',
			margin: '0',
			top: '190px',
			left: '362px'
		}

		return (
			<ul id="list">
				<li></li>
				<li>Tomates <a href="#"><i className="fa fa-times remove-item" aria-hidden="true" onClick={() => this.refs.popupRemoveItem.show()}></i></a></li>
				<li>Jus de pomme <a href="#"><i className="fa fa-times remove-item" aria-hidden="true" onClick={() => this.refs.popupRemoveItem.show()}></i></a></li>
				<li>Crème fraîche <a href="#"><i className="fa fa-times remove-item" aria-hidden="true" onClick={() => this.refs.popupRemoveItem.show()}></i></a></li>
				<li>Gouda <a href="#"><i className="fa fa-times remove-item" aria-hidden="true" onClick={() => this.refs.popupRemoveItem.show()}></i></a></li>
				<li>Confiture <a href="#"><i className="fa fa-times remove-item" aria-hidden="true" onClick={() => this.refs.popupRemoveItem.show()}></i></a></li>
				<li>Lait <a href="#"><i className="fa fa-times remove-item" aria-hidden="true" onClick={() => this.refs.popupRemoveItem.show()}></i></a></li>
				<li>Jambon <a href="#"><i className="fa fa-times remove-item" aria-hidden="true" onClick={() => this.refs.popupRemoveItem.show()}></i></a></li>
				<li>Bacon <a href="#"><i className="fa fa-times remove-item" aria-hidden="true" onClick={() => this.refs.popupRemoveItem.show()}></i></a></li>
				<li>Margarine <a href="#"><i className="fa fa-times remove-item" aria-hidden="true" onClick={() => this.refs.popupRemoveItem.show()}></i></a></li>
				<li>Oeufs <a href="#"><i className="fa fa-times remove-item" aria-hidden="true" onClick={() => this.refs.popupRemoveItem.show()}></i></a></li>
				<SkyLight hideOnOverlayClicked dialogStyles={removeItemPopupStyle} ref="popupRemoveItem" id="empty-list-popup" className="popup">
					<h1>Retirer de la liste ?</h1>
					<div id="confirm-buttons">
						<a href="#">
							<img src="./assets/images/dark-red/confirm-button.svg"/>
						</a>
					</div>
				</SkyLight>
			</ul>
		);
	}
}

class MiddleList extends Component {
	render() {
		return (
			<div id="middle-block" className="main-part list-block">
				<h1>Ma liste</h1>
				<ShoppingList />
				<div id="add-item">
					<div id="mask"></div>
					<img src="./assets/images/dark-red/plus-button.svg"/>
				</div>
			</div>
		);
	}
}

//START OF THE POPUPS

class PopupEmpty extends Component {
	render(){
		return (
			<div id="empty-list-popup" className="popup">
				<h1>Vider la liste ?</h1>
				<div id="confirm-buttons">
					<a href="#">
					<img src="./assets/images/dark-red/confirm-button.svg"/>
					</a>
					<a href="#">
						<img src="./assets/images/dark-red/X-button.svg" className="popup-x"/>
					</a>
				</div>
			</div>
		);  
	}
}

class PopupAddItems extends Component {
	render(){
		return (
			<div id="add-items-popup" className="popup items-in-out">
				<a href="#">
					<img src="./assets/images/dark-red/X-button.svg" className="close-popup popup-x"/>
				</a>
				<h1>Comment ?</h1>
				<div className="methods-buttons">
					<a href="#">
						<span className="method">
							<img src="./assets/images/barcode.svg"/>
							<h3>Scanner</h3>
						</span>
					</a>
					<a href="#">
						<span className="method">
							<img src="./assets/images/hand.svg"/>
							<h3>Ajouter manuellement</h3>
						</span>
					</a>
				</div>
			</div>
		);
	}
}

class PopupRemoveItems extends Component {
	render(){
		return (
			<div id="remove-items-popup" className="popup items-in-out">
				<a href="#">
					<img src="./assets/images/dark-red/X-button.svg" className="close-popup popup-x"/>
				</a>
				<h1>Comment ?</h1>
				<div className="methods-buttons">
					<a href="#">
						<span className="method">
							<img src="./assets/images/barcode.svg"/>
							<h3>Scanner</h3>
						</span>
					</a>
					<a href="#">
						<span className="method">
							<img src="./assets/images/hand.svg"/>
							<h3>Retirer manuellement</h3>
						</span>
					</a>
				</div>
			</div>
		);
	}
}

class PopupRemoveFromList extends Component {
	render(){
		return (
			<div id="remove-from-list-popup" className="popup">
				<h1>Retirer de la liste ?</h1>
				<div id="confirm-buttons">
					<a href="#">
						<img src="./assets/images/dark-red/confirm-button.svg"/>
					</a>
					<a href="#">
						<img src="./assets/images/dark-red/X-button.svg" className="popup-x"/>
					</a>
				</div>
			</div>
		);  
	}
}

class PopupSendList extends Component {
	render(){
		return (
			<div id="send-method-popup" className="popup">
				<a href="#" className="close-popup popup-x">
					<img src="./assets/images/dark-red/X-button.svg" className="close-popup" id="close-send"/>
				</a>
				<h1>Comment ?</h1>
				<div className="send-fields">
					<div>
						<input type="tel" name="tel-number" placeholder="sms"/> <img src="./assets/images/dark-red/go-button.svg"/>
					</div>
					<div>
						<input type="email" name="email-address" placeholder="email"/> <img src="./assets/images/dark-red/go-button.svg"/>
					</div>
				</div>
			</div>
		);
	}
}

//END OF THE POPUPS

class Home extends Component {
	render() {
		return (
			<div className="Home">
				<TopHome />
				<div id="wrapper">
					<Left />
					<MiddleHome />
					<RightHome />
				</div>
			</div>
		);
	}
}

class List extends Component {
	render() {
		return (
			<div className="List">
				<TopPages />
				<div id="wrapper">
					<MiddleList />
					<RightList />
				</div>
			</div>
		);
	}
}

var routes = (
	<Router history={browserHistory}>
		<Route path='/' component={Home} />
		<Route path='/list' component={List} />
		<Route path='/fridgeContent/:FridgeId' component={FridgeContent} />
	</Router>
);

/**************************************************************************
 ***********************Functions and JavaScript***************************
 *************************************************************************/
var apiBase;
var fridgesList=-1;
/**
 * Permet d'aller chercher les variables de session nécessaires
 * TODO remettre les vraies variables de session
 */
request("GET", "http://app.intellifridge.ovh/app/getSession.php", "", storeApiDatas, apiError);
/**
 * Va chercher la liste des frigos
 */
apiRequest("GET", "fridges/list", null, function(an){
	console.log(JSON.stringify(an));
	setFridgeList(an);
}, function (an) {
	console.log("Erreur : \n"+JSON.stringify(an));
});
/**
 * lance le rendu de l'application
 */
render();
function render(){
	ReactDOM.render(routes, document.querySelector('#root'));
}
/**
 * Permet de faire une requète vers une page renvoyant du JSON
 * @param type type de requète. Ex : GET, POST etc
 * @param url URL de la page pour la requète
 * @param data Données envoyées pour la requète
 * @param fs Fonction lancée si la requète réussi
 * @param fe Fonction lancée si la requète ne réussit pas
 */
function request(type, url, data, fs, fe){
	$.ajax({
		async : false,
		type: type,
		url: url,
		data : data,
		dataType: 'text',
		crossDomain: true,
		xhrFields: {
			withCredentials: false
		},
		success: fs,
		error: fe
	});
}
/**
 * Change l'objet apiBase pour contenir UserId et ApiKey
 * @param an String JSON des données
 */
function storeApiDatas(an){
	apiBase = JSON.parse(an);
}
/**
 * Fonction de debug qui envoie en console le résultat
 * @param an Objet renvoyé par une requète
 */
function apiError(an){
	console.log(JSON.stringify(an));
}
/**
 * Permet de faire une requète vers l'API d'IntelliFridge
 * @param type type de requète. Ex : GET, POST etc
 * @param url Fin de l'URL http://api.intellifridge.ovh/v1/ pour avoir accès aux informations souhaitées
 * @param data Données envoyées pour la requète (viendra s'ajouter ApiKey et UserId automatiquement)
 * @param fs Fonction lancée si la requète réussi
 * @param fe Fonction lancée si la requète ne réussit pas
 */
function apiRequest(type, url, data, fs, fe){
	var apiAn = -1;
	if(data!=null){
		var sData = JSON.stringify(Array.prototype.push.apply(apiBase, data));
	}else{
		var sData = JSON.stringify(apiBase);
	}
	var sJWT = {jwt:jwt.sign(sData, "wAMxBauED07a4GurMpuD", {header:{alg: 'HS256', typ: 'JWT'}})};
	console.log(JSON.stringify(sJWT));
	request(type, "http://api.intellifridge.ovh/v1/"+url, sJWT, function(an){
		var decoded = jwt.decode(an, {complete: true});
		if(decoded.payload.status==200){
			fs(decoded.payload.data);
		}else{
			fe(decoded.payload);
		}
	}, function(an){
		alert("Erreur d'API : \n\n"+an);
	});
}
function setFridgeList(data){
	fridgesList = data;
}