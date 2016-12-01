import React, { Component } from 'react';
import ReactDOM from 'react-dom';
import { Router, Route, Link, browserHistory } from 'react-router'; 
import logo from './logo.svg';



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
	render() {
		return (
			<div id="left-part" className="side-part main-part">
				<h2>Ajouter/retirer<br/>des aliments</h2>
				<div id="left-block" className="side-block">
					<div className="separator"></div>
					<a href="#">
						<img id="fridge-add" className="left-button" src="./assets/images/add-to-fridge.svg"/>
					</a>
					<a href="#">
						<img id="fridge-remove" className="left-button" src="./assets/images/remove-from-fridge.svg"/>
					</a>
				</div>
			</div>  
		);
	}
}

class RightHome extends Component {
	render() {
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
					<a href="#">
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
			</div>
		);
	}
}

class RightList extends Component {
	render() {
		return (
			<div id="right-part" className="side-part main-part">
				<h2>Ma liste de<br/>courses</h2>
				<div id="right-block" className="side-block">
					<div className="separator"></div>
					<div className="separator"></div>
					<a href="#">
						<div id="empty-list" className="right-button">
						    <i className="fa fa-trash fa-4x" aria-hidden="true"></i>
						    <h3>Vider</h3>
						</div>
					</a>
					<a href="#">
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
			</div>
		);
	}
}

class MiddleHome extends Component {
	render() {
		return (
			<div id="middle-block" className="main-part">
				<h1>Mes frigos</h1>
				<div id="fridges">
					<div className="fridge">
						<img src="./assets/images/fridge.svg"/>
						<h3>Cuisine</h3>
					</div>
					<div className="fridge">
						<img src="./assets/images/fridge.svg"/>
						<h3>Cave</h3>
					</div>
				</div>
				<div id="buttons">
					<img src="./assets/images/dark-red/plus-button.svg"/>
					<img src="./assets/images/dark-red/gear-button.svg"/>
				</div>
			</div>
		);
	}
}

class MiddleList extends Component {
	render() {
		return (
			<div id="middle-block" className="main-part list-block">
				<h1>Ma liste</h1>
				<ul id="list">
					<li></li>
					<li>Tomates <a href="#"><i className="fa fa-times remove-item" aria-hidden="true"></i></a></li>
					<li>Jus de pomme <a href="#"><i className="fa fa-times remove-item" aria-hidden="true"></i></a></li>
					<li>Crème fraîche <a href="#"><i className="fa fa-times remove-item" aria-hidden="true"></i></a></li>
					<li>Gouda <a href="#"><i className="fa fa-times remove-item" aria-hidden="true"></i></a></li>
					<li>Confiture <a href="#"><i className="fa fa-times remove-item" aria-hidden="true"></i></a></li>
					<li>Lait <a href="#"><i className="fa fa-times remove-item" aria-hidden="true"></i></a></li>
					<li>Jambon <a href="#"><i className="fa fa-times remove-item" aria-hidden="true"></i></a></li>
					<li>Bacon <a href="#"><i className="fa fa-times remove-item" aria-hidden="true"></i></a></li>
					<li>Margarine <a href="#"><i className="fa fa-times remove-item" aria-hidden="true"></i></a></li>
					<li>Oeufs <a href="#"><i className="fa fa-times remove-item" aria-hidden="true"></i></a></li>
				</ul>
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
				<a href="#">
					<img src="./assets/images/dark-red/X-button.svg" className="close-popup popup-x"/>
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
					<PopupSendList />
					<PopupRemoveItems />
					<PopupAddItems />
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
					<PopupEmpty />
					<PopupSendList />
					<PopupRemoveFromList />
					<PopupAddItems />
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
	</Router>
);

ReactDOM.render(routes, document.querySelector('#root'));

document.querySelector('#send-list').onclick =  function(){
	document.querySelector('#send-method-popup').style.zIndex = "25";
	document.querySelector('#send-method-popup').style.opacity = "1";
	document.querySelector('#send-method-popup').style.visibility = "visible";

}

document.querySelector('#empty-list').onclick =  function(){
	document.querySelector('#empty-list-popup').style.zIndex = "25";
	document.querySelector('#empty-list-popup').style.opacity = "1";
	document.querySelector('#empty-list-popup').style.visibility = "visible";
}

document.querySelector('#fridge-add').onclick =  function(){
	document.querySelector('#add-items-popup').style.zIndex = "25";
	document.querySelector('#add-items-popup').style.opacity = "1";
	document.querySelector('#add-items-popup').style.visibility = "visible";
}

document.querySelector('#fridge-remove').onclick =  function(){
	document.querySelector('#remove-items-popup').style.zIndex = "25";
	document.querySelector('#remove-items-popup').style.opacity = "1";
	document.querySelector('#remove-items-popup').style.visibility = "visible";
}

document.querySelector('#remove-item').onclick =  function(){
	document.querySelector('#remove-from-list-popup').style.zIndex = "25";
	document.querySelector('#remove-from-list-popup').style.opacity = "1";
	document.querySelector('#remove-from-list-popup').style.visibility = "visible";
}

document.querySelector('.popup-x').onclick = function(){
	document.querySelector('.popup').style.zIndex = "0";
	document.querySelector('.popup').style.opacity = "0";
	document.querySelector('.popup').style.visibility = "hidden";
}