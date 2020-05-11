package sparqlclient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GoToMovieSpotDataImport {

	
	private final static String STR_REGEX = "[ $&+:;=?@#|'<>.^*()%!-/]";
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
            //insertFilmFromCSV(sparqlClient);
            
            //Fill Arret de Bus
            insertArretDeBusFromCSV(sparqlClient);
            
            //TODO calculer "est près de"
            

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
            	String titleWithoutSpaces = cellValues.get(0).replaceAll(STR_REGEX, "_");
            	String adressWithoutSpaces = cellValues.get(2).replaceAll(STR_REGEX, "_");
            	
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
        		//property : a pour latitude
        		"goToMovieSpot:"+ adressWithoutSpaces + " goToMovieSpot:OWLDataProperty_56490c48_9d55_48b0_89c4_680d73ee32ed \"" + latitude +"\"^^xsd:decimal. " + 
        		//property : a pour longitude
        		"goToMovieSpot:"+ adressWithoutSpaces + " goToMovieSpot:OWLDataProperty_2cfda50e_062f_41a4_8d92_b74af8fbc93f \"" + longitude + "\"^^xsd:decimal " + 
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
        		//property : a pour réalisateur
        		"goToMovieSpot:"+ titleWithoutSpaces + " goToMovieSpot:OWLObjectProperty_7714a382_8fbc_485a_b6b7_5ef0cc32fd41 \"" + cellValues.get(1) +"\". " +
        		//property : est tourné à
        		"goToMovieSpot:"+ titleWithoutSpaces + " goToMovieSpot:OWLObjectProperty_29cbf90f_8452_4a15_930f_90b6f564ea9a goToMovieSpot:" + adressWithoutSpaces +". " +
        		//property : est produit par
        		"goToMovieSpot:"+ titleWithoutSpaces + " goToMovieSpot:OWLObjectProperty_8f0f0e4a_d3b4_454d_8f8d_23cdf41b9062 \"" + cellValues.get(3) +"\"" +
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
                String[] values = line.split(";");
                //0:idptar;1:X;2:Y;3:Annonce Sonore Prochain Passage;4:Annonce Visuelle Prochain Passage;5:Annonce Sonore Situations Perturbees;
                //6:Annonce Visuelle Situations Perturbees;7:coord;8:IDAMIVIF;9:nomptar;10:CODEINSEE;11:Accessibilité UFR;12:PAQT;13:IDFM;14:Sens;
                //15:Doublon;16:Ligne;17:GIPA
                List<String> cellValues = Arrays.asList(values);
                //replace all special characters for correct entities name (their ID)
            	String nomArretWithoutSpaces = cellValues.get(9).replaceAll(STR_REGEX, "_");

            	//Spliting coord as latitude / longitude
            	String[] coords = cellValues.get(7).replace("\"", "").split(",");
            	float latitude = (float) 0.0;
            	float longitude = (float) 0.0;
            	try {
                	latitude = Float.valueOf(coords[0]);
                	longitude = Float.valueOf(coords[1]);
            	}catch (Exception e) {
					// If xy isn't set, let latitude and longitude null
				}
            	
            	Integer numLigne = 0;
            	
            	if(!cellValues.get(16).equals("")) {
            		numLigne = Integer.valueOf(cellValues.get(16)); 
            	}
            	
            	query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + 
        		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
            	"PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> " +
        		"PREFIX goToMovieSpot: <http://www.semanticweb.org/nathalie/ontologies/2017/1/untitled-ontology-161#> " +
        		"INSERT DATA { " +
        		"goToMovieSpot:" + nomArretWithoutSpaces + " rdf:type goToMovieSpot:OWLClass_fa89954a_688a_4eca_b1a5_89a6a06ead17. " +
        		"goToMovieSpot:"+ nomArretWithoutSpaces + " rdfs:label \"" + cellValues.get(9) +"\". " +
        		//property : appartient à la ligne
        		"goToMovieSpot:"+ nomArretWithoutSpaces + " goToMovieSpot:OWLDataProperty_84061d29_b41e_40e0_aa3c_f6f4098180fa \"" + numLigne +"\"^^xsd:decimal. " +
        		//property : a pour latitude
        		"goToMovieSpot:"+ nomArretWithoutSpaces + " goToMovieSpot:OWLDataProperty_56490c48_9d55_48b0_89c4_680d73ee32ed \"" + latitude +"\"^^xsd:decimal. " + 
        		//property : a pour longitude
        		"goToMovieSpot:"+ nomArretWithoutSpaces + " goToMovieSpot:OWLDataProperty_2cfda50e_062f_41a4_8d92_b74af8fbc93f \"" + longitude + "\"^^xsd:decimal " + 
        		"}";

                System.out.println("Arret de Bus : " + query);
                sparqlClient.update(query);
                
            }
        } catch (IOException e) {
			e.printStackTrace();
		}
	}
}
