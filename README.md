# BioStats

*Travail réalisé dans le cadre d'une UE de bioinformatique et de biostatistiques, le code est de mon fait, et j'inclus ici les parts de rapport dont je suis responsable uniquement.*

# Introduction

Au cours de ce projet, nous allons tenter de dégager des relations entre composantes matérielles d'un terminal et exécution de scripts de manipulation de données au format fasta. Etant dans le domaine de la bio-informatique, la **manipulation de données en haut-débit** s'annonce comme un des enjeux de la biologie de demain. Pouvoir exécuter en toute circonstance, sur un ordinateur personnel, des outils bio-informatiques est un enjeu de flexibilité notable.
Aussi, c'est dans ce cadre que s'inscrit notre étude : tenter une approche empirique pour déterminer quelles caractéristiques matérielles d'un terminal personnel vont sensiblement augmenter le volume traité par unité de temps, c'est-à-dire minimiser le temps d'exécution.

# Protocole de relevé des données

Afin d'acquérir les données qui vont être utilisées dans le cadre de cette étude, nous avons exécuté un programme développé en Java, effectuant des **alignements de séquence ADN** sur de multiples terminaux, qui avait été développé dans le cadre d'une UE de bioinformatique proposée par l'ISTIC. La boucle principale d'exécution est la suivante ; il vous est possible de consulter le [Github du projet](https://github.com/Tharos-ux/BioStats) où le code source Java est disponible dans son intégralité.

Basiquement, il s'agit d'assemblages de reads deux à deux avec un système d'ancre qui s'aligne contre une séquence et cherche à aligner à droite et à gauche sur la séquence en construction, ancres étant calculées pour chacun des reads et ces reads accédés via une fonction de hachage.

```{java}
  /**
	 * main loop
	 * @param executions number of times calculations are done
	 * @return maximum CPU temperature in degrees
	 */
	public static double executionCode(int executions) {
		double averageTemp = 0;
		ArrayList<Double> temps = new ArrayList<Double>();
		long startTime;
		averageTemp = sensor.getCpuTemperature();
		for(String s : pathList) {
			// Save command starting time
			startTime = System.nanoTime();
			for(int i=0;i<executions;i++) {
				Backend.exec(Charge.load(s),keySize);
				temps.add(sensor.getCpuTemperature());
			}
			// Print out processing time
			System.out.println(s.substring(11,s.length()-6) +" --> " + ((System.nanoTime()-startTime)/executions) / 1000000+" ms");
			tableArgs.add(Long.toString(((System.nanoTime()-startTime)/executions) / 1000000));
		}
		return averageTemp>=Collections.max(temps)?averageTemp:Collections.max(temps);
	}
		
```
Le protocole de récolte était le suivant :

* Installation de l'IDE Eclipse sur la machine à tester
* Installation du JRE 17 pour permettre l'utilisation de la JVM
* Exécution du programme
  + Récupération de la température initiale
  + Capture de l'heure système
  + Exécution de n alignements, répétés 5 fois, en stockant la température maximale
  + Capture de l'heure de fin, et donc calcul de la durée d'exécution moyenne en divisant par le nombre d'exécutions
  + Collecte des informations de configuration matérielle
  + Demande à l'utilisateur pour enregistrer
* Enregistrement des données à la suite d'un fichier .csv

Les variables système collectées sont les suivantes :

<details>
  <summary>Afficher les variables</summary>
| Intitulé                | Type de donnée | Format de donnée     |
|-------------------------|----------------|----------------------|
| Temperature.maximale    | Quantitative   | Flottant signé       |
| Delta.temperature       | Quantitative   | Flottant signé       |
| Nom.processeur          | Qualitative    | Chaîne de caractères |
| Cadence.horloge         | Quantitative   | Entier continu       |
| Architecture.processeur | Qualitative    | Chaîne de caractères |
| Fabircant.processeur    | Qualitative    | Chaîne de caractères |
| Microarchitecture       | Qualitative    | Chaîne de caractères |
| Processeurs.physiques   | Quantitative   | Entier discret       |
| Processeurs.logiques    | Quantitative   | Entier discret       |
| Processeurs.JVM         | Quantitative   | Entier discret       |
| Mémoire.JVM             | Quantitative   | Entier continu       |
| Mémoire.totale          | Quantitative   | Entier discret       |
| Vitesse.memoire         | Quantitative   | Entier discret       |
| Type.memoire            | Qualitative    | Chaîne de caractères |
| OS                      | Qualitative    | Chaîne de caractères |
</details>


L'intégralité du code s'exécutant à l'intérieur d'une boucle, il est aisé de mesurer des **temps d'exécution** pour différentes tailles de fichiers d'entrée (n = 7, on a sept tailles de reads différentes).
Notre étude va dans ce sens : via la collecte de durées de calcul d'une tâche commune, nous allons tenter de montrer un lien entre configuration matérielle et rapidité d'alignement.

Les données ont été collectées sur un ensemble d'ordinateurs personnels et de terminaux présents dans différentes salles informatiques du campus de Rennes 1.

## Hypothèses

Sur une station de travail, on dit généralement que les trois points pouvant limiter l'exécution d'un programme sont : **la vitesse du stockage**, **la vitesse et la quantité de la mémoire physique**, **le nombre d'opértions processeur** par unité de temps. Ici, le stockage est le même dans toutes les configurations (clé USB 3.0 branchée sur port USB 3.0, constante à travers les échantillons), on peut donc écarter ce paramètre.
Dans la pratique, le port USB n'aura pas le même débit exactement d'un terminal à l'autre, dépendant notamment de l'encombrement du bus au moment de l'exécution, mais travaiilant sur des fichiers de petite taille (inférieur à 10 Mo, temps de chargement = 0,02 seconde en moyenne) on peut considérer ce biais comme négligeable en comparaison à des alignements durant en moyenne 16,828 secondes et répétés 5 fois sans rechargement de la donnée à chaque alignement.

On va poser les hypothèses suivantes :

* La température et la delta température sont liées au processeur, et non à la charge logicielle
* Le nombre de coeurs dédiés a un impact sur la performance
* La cadence de l'horloge a un impact sur la performance
* La vitesse mémoire a un impact sur la performance
* La quantité de mémoire a un impact sur la performance

Il semble d'après nos résultats que le temps d'exécution de l'algorithme pour une séquence de 100000 pb soit en corrélation avec la cadence de l'horloge.
Donc lorsque la cadence de l'horloge est grande, le temps d'exécution a tendance à augmenter, ce qui pourrait sembler contre-intuitif mais qui pourrait être une illustration du bottlenecking si l'on arrive à montrer un autre paramètre matériel limitant.

A l'inverse, les processeurs physiques, logiques, JVM, la vitesse mémoire et la mémoire totale semblent être anticorrélés avec le temps d'exécution. Donc plus ces variables sont basses, plus le temps d'exécution diminuera.
Cela permet donc d'avancer que la mémoire (en vitesse comme en quantité) est le point limitant pour l'exécution d'un alignement de séquence, et que l'on observe une baisse de performance pour une grande cadence d'horloge uniquement parce que la mémoire devient facteur limitant des temps de calcul.

Comme les processeurs logiques et JVM donnent des flèches superposées, pour des questions de lisibilité du graphe, la variable processeurs logiques a été supprimée de la table

Quant à la température maximale, le delta température et la mémoire JVM, ces variables ne semblent pas avoir d'effet sur le temps d'exécution.

# Pour aller un peu plus loin : le bottlenecking

> **Définition :**
> Point d’un système informatique limitant les performances, point qui peut être matériel ou logiciel.
> *[Source : wiktionary.org](https://fr.wiktionary.org/wiki/bottleneck#en)*
  
  
S'inscrivant dans le cadre de la **théorie des contraintes**, le **bottlenecking** est un phénomène causé par un endiguement d'une sous-partie d'un système. Si on imagine une tour de plusieurs entennoirs avec des diamètres de sortie différents, on imagine sans peine que l'étage ayant le plus petit diamètre ne va pas pouvoir déverser un débit suffisant, et va à terme déborder : ce point d'engorgement serait le bottleneck de notre système d'entennoirs.

Ici, on travaille sur des composants informatiques : un système composé de mémoire vive, d'un processeur et d'un périphérique de stockage. Comme on a défini le stockage comme négligeable, ce qui s'est avéré confirmé par nos analyses, on se retrouve dans une situation où on place en facteur limitant la mémoire ou le processeur. Sans grande surprise, vu que l'on travaille sur des alignements de données de grande taille et que l'on fait donc peu d'opérations pour beaucoup d'accès mémoire, c'est la mémoire vive qui se place en facteur limitant, ce qui est confirmé par les analyses en composante principale.

# Conclusion

Dans cette étude, nous avons pu montrer qu'il existait bien un lien entre taille de donnée d'entrée et temps d'exécution, ce qui est le postulat de base du calcul de **complexité algorithmique** ; et que empiriquement on peut en montrer tant les limites (les capacités de la machine influent bien sur la courbe) que le principe global (chaque ordinateur, pris indépendament, montre une courbe de temps d'exécution en fonction de taille de donnée tendant vers log(n) si on pose n comme taille de donnée d'entrée)

Par ailleurs, on a pu montrer que certains paramètres étaient en lien (tels que la vitesse mémoire) d'autres semblaient être juste caractéristiques du terminal et n'influaient pas la performance (température processeur), et qu'enfin on avait d'une part des limitants (facteurs qui quelle que soit l'augmentation de ceux-ci n'amenaient pas à un optimum, la mémoire par exemple) et d'autres qui se trouvaient à être limités (facteurs qui, au delà d'un certain seuil, n'augmentaient plus la vitesse de calcul comme le nombre dopérations processeur par unité de temps par exemple)
