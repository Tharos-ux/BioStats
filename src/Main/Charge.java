package Main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Charge {

	public static ArrayList<String> chaines = new ArrayList<String>();
	
	/**************** LOAD ******************/
	
	public static ArrayList<String> load(String path) {
		BufferedReader lecteur;
		try {
			lecteur = new BufferedReader(new FileReader(path));
			String ligne = lecteur.readLine();
			while(ligne!=null) {
				if (ligne.charAt(0)!='>') { // on retire les lignes avec chevron du fasta
					ligne = ligne.replaceAll("[^ATCG]",""); // expression régulière, on retire tout ce qui ne correspond pas à des bases
					chaines.add(ligne);
				}
				ligne= lecteur.readLine();
			}
		} catch (FileNotFoundException e) {
			System.out.println("/!\\ Le fichier n'existe pas !");
		} catch (IOException e) {
			System.out.println("/!\\ Erreur d'entrée/sortie !");
		}
		return chaines;
	}
	
	public static void printList(ArrayList<String> s) {
		if(!s.isEmpty()) {
			for(String entry : s ) {
				System.out.println(entry);
			}
		}
		else {
			System.out.println("WARNING : On a tenté d'afficher une liste vide !");
		}
	}

}
