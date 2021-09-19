package Main;

	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.Map;

	public class Backend {
		// définition de la dimension des clés de recherche
		public static int dimKey;
		
		
		// définition de pile d'entrée
		public static ArrayList<String> sList = new ArrayList<String>();
		
		
		/**
		 * Fonction à appeler
		 * @param chaines la liste de reads du fichier fasta
		 * @param dimKey la dimension de la clé utilisée pour les reconaissances
		 * @return la chaine recomposée
		 */
		public static String exec(ArrayList<String> chaines, int dimKey) {
			Backend.sList = chaines;
			Backend.dimKey = dimKey;
			return recomposeG(recomposeR());
		}
		
		/**
		 * Affiche le contenu d'une MAP
		 * @param reads la map de String-List<String>
		 */
		public static void mapPrintList(Map<String,ArrayList<String>> reads) {
			 for (Map.Entry<String,ArrayList<String>> entry : reads.entrySet()) {
				    System.out.println("[" + entry.getKey() +  "] -> " + entry.getValue()) ;
				}
		}
		
		/**
		 * Affiche le contenu d'une MAP
		 * @param reads la map de String-String
		 */
		public static void mapPrint(Map<String,String> reads) {
			 for (Map.Entry<String,String> entry : reads.entrySet()) {
				    System.out.println("[" + entry.getKey() +  "] -> " + entry.getValue()) ;
				}
		}
		
		/**
		 * fonction de recomposition globale
		 * @param ls liste de reads pré-assemblés
		 * @return une chaine, assemblage de ls
		 */
		@SuppressWarnings("unused")
		public static String recomposeG(ArrayList<String> ls){
			String sRecompose=ls.get(0);
			ls.remove(0);
			
			mainloop:
			while(!ls.isEmpty()) {

				String keyStart = parserStart(sRecompose);
				String keyEnd = parserEnd(sRecompose);
				for(int i=0;i<ls.size();i++) {
					if(sRecompose.contains(ls.get(i))) {
						ls.remove(i);
					}
					else if(ls.get(i).contains(keyStart)) {
						if(fusionne(ls.get(i),sRecompose,keyStart)!="false") {
							sRecompose = fusionne(ls.get(i),sRecompose,keyStart);
							ls.remove(i);
						}
					}
					else if(ls.get(i).contains(keyEnd)) {
						if(fusionne(ls.get(i),sRecompose,keyEnd)!="false") {
							sRecompose = fusionne(ls.get(i),sRecompose,keyEnd);
							ls.remove(i);
						}
					}
				}
			}
			return sRecompose;
		}
		
		/**
		 * Recomposition partielle
		 * @return une liste de recompositions locales 
		 */
		public static ArrayList<String> recomposeR(){
			Map<String, ArrayList<String>> myMap = listOrder();
			ArrayList<String> listing = new ArrayList<String>();
			for(Map.Entry<String, ArrayList<String>> entry : myMap.entrySet()) {
				listing.add(recompose(entry.getKey(),entry.getValue()));
			}
			return listing;
		}
		
		/**
		 * Recoposition locale
		 * @param key une clé de taille dimKey
		 * @param ls une liste de reads compatibles entre eux et avec key
		 * @return une string issue de la fusion des éléments de la liste selon la clé partagée
		 */
		public static String recompose(String key, ArrayList<String> ls) {
			String fusion = ls.get(0);
			for(int i = 1; i<ls.size();i++) {
				fusion = fusionne(fusion,ls.get(i),key);
			}
			return fusion;
		}
		
		/**
		 * Crée une liste d'associations entre clés et valeurs
		 * @return une map contenant les associations entre une clé de taille dimKey et reads correspondant à cette clé
		 */
		public static Map<String, ArrayList<String>> listOrder(){
			Map<String, ArrayList<String>> orderedMap = new HashMap<String,ArrayList<String>>();
			while(!sList.isEmpty()) {
				ArrayList<String> tempList = new ArrayList<String>();
				String key = parser(sList.get(0));
				tempList.add(sList.get(0));
				sList.remove(0);
				for(int i=0;i<sList.size();i++) {
					String tempString = tempList.size()>0?tempList.get(0):sList.get(i);
					if(contient(sList.get(i),key) && fusionne(sList.get(i),tempString,key)!="false") {
						tempList.add(sList.get(i));
						sList.remove(i);
					}
				}
				orderedMap.put(key,tempList);
			}
			return orderedMap;
		}
		
		/**
		 * Création d'une sous-chaine, mot clé de recherche
		 * @param s une chaine
		 * @return les dimKey pb du milieu de la chaine
		 */
		public static String parser(String s) {
			int middle = s.length()/2;
			int percent = dimKey/2;
			return s.substring(middle-percent, middle+percent);
		}
		
		/**
		 * Création d'une sous-chaine, mot clé de recherche
		 * @param s une chaine
		 * @return les 10 premières pb
		 */
		public static String parserStart(String s) {
			return s.substring(0,dimKey);
		}
		
		/**
		 * Création d'une sous-chaine, mot clé de recherche
		 * @param s une chaine
		 * @return les 10 dernières pb
		 */
		public static String parserEnd(String s) {
			return s.substring(s.length()-dimKey,s.length());
		}
		
		
		/**
		 * Sert à vérifier une contenance
		 * @param chaine 
		 * @param cle
		 * @return chaine contient cle
		 */
		public static boolean contient(String chaine, String cle) {
			return chaine.contains(cle);
		}
		
		/**
		 * Prend deux chaines ayant une clé en commun et recompose la concaténation des deux
		 * @param s1 une chaine
		 * @param s2 une chaine
		 * @param key une clé commune
		 * @return la concaténation de s1 et s2 avec recouvrement
		 */
		public static String fusionne(String s1, String s2, String key) {
			int a1 = s1.indexOf(key);
			int a2 = s2.indexOf(key);
			
			// Définition des maps
			ArrayList<Duet> pref = new ArrayList<>();
			ArrayList<Duet> suff = new ArrayList<>();
			
			// on rentre les valeurs
			pref.add(new Duet(s1.substring(0,a1)));
			pref.add(new Duet(s2.substring(0,a2)));
			suff.add(new Duet(s1.substring(a1+key.length(),s1.length())));
			suff.add(new Duet(s2.substring(a2+key.length(),s2.length())));
			
			boolean condPref = false;
			boolean condSuff = false;
			
			// bloc de test
			// permet de déterminer si il y a superposition des préfixes et suffixes possibles
			if(pref.get(0).valueOf()>pref.get(1).valueOf()) {
				condPref=pref.get(0).stringOf().contains(pref.get(1).stringOf());
			}
			else {
				condPref=pref.get(1).stringOf().contains(pref.get(0).stringOf());
			}
			if(suff.get(0).valueOf()>suff.get(1).valueOf()) {
				condSuff=suff.get(0).stringOf().contains(suff.get(1).stringOf());
			}
			else {
				condSuff=suff.get(1).stringOf().contains(suff.get(0).stringOf());
			}
			boolean cond = condPref && condSuff;
			
			// on vérifie si il y a superposition possible
			if(cond) {
				int i = 0;
				String memPref = "";
				for(Duet e : pref) {
					if(e.valueOf()>i) {
						i = e.valueOf();
						memPref = e.stringOf();
					}
				}
				int f = 0;
				String memSuff = "";
				for(Duet e : suff) {
					if(e.valueOf()>f) {
						f = e.valueOf();
						memSuff = e.stringOf();
					}
				}
				return memPref + key + memSuff;
			}
			// pas de superposition possible
			else {
				return "false";
			}
		}
		
	}

