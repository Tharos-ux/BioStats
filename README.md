# BioStats

L'algo de base sera expliqué dans une section un peu plus basse.
Basiquement, il convient de savoir qu'il s'agit d'un assembleur de chaines d'ADN, fonctionnant sur des reads de différentes tailles.
Les paramètres keySize et execs ont un impact direct sur le temps d'exécution.
Execs décrit le nombre d'itérations pour le calcul du temps moyen par opération.
KeySize détermine la longueur découpée dans les reads pour l'appariement.

La classe main contient l'intégralité des méthodes récupérant les paramètres d'environnement, ce à l'aide de deux librairies : OSHI et SYSMON.

![alt text](https://media.discordapp.net/attachments/888034160265007174/889235961966387260/unknown.png)

Les fonctionnalités d'enregistrement automatique au format CSV ne sont pas encore gérées.

![alt](https://media.discordapp.net/attachments/888034160265007174/889246734990069760/unknown.png)

Il semble peu possible de pouvoir obtenir automatiquement l'état mémoire sous Linux, vu qu'il faut les permissions sudo pour obtenir de telles données.

Docs utiles !
https://javadoc.io/doc/com.outr.javasysmon/javasysmon_2.10/latest/index.html
https://openhardwaremonitor.org/downloads/
https://javadoc.io/doc/com.github.oshi/oshi-core/latest/index.html
