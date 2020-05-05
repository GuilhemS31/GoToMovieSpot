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


function searchFilm(){
  console.log("searchFilm");
  //document.getElementById("erreurFilm").hidden = false;

}


function searchLigne(){
  console.log("searchLigne");
  //document.getElementById("numLigne").value 
  //document.getElementById("erreurLigne").hidden = false;

}
