Notre projet porte sur le jeu "Slice and Dice". Nous avons donc décidé de garder l'âme du jeu, tout en l'améliorant avec nos idées personnelles.

Ce jeu est un rpg, et a pour nouveauté des dés pouvant se lancer, déterminant les attaques/pouvoirs des héros que contrôle le joueur. Il y a donc une phase de lancement des dés, où le joueur détermine ses prochaines attaques et défenses. La deuxième phase est la phase des ennemis, où ils attaquent à leur tour.
Le jeu se décompose en niveaux, où le joueur devra vaincre les ennemis jusqu'au boss final. Le joueur contrôle donc des héros, pouvant être améliorés au fur et à mesure qu'il avance dans les niveaux.

Nous sommes une équipe de cinq à travailler sur le projet: JOHNSON Michael, WITZMANN Kai, FISCHER Gabriel, POPOVIC Mila et TOMAS Andréa.

Pour ouvrir le jeu en utilisant le fichier run.sh (pour Linux):
1. Vous devez ouvrir le terminal dans le dossier où se trouve le jeu (dossier slice-and-dice)
2. Dans le terminal, vous devez d'abord taper chmod +x run.sh pour rendre le fichier exécutable
3. Ensuite, vous devez écrire ./run.sh

Pour ouvrir le jeu en utilisant l'éxecutable .exe (pour Windows):
Lancez l'éxecutable SliceAndDice.exe à la racine du projet
Si l'éxecution du .exe affiche une erreur, essayez de mettre le dossier du jeu sur votre bureau directement

Nous vous conseillons de jouer en difficulté normale, qui nous semble être la mieux optimisée
Vous pouvez sauvegarder votre partie avant de quitter en appuyant sur la disquette en haut de l'écran puis "sauvegarder"
Vous pourrez ensuite recharger votre partie en rappuyant sur la même icone puis "charger"


Les commandes permettant de lancer notre projet sur le terminal sont les suivantes:

Pour Linux: 

javac -sourcepath src/ -d out/ src/main/Main.java

java -classpath out/:res/ main.Main

Pour Windows:

javac -sourcepath src\ -d out\ src\main\Main.java

java -Xdiag -classpath "%CD%\out\";"%CD%\res\" main.Main

