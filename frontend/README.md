#intelliFridge webapp

Frontend de la webapp intelliFridge créé à partir de [create-react-app](https://github.com/facebookincubator/create-react-app)

##Installer les dépendances et lancer `browser-watch` sur `localhost`

~~~bash
$ npm install react-router
$ npm install react-skylight
$ npm install jquery
$ npm install jsonwebtoken
~~~

After That edit the two `webpack.config files` in the directory `node_modules/react-scripts/config`
and in the directory `node_modules\webpack-dev-server\client`
Edit these lines : 
~~~js
node:{
  fs : 'empty'
}
~~~
with the folowing lines :  
~~~js 
node: {
  fs: 'empty',
  dns: 'mock',
  net: 'mock',
  tls: 'empty'
}
~~~

Then you can launch the `browser-watch` on `localhost`
~~~bash
$ npm start
~~~

##Créer le dossier build

`npm run build`
