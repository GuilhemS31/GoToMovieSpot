window.onload = function initMap(){
  // Créer l'objet "macarte" et l'insèrer dans l'élément HTML qui a l'ID "map"
                mapParis = L.map('map').setView([48.852969, 2.349903], 11);
                // Leaflet ne récupère pas les cartes (tiles) sur un serveur par défaut. Nous devons lui préciser où nous souhaitons les récupérer. Ici, openstreetmap.fr
                L.tileLayer('https://{s}.tile.openstreetmap.fr/osmfr/{z}/{x}/{y}.png', {
                    // Il est toujours bien de laisser le lien vers la source des données
                    attribution: 'données © <a href="//osm.org/copyright">OpenStreetMap</a>/ODbL - rendu <a href="//openstreetmap.fr">OSM France</a>',
                    minZoom: 1,
                    maxZoom: 20
                }).addTo(mapParis);
}


//https://www.w3.org/community/rdfjs/wiki/Comparison_of_RDFJS_libraries#SPARQL.2FQuery_libraries

function searchFilm(){
  console.log("searchFilm");
  document.getElementById("noFilmFound").hidden = true;
  //TODO : SPARQL QUERY
  var queryResult = [];

  if(document.getElementById("nomFilm").value != ""){
    queryResult.push(document.getElementById("nomFilm").value);
  }

  if (queryResult.length == 0) {
    document.getElementById("noFilmFound").hidden = false;
  }
  else {
    var selectFilm = document.getElementById("selectFilm");
    selectFilm.hidden = false;
    selectFilm.innerHTML = "";

    queryResult.forEach(function(y){
      console.log(y);
      var newOption = document.createElement("option");
      newOption.value = y;
      newOption.innerHTML = y;
      selectFilm.options.add(newOption);
    });


    document.getElementById("submitFilm").hidden = false;
  }
}


function searchLigne(){
  console.log("searchLigne");
  document.getElementById("erreurLigne").hidden = true; // if error set false
  //document.getElementById("numLigne").value
  //TODO : SPARQL QUERY
}


//https://stackoverflow.com/questions/31292796/dynamically-fill-select-field-in-html-and-javascript
function displayFilm(){
    console.log("displayFilm");
}
