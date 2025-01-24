# Architecture du projet

## Stratégie utilisée pour répondre à la question posée
Le début du projet se focalisant sur l'implémentation des commandes internes, nous avons opté pour qu'un membre de l'équipe soit assigné à une commande interne (ou 2 selon l'envie). Le point essentiel qu'il faut retenir est que, malgré l'implication de tous les membres du projet dans la conception théroqiue de chacune des fonctions programmées, nous avons opté pour que seul celui en charge de la programmation de la dite fonction l'implémente, au moins au départ. Cette manière de procéder a été choisie (par expérience) pour éviter au maximum les confrontations dûes aux manières différentes de programmer, notamment. La répartition des tâches quant à elle s'est faite chaque semaine, en fonction de l'avancée de chacun sur ses propres tâches, et en tenant compte des aptitudes de chacun vis-à-vis de la tâche à effectuer.

## Architecture logicielle
### Avant lancement du programme
Ici, il s'agit d'un programme en langage C uniquement qui simule un invite de commande, ou shell.
Le dossier src contient tous les fichiers de code source .c, chacun présentant le code qui gère la commande interne/la partie dont il porte le nom.
    Exemple : exit.c contient le code (en l'occurrence la fonction exit_perso) qui est utilisé lorsque la commande exit est entrée par l'utilisateur.
    redirection.c contient les fonctions qui sont utilisées lorsqu'une redirection est entrée par l'utilisateur.
    Etc.
Un fichier header.h est également contenu dans src, il contient les prototypes de nombreuses fonctions, de toutes les bibliothèques utilisées, ainsi que l'inititalisation de nombreuses MACROS, tous utilisés dans les fichiers qui contient l'inclusion de l'en-tête "header.h" .
Les fichiers makefile, test et test.sh correspondent quant à eux à la réalisation des tests.
Enfin, un répertoire Comptes_rendus est également présent, ce-dernier contient les comptes rendus hebdomadaires que nous avons décidé de rédiger (sur le modèle du projet de second semestre de L2). Ces-derniers permettaient à tous de se retrouver dans l'avancée du projet, tout en mettant à plat les idées, problèmes et solutions qui avaient été trouvés durant la semaine. Les compte rendus permettaient également d'accéder facilement à la répartition des tâches pour la semaine.

### Une fois le programme lancé
Le programme se lance à la racine du projet avec la commande ./fsh. Une fois lancé, un prompt s'affiche, on peut alors taper les commandes que l'on veut. L'exécution du programme démarre dans le main de fsh.c . fsh.c est le fichier "principal", c'est lui qui contient le code qui gère tout ce peut se passer avant, entre, et après l'entrée de commandes. fsh.c contient docn toutes les fonctions nécessaires à la réception des commandes, à son décorticage, ainsi qu'à la réception des signaux, entre autres. 

## Structure de données
Le projet utilise des types de variable les plus simples possibles, et évite de faire appel à des structures trop complexes. Ainsi, chaque étape d'un processus correspond à une fonction, et si certaines d'entre elles dépassent un certain nombre de lignes et pourraient probablement être réduites, la très grande majorité d'entre elles sont courtes et exécutent une seule tâche à la fois. Le programme comporte bon nombre de fonctions, dont une bonne partie d'entre elles, bien qu'elles soient déclarées et utilisées dans le même fichier, peuvent et sont utilisées dans d'autres fichiers que ceux auxquels elles étaient déstinées à la base. Nos fonctions sont donc modulaires.

### Algorithmes utilisés 
Nous n'avons pas consciemment utilisé d'algorithmes spécifiques, bien que certaines partie du programme pourraient sans doute s'y apprêter.