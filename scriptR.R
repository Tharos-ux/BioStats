# projet stats

# on change le dossier pour charger le jeu de données
setwd("~/Java/BioStats/")

# on charge le jeu de données
donnees = read.table("data.csv", header = T, sep = ",")
head(donnees)