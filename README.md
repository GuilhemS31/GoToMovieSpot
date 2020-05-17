# Description
Cette application permet de rapidement visualiser les arrêts de bus proches de vos lieu de tournage favoris.

Produit dans le cadre d’utilisation de données répondant à une ontologie de donnée du Web Sémantique pour un cours de Master 2 ICE.


# Utilisation

### Prérequis

Pour pouvoir utiliser GoToMovieSpot, assurez-vous que vous avez installer sur votre machine :
apache Fuseki (testé avec la version 3.14) : https://jena.apache.org/download/
JDK 8 : https://www.oracle.com/java/technologies/javase-jdk8-downloads.html

### Fuseki

Fuseki va vous permettre de stocker toutes les données dans une base de connaissance local. 

Pour lancer Fuseki, placer vous dans le répertoire de Fuseki et ouvez un invite de commande. Tapez alors la commande *fuseki-server -update*

Ensuite, dans votre navigateur, rendez-vous sur http://localhost:3030/. Fuseki est alors lancer en mode update !

Nous alons alors avoir besoin de créer un dataset. Rendez-vous dans l'onglet *Manage Dataset* puis sélectionnez *Create dataset*. **Il est nécessaire que vous lui donniez le nom *goToMovieSpot* si vous ne voulez pas avoir à modifier le code**. Il est également recommendé de sélectionner l'option *Persistent – dataset will persist across Fuseki restarts*.

Une fois votre dataset créé, vous serai de retour sur l'onglet *Existing datasets*. A partie de là, sélectionnez *upload data* puis indiquez le chemin du fichier *goToMovieSpotOntology.owl* situé à la racine de ce repository.

Nous en avons alors finis avec Fuseki.


### Peuplement de l'ontologie

Nous alons désormais peupler notre ontologie. Pour ce faire, à partir de la racine de ce repository, rendez-vous dans *dataImport\SparqlClient\src\sparqlclient* et exécutez le fichier Java *GoToMovieSpotDataImport.java*

Cette étape prend environ 30 minutes car le programmes va insérez les données dans notre dataset à partir des fichiers sources *accessibilite-des-arrets-de-bus-ratp.csv* et *films.csv*.


### Lancer l'application

Une fois votre ontologie peuplée, vous pouvez alors lancer l'application. Pour ce faire, rendez-vous dans *Application*, puis ouvez *index.html* dans votre navigateur favori.
