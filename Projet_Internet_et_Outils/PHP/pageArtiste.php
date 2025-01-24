<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MOOSIK</title>
    <link rel="stylesheet" href="../CSS/stylesheet.css">
</head>
<header>
        <nav >
            <ul id="login-box">
                <li>
                    <a href="index.php"><img id ="logo" src="../Images/logo_moosik.png" alt="logo_moosik"></a>
                </li>
            </ul>
        </nav>
</header>

<section class ="page">

<?php
if(!isset($_GET["artiste"])){
    echo("La recherche à échouée, veuillez recommencer.");
    //AFFICHER LA BARRE DE RECHERCHE
}else{
    $artiste=$_GET["artiste"];
    $artiste_min = strtolower($artiste); //Mettre le nom en minuscule 
    session_start();
    $connexion=mysqli_connect("localhost","root","","moosik");
    if ($connexion){
        $recherche=mysqli_query($connexion,"SELECT * FROM artiste WHERE LOWER(nom)='$artiste_min'");
        if(mysqli_num_rows($recherche)==1){
            $extraction=mysqli_fetch_assoc($recherche);
            $nb_morceaux=$extraction["nb_morceaux"];
            $nb_albums=$extraction["nb_albums"];
            echo("$artiste<br>");
            //ECHO PDP QUAND IL Y EN AURA UNE
            echo("$nb_morceaux<br>");
            echo("$nb_albums<br>");
            echo("<br>");
            $album=mysqli_query($connexion,"SELECT * FROM album WHERE LOWER(artiste)='$artiste'");
            if (mysqli_num_rows($album)!=0){
                echo("Albums: <br>");echo("<br>");
                while($row=mysqli_fetch_assoc($album)){
                    echo($row["nom"]);echo("<br>"); //Faire un lien vers la page de l'album
                    echo($row["annee"]);echo("<br>");
                    echo("<br>");

                }
                echo("<br>");
            }
            $single=mysqli_query($connexion,"SELECT * FROM morceau WHERE LOWER(artiste)='$artiste' AND album=''");
            if (mysqli_num_rows($single)!=0){
                echo("Singles: <br>");echo("<br>");
                while($row=mysqli_fetch_assoc($single)){
                    echo($row["nom"]);echo("<br>"); //Faire un lien vers la page du single
                    echo($row["annee"]);echo("<br>");
                }
                echo("<br>");
            }
        }else if(mysqli_num_rows($recherche)==0){
            echo("$artiste<br>");
            echo("Oops, nous n'avons pas trouvé d'artiste à ce nom, veuillez vérifiez l'orthographe");
            //AFFICHER LA BARRE DE RECHERCHE
        }else if(mysqli_num_rows($recherche)>1){
            echo("afficher tout les artistes portant ce nom");
            //AFFICHER TOUT LES ARTISTES PORTANT CE NOM AVEC LEUR PHOTO DE PROFIL ET LA POSSIBILITE DE CLIQUER DESSUS POUR ACCEDER A LEUR PAGE
        }
    }

}

?>

</section>