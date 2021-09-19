package Main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Output {
	// on génère le fichier
	public static File di = new File("/BioStats_Resultats.csv");

	public static void rec(ArrayList<String> argsRec) {
		try {
			di.createNewFile();
			di.setWritable(true);
			enregistrer(argsRec);
		} catch (IOException e) {
			// ptdr t'as cru je gérais l'erreur ?
			promptInformation("Une erreur est survenue.");
		}
	}
	
	public static void enregistrer(ArrayList<String> argsRec) throws IOException {
		// blabla paramètres ENNUYEUX blabla
		int id = 0;
		String compose = Integer.toString(id);
		// boucle qui concatène les infos
		for(String a : argsRec) {
			compose = compose + "," + a;
		}
		// on écrit dans le fichier
		PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(di)));
		printer.flush();
		// html
		printer.println(compose);
		printer.close();
	}
	
	// une méthode pour afficher une boite de dialogue TRES informative
	public static void promptInformation(String texte) {
		JOptionPane info = new JOptionPane(texte,JOptionPane.INFORMATION_MESSAGE);
		JDialog dialoge = info.createDialog("Information");
		dialoge.setAlwaysOnTop(true);
		dialoge.setVisible(true);
	}
}
