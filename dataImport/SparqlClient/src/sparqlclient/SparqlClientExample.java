package sparqlclient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class SparqlClientExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        SparqlClient sparqlClient = new SparqlClient("localhost:3030/goToMovieSpot");

        String query = "ASK WHERE { ?s ?p ?o }";
        boolean serverIsUp = sparqlClient.ask(query);
        if (serverIsUp) {
            System.out.println("server is UP");

            
            //Parsing films.csv
            try (BufferedReader br = new BufferedReader(new FileReader("films.csv"))) {
            	System.out.println("parsing films.csv...");
                String line;
                //TODO : skip 1st line (cuz headers)
                //TODO : replace all special characters. Use [$&+,:;=?@#|'<>.^*()%!-]
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    List<String> cellValues = Arrays.asList(values);
                	String titleWithoutSpaces = cellValues.get(0).replaceAll(" ", "_");
                    
                    query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> " + 
                    		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " +
                    		"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "  +
                    		"PREFIX film: <http://www.semanticweb.org/nathalie/ontologies/2017/1/untitled-ontology-161#> " +
                    		"INSERT DATA { " +
                    		"film:" + titleWithoutSpaces + " rdf:type film:OWLClass_b3ec988b_3df8_44bc_bb90_2242c83e3158. " +
                    		"film:"+ titleWithoutSpaces + " rdfs:label \"" + cellValues.get(0) +"\" " +
                    		"}"
                    		;
                    System.out.println(query);
                    sparqlClient.update(query);
                    
                }
            } catch (IOException e) {
				e.printStackTrace();
			}

            System.out.println(sparqlClient.getEndpointUri());
            System.out.println("server is down");
            
            
            /* Example
            nbPersonnesParPiece(sparqlClient);

            System.out.println("ajout d'une personne dans le bureau:");
            query = "PREFIX : <http://www.lamaisondumeurtre.fr#>\n"
                    + "PREFIX instances: <http://www.lamaisondumeurtre.fr/instances#>\n"
                    + "INSERT DATA\n"
                    + "{\n"
                    + "  instances:Bob :personneDansPiece instances:Bureau.\n"
                    + "}\n";
            sparqlClient.update(query);

            nbPersonnesParPiece(sparqlClient);

            System.out.println("suppression d'une personne du bureau:");
            query = "PREFIX : <http://www.lamaisondumeurtre.fr#>\n"
                    + "PREFIX instances: <http://www.lamaisondumeurtre.fr/instances#>\n"
                    + "DELETE DATA\n"
                    + "{\n"
                    + "  instances:Bob :personneDansPiece instances:Bureau.\n"
                    + "}\n";
            sparqlClient.update(query);
            
            nbPersonnesParPiece(sparqlClient);
            */
        } else {
            System.out.println("service is DOWN");
        }
    }
    
    private static void nbPersonnesParPiece(SparqlClient sparqlClient) {
        String query = "PREFIX : <http://www.lamaisondumeurtre.fr#>\n"
                    + "SELECT ?piece (COUNT(?personne) AS ?nbPers) WHERE\n"
                    + "{\n"
                    + "    ?personne :personneDansPiece ?piece.\n"
                    + "}\n"
                    + "GROUP BY ?piece\n";
            Iterable<Map<String, String>> results = sparqlClient.select(query);
            System.out.println("nombre de personnes par pièce:");
            for (Map<String, String> result : results) {
                System.out.println(result.get("piece") + " : " + result.get("nbPers"));
            }
    }    
}
