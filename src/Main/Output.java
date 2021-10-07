package Main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import com.besaba.revonline.pastebinapi.Pastebin;
import com.besaba.revonline.pastebinapi.impl.factory.PastebinFactory;
import com.besaba.revonline.pastebinapi.response.Response;
import com.besaba.revonline.pastebinapi.paste.Paste;
import com.besaba.revonline.pastebinapi.paste.PasteBuilder;
import com.besaba.revonline.pastebinapi.paste.PasteExpire;
import com.besaba.revonline.pastebinapi.paste.PasteVisiblity;
import java.awt.Desktop;
import java.net.URI;
import java.sql.*;

public class Output {
	// Pastebin
	public static final String DEV_KEY = "piqUzjJeWnPkIaBQWVvC8NlvddNwNhig";
	// MySQL
	public static final String chain = "";
	public static final String user = "";
	public static final String password = "";
	
	public static void sqlSave(ArrayList<String> data) {
		
	}
	
	@SuppressWarnings({ "rawtypes", "deprecation", "unused" })
	public static void sqlConnect() {
		// instanciation du driver
		Class cPilote = null;
        try {
            cPilote = Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            promptInformation("Erreur à la création de l'instance !");
        }
        // invocation d'une instance du driver
        Object pilote = null;
		try {
			pilote = cPilote.newInstance();
		} catch (Exception e) {
			promptInformation("L'invocation a échoué !");
		}
        // enregistrement du pilote
        try {
			DriverManager.registerDriver((Driver) pilote);
		} catch (Exception e) {
			promptInformation("Pas d'enregistemeent du pilote !");
		}
        // instanciation de la connexion
        try {
			Connection c = DriverManager.getConnection(chain, user, password);
		} catch (Exception e) {
			promptInformation("Connexion non instanciée !");
		}
    }
	
	  

	  public static void lecture() {
	    PastebinFactory factory = new PastebinFactory();
	    Pastebin pastebin = factory.createPastebin(DEV_KEY);

	    final String pasteToRead = "CWs08Mmd";
	    final Response<String> rawResponse = pastebin.getRawPaste(pasteToRead);

	    if (rawResponse.hasError()) {
	      System.out.println("Impossible de lire ce pastebin ! " + rawResponse.hasError());
	      return;
	    }

	    System.out.println(rawResponse.get());
	  }

	    public static void ecriture(String id, String content) {
	      PastebinFactory factory = new PastebinFactory();
	      Pastebin pastebin = factory.createPastebin(DEV_KEY);
	      // get a pastebuilder to build the paste I want to publish
	      final PasteBuilder pasteBuilder = factory.createPaste();

	      // Title paste
	      pasteBuilder.setTitle("Machine@"+id);
	      
	      // What will be inside the paste?
	      pasteBuilder.setRaw(content);
	      // Which syntax will use the paste?
	      pasteBuilder.setMachineFriendlyLanguage("text");
	      // What is the visibility of this paste?
	      pasteBuilder.setVisiblity(PasteVisiblity.Unlisted);
	      // When the paste will expire?
	      pasteBuilder.setExpire(PasteExpire.Never);

	      // when i'm ready, create the Paste object
	      final Paste paste = pasteBuilder.build();

	      // ask to Pastebin to post the paste
	      // the .post method: if the paste has been published will return the key assigned
	      // by pastebin
	      final Response<String> postResult = pastebin.post(paste);

	      if (postResult.hasError()) {
	        System.out.println("Erreur : " + postResult.getError());
	        return;
	      }

	      System.out.println("Enregistré ! Url: " + postResult.get());
	    }
	
	
	/**
	 * ouvre un navigateur après avoir post le lien
	 * @param url lien à ouvrir
	 */
	public static void openNav(String url) {
		Desktop d = java.awt.Desktop.getDesktop();
		try {
			d.browse(new URI(url));
		} catch(Exception e) {
			promptInformation("Erreur URL");
		}
	}
	
	// on génère le fichier
	//public static File di = new File("/BioStats_Resultats.csv");

	public static void rec(ArrayList<String> argsRec) {	
			enregistrer(argsRec);
	}
	
	public static void enregistrer(ArrayList<String> argsRec) {

		BufferedWriter printer = null;

		// blabla paramètres ENNUYEUX blabla
		int id = 0;
		String compose = Integer.toString(id);
		// boucle qui concatène les infos
		for(String a : argsRec) {
			compose = compose + "," + a;
		}
		// on écrit dans le fichier
		
		try {
			printer = new BufferedWriter(new FileWriter("/BioStats_Resultats.csv", true));
			printer.close();
		} catch (IOException e) {
			promptInformation("Erreur I/O");
		}
		
		
	}
	
	// une méthode pour afficher une boite de dialogue TRES informative
	public static void promptInformation(String texte) {
		JOptionPane info = new JOptionPane(texte,JOptionPane.INFORMATION_MESSAGE);
		JDialog dialoge = info.createDialog("Information");
		dialoge.setAlwaysOnTop(true);
		dialoge.setVisible(true);
	}
}
