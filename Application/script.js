var arretIcon = L.icon({
    iconUrl: 'arret-marker.png',

    iconSize:     [38, 95], // size of the icon
    iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
    popupAnchor:  [-3, -76] // point from which the popup should open relative to the iconAnchor
});

var filmIcon = L.icon({
    iconUrl: 'movie-marker.png',

    iconSize:     [38, 95], // size of the icon
    iconAnchor:   [22, 94], // point of the icon which will correspond to marker's location
    popupAnchor:  [-3, -76] // point from which the popup should open relative to the iconAnchor
});

var markerGroup;
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

                markerGroup = L.layerGroup().addTo(mapParis);
}


//https://www.w3.org/community/rdfjs/wiki/Comparison_of_RDFJS_libraries#SPARQL.2FQuery_libraries

function searchFilm(){
  console.log("searchFilm");
  document.getElementById("noFilmFound").hidden = true;
  document.getElementById("selectFilm").hidden = true;
  document.getElementById("submitFilm").hidden = true;

  //TODO : SPARQL QUERY
  const query = "PREFIX goToMovieSpot: <http://www.semanticweb.org/nathalie/ontologies/2017/1/untitled-ontology-161#> "+
    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
    "SELECT distinct ?label "+
    "WHERE { "+
    "?film a ?type; "+
    "       rdfs:label ?label. " +
    "filter contains(?label,\"" + document.getElementById("nomFilm").value.toUpperCase() + "\") " +
    "filter (?type IN (goToMovieSpot:OWLClass_b3ec988b_3df8_44bc_bb90_2242c83e3158," +
      				"goToMovieSpot:OWLClass_dea269dd_41df_4b73_ba4f_fa9e99620933, " +
      				"goToMovieSpot:OWLClass_f7a4a251_a16f_49b3_ae49_99b3687fbb48, " +
      				"goToMovieSpot:OWLClass_864e038d_b0fc_4708_b47e_03518f04e159))" +
     "}";

      $.ajax({
        //envoi de la requête
        method: "GET",
        url: "http://localhost:3030/goToMovieSpot/sparql?query="+encodeURIComponent(query)+"&format=json",
        dataType : "json",
        success:function(data) {

            if(data.results.bindings.length != 0){

              var selectFilm = document.getElementById("selectFilm");
              selectFilm.hidden = false;
              selectFilm.innerHTML = "";


              data.results.bindings.forEach(function(s){
                //poru chaque film trouvé, créer une option de Select
                const labelFilm = s.label.value;

                var newOption = document.createElement("option");
                newOption.value = labelFilm;
                newOption.innerHTML = labelFilm;
                selectFilm.options.add(newOption);
              });

              document.getElementById("submitFilm").hidden = false;
            }
            else{
              document.getElementById("selectFilm").hidden = true;
              document.getElementById("noFilmFound").hidden = false;
            }
          }
      })
}


function searchLigne(){
  console.log("searchLigne");
  document.getElementById("erreurLigne").hidden = true;
  //document.getElementById("numLigne").value

  const query = "PREFIX goToMovieSpot: <http://www.semanticweb.org/nathalie/ontologies/2017/1/untitled-ontology-161#> "+
    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "+
    "SELECT distinct * "+
    "WHERE { "+
    "?arret a goToMovieSpot:OWLClass_fa89954a_688a_4eca_b1a5_89a6a06ead17; "+
    "       rdfs:label ?nomArret; "+
    "       goToMovieSpot:OWLDataProperty_84061d29_b41e_40e0_aa3c_f6f4098180fa \"" + document.getElementById("numLigne").value + "\"^^xsd:decimal " +
    "}";

      $.ajax({
        //envoi de la requête
        method: "GET",
        url: "http://localhost:3030/goToMovieSpot/sparql?query="+encodeURIComponent(query)+"&format=json",
        dataType : "json",
        success:function(data) {
            if(data.results.bindings.length != 0){
              data.results.bindings.forEach(function(s){
                markerGroup.clearLayers();
                creerPointArret(s.arret.value.split('#')[1],s.nomArret.value);
                creerPointLieuxProches(s.arret.value.split('#')[1]);
              });
            }
            else{
              markerGroup.clearLayers();
              document.getElementById("erreurLigne").hidden = false;
            }
          }
      })
}


function displayFilm(){
    console.log("displayFilm");
    //TODO chercher lieux de ce films
    // créer un point

}


function creerPointArret(idArret, nomArret){
    console.log("creerPointArret : "+idArret);
    const query = "PREFIX goToMovieSpot: <http://www.semanticweb.org/nathalie/ontologies/2017/1/untitled-ontology-161#> "+
      "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
      "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "+
      "SELECT distinct * "+
      "WHERE { "+
      "goToMovieSpot:"+idArret +" goToMovieSpot:OWLDataProperty_56490c48_9d55_48b0_89c4_680d73ee32ed ?latArret. " + //latitude de l'arret
      "goToMovieSpot:"+idArret +" goToMovieSpot:OWLDataProperty_2cfda50e_062f_41a4_8d92_b74af8fbc93f ?longArret " + //longitude de l'arret
      "}";

        $.ajax({
          //envoi de la requête
          method: "GET",
          url: "http://localhost:3030/goToMovieSpot/sparql?query="+encodeURIComponent(query)+"&format=json",
          dataType : "json",
          success:function(data) {
              var latPoint = 0;
              var longPoint = 0;
              if(data.results.bindings.length !=0){
                 latPoint = data.results.bindings[0].latArret.value;
                 longPoint = data.results.bindings[0].longArret.value;
              }
              var marker = L.marker([latPoint, longPoint], {icon: arretIcon}).addTo(markerGroup);
              marker.bindPopup("<b>"+nomArret+"</b>");
            }
        })
}

function creerPointLieuxProches(idArret){
  console.log("creerPointLieuxProches : "+idArret);
  const query = "PREFIX goToMovieSpot: <http://www.semanticweb.org/nathalie/ontologies/2017/1/untitled-ontology-161#> "+
    "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "+
    "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> "+
    "SELECT distinct * "+
    "WHERE { "+
    "goToMovieSpot:"+idArret +" goToMovieSpot:OWLObjectProperty_a25f1bc5_9d9b_4ae8_b942_ba60db407a84 ?lieux. " +
    "?lieux rdfs:label ?adrLieux. "+
    "?film goToMovieSpot:OWLObjectProperty_29cbf90f_8452_4a15_930f_90b6f564ea9a ?lieux. " +
    "?film rdfs:label ?nomFilm. "+
    "?lieux goToMovieSpot:OWLDataProperty_56490c48_9d55_48b0_89c4_680d73ee32ed ?latLieux. "+
    "?lieux goToMovieSpot:OWLDataProperty_2cfda50e_062f_41a4_8d92_b74af8fbc93f ?longLieux "+
    "}";

      $.ajax({
        //envoi de la requête
        method: "GET",
        url: "http://localhost:3030/goToMovieSpot/sparql?query="+encodeURIComponent(query)+"&format=json",
        dataType : "json",
        success:function(data) {
            data.results.bindings.forEach(function(s){
            var marker = L.marker([s.latLieux.value, s.longLieux.value], {icon: filmIcon}).addTo(markerGroup);
            marker.bindPopup("<b>"+s.nomFilm.value+"</b><br>"+s.adrLieux.value);
            });
          }
      })
}
