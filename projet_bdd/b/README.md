CSV contient des fichiers de données csv que nous générons avec les fichiers python contenus dans src. 
evenements.csv contient les données utilisées pour remplir les tables evenement notamment.
profils_utilisateurs.csv est le fichier principal de données des utilisateurs et de leurs profils.
profils_utilisateursEX.csv n'est qu'une autre version de cette génération

TOUS LES SCRIPT SQL DOIVENT ÊTRE LANCES DEPUIS le répertoire b.

src contient les fichiers de code source.
evenements.py generateur.py ainsi que generateurEX.py sont simplement des fichiers pythons présents pour générer des données.
script.sql correspond au fichier à lancer en premier. Il permet la création de toutes les tables permanentes de notre base de données.
import_data.sql correspond au deuxième fichier à lancer. Il importe les données des fichiers csv vers les tables crées par script.sql. Il
gère également l'insertion de nouvelles données générées au moment du remplissage de certaines tables.
requetes.sql est le fichier sql qui contient toutes les requêtes sur lequelles nous avons travaillé durant ce projet. Vous remarquerez que
tout est commenté, c'est normal. Ce script se lance avec une requête décommentée à la fois pour éviter de ne voir que le résultat de la dernière requête effectuée. On prendra soin de décommenter la requête que l'on souhaite exécuter, puis de la recommenter avant de passer à la suite.