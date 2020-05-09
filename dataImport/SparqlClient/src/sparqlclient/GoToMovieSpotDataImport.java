package sparqlclient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class GoToMovieSpotDataImport {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    	//TODO CREATE DATASET goToMovieSpot !!!
        SparqlClient sparqlClient = new SparqlClient("localhost:3030/goToMovieSpot");

        String query = "ASK WHERE { ?s ?p ?o }";
        boolean serverIsUp = sparqlClient.ask(query);
        if (serverIsUp) {
            System.out.println("server is UP");
            
            //Fill Lieux de tournage & Film
            insertFilmFromCSV(sparqlClient);
            
            //Fill Arret de Bus
            //insertArretDeBusFromCSV(sparqlClient);
            
            //TODO calculer "est pr�s de"
            

            System.out.println(sparqlClient.getEndpointUri());
            System.out.println("server is down");
        } else {
            System.out.println("service is DOWN");
        }
    }

	private static void insertFilmFromCSV(SparqlClient sparqlClient) {
        try (BufferedReader br = new BufferedReader(new FileReader("films.csv"))) {
        	System.out.println("parsing films.csv...");
            String line;
            String query;
            
            //skip 1st line (headers)
            String headerLine = br.readLine();
            		
            //For each line of the CSV
            while ((line = br.readLine()) != null) {
            	//Make a List of attributes
                String[] values = line.split(",");
                //0:titre,1:realisateur,2:adresse,3:organisme_demandeur,4:type_de_tournage,5:ardt,6:date_debut,7:date_fin,89:xy
                List<String> cellValues = Arrays.asList(values);
                //replace all special characters for correct entities name (their ID)
            	String titleWithoutSpaces = cellValues.get(0).replaceAll("[ $&+:;=?@#|'<>.^*()%!-]", "_");
            	String adressWithoutSpaces = cellValues.get(2).replaceAll("[ $&+:;=?@#|'<>.^*()%!-]", "_");
            	
            	//___________ First add Lieux de Tournage ___________
            	
            	//Spliting xy as latitude / longitude
            	float latitude = (float) 0.0;
            	float longitude = (float) 0.0;
            	try {
                	latitude = Float.valueOf(cellValues.get(8).replace("\"", ""));
                	longitude = Float.valueOf(cellValues.get(9).replace("\"", ""));
            	}catch (Exception e) {
					// If xy isn't set, let latitude and longitude null
				}
            	
            	query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + 
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "  +
            	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
        		"PREFIX goToMovieSpot: <http://www.semanticweb.org/nathalie/ontologies/2017/1/untitled-ontology-161#> " +
        		"INSERT DATA { " +
        		"goToMovieSpot:" + adressWithoutSpaces + " rdf:type goToMovieSpot:OWLClass_76723d59_95c9_4d66_92e6_ee20c2d7b7ae. " +
        		"goToMovieSpot:"+ adressWithoutSpaces + " rdfs:label \"" + cellValues.get(2) +"\". " +
        		"goToMovieSpot:"+ adressWithoutSpaces + " goToMovieSpot:\"a pour latitude\" " + latitude +". " +
        		"goToMovieSpot:"+ adressWithoutSpaces + " goToMovieSpot:\"a pour longitude\" " + longitude +
        		"}";

                System.out.println("Lieux de Tournage : " + query);
                //String encodedQuery = new String(query.getBytes(),  Charset.forName("UTF-8"));
                sparqlClient.update(query);
            	
                

            	//___________ Then add Film ___________
                
               
            	//Define Entity class
            	String OWLclassName = "OWLClass_b3ec988b_3df8_44bc_bb90_2242c83e3158";
            	switch(cellValues.get(4)) {
            	case "LONG METRAGE" :
            		OWLclassName = "OWLClass_dea269dd_41df_4b73_ba4f_fa9e99620933";
            		break;
            	case "SERIE TELEVISEE" :
            		OWLclassName = "OWLClass_f7a4a251_a16f_49b3_ae49_99b3687fbb48";
            		break;

            	case "TELEFILM" :
            		OWLclassName = "OWLClass_864e038d_b0fc_4708_b47e_03518f04e159";
            		break;
            	}
            	
            	query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + 
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "  +
        		"PREFIX goToMovieSpot: <http://www.semanticweb.org/nathalie/ontologies/2017/1/untitled-ontology-161#> " +
        		"INSERT DATA { " +
        		"goToMovieSpot:" + titleWithoutSpaces + " rdf:type goToMovieSpot:" + OWLclassName + ". " +
        		"goToMovieSpot:"+ titleWithoutSpaces + " rdfs:label \"" + cellValues.get(0) +"\". " +
        		"goToMovieSpot:"+ titleWithoutSpaces + " goToMovieSpot:\"a pour r�alisateur\" \"" + cellValues.get(1) +"\". " +
        		"goToMovieSpot:"+ titleWithoutSpaces + " goToMovieSpot:\"est tourn� �\" goToMovieSpot:" + adressWithoutSpaces +". " +
        		"goToMovieSpot:"+ titleWithoutSpaces + " goToMovieSpot:\"est produit par\" \"" + cellValues.get(3) +"\"" +
        		"}";

                System.out.println("Film : " + query);
                sparqlClient.update(query);
                
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
	}


    private static void insertArretDeBusFromCSV(SparqlClient sparqlClient) {
    	try (BufferedReader br = new BufferedReader(new FileReader("accessibilite-des-arrets-de-bus-ratp.csv"))) {
        	System.out.println("parsing accessibilite-des-arrets-de-bus-ratp.csv...");
            String line;
            String query;
            
            //skip 1st line (headers)
            String headerLine = br.readLine();
            		
            //For each line of the CSV
            while ((line = br.readLine()) != null) {
            	//Make a List of attributes
                String[] values = line.split(",");
                //0:idptar;1:X;2:Y;3:Annonce Sonore Prochain Passage;4:Annonce Visuelle Prochain Passage;5:Annonce Sonore Situations Perturbees;
                //6:Annonce Visuelle Situations Perturbees;78:coord;9:IDAMIVIF;10:nomptar;11:CODEINSEE;12:Accessibilit� UFR;13:PAQT;14:IDFM;15:Sens;
                //16:Doublon;17:Ligne;18:GIPA
                List<String> cellValues = Arrays.asList(values);
                //replace all special characters for correct entities name (their ID)
            	String nomArretWithoutSpaces = cellValues.get(10).replaceAll("[ $&+:;=?@#|'<>.^*()%!-]", "_");

            	//Spliting coord as latitude / longitude
            	float latitude = (float) 0.0;
            	float longitude = (float) 0.0;
            	try {
                	latitude = Float.valueOf(cellValues.get(7).replace("\"", ""));
                	longitude = Float.valueOf(cellValues.get(7).replace("\"", ""));
            	}catch (Exception e) {
					// If xy isn't set, let latitude and longitude null
				}
            	
            	/* tester si arret existe deja
            	INSERT { :Ferrari owl:sameAs "http://dbpedia.org/page/"?label }
            	WHERE {
            	 :Ferrari rdfs:label ?label.
            	}
            	*/
            	query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + 
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
            	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
        		"PREFIX goToMovieSpot: <http://www.semanticweb.org/nathalie/ontologies/2017/1/untitled-ontology-161#> " +
        		"INSERT DATA { " +
        		"goToMovieSpot:" + nomArretWithoutSpaces + " rdf:type goToMovieSpot:OWLClass_fa89954a_688a_4eca_b1a5_89a6a06ead17. " +
        		"goToMovieSpot:"+ nomArretWithoutSpaces + " rdfs:label \"" + cellValues.get(10) +"\". " +
        		"goToMovieSpot:"+ nomArretWithoutSpaces + " goToMovieSpot:\"appartient � la ligne\" \"" + Integer.valueOf(cellValues.get(17)) +"\". " +
        		"goToMovieSpot:"+ nomArretWithoutSpaces + " goToMovieSpot:\"a pour latitude\" " + latitude +"^^xsd:decimal. " +
        		"goToMovieSpot:"+ nomArretWithoutSpaces + " goToMovieSpot:\"a pour longitude\" " + longitude +"^^xsd:decimal " +
        		"}";

                System.out.println("Arret de Bus : " + query);
                sparqlClient.update(query);
                
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
}