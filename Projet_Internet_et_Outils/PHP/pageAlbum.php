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
if(!isset($_GET["album"])){
    echo("La recherche à échouée, veuillez recommencer.");
    //AFFICHER LA BARRE DE RECHERCHE
}else{
    if(!isset($_GET["id"])){
        $album=$_GET["album"];
        $album_min = strtolower($album);
        session_start();
        $connexion=mysqli_connect("localhost","root","","moosik");
        if ($connexion){
            $recherche=mysqli_query($connexion,"SELECT * FROM album WHERE LOWER(nom)='$album_min'");
            if(mysqli_num_rows($recherche)!=0){
                //Récuperer l'url courant 
                if(isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on'){ 
                    $url = "https://";   
                }
                else{  
                    $url = "http://";  
                } 
                $url.= $_SERVER['HTTP_HOST'];    
                $url= $_SERVER['REQUEST_URI'];    
                while($extraction=mysqli_fetch_assoc($recherche)){
                    $id=$extraction["id"];
                    $nom=$extraction["nom"];
                    $artiste=$extraction["artiste"];
                    $annee=$extraction["annee"];
                    $feat1=$extraction["feat1"];
                    $feat2=$extraction["feat2"];
                    $feat3=$extraction["feat3"];
                    $feat4=$extraction["feat4"];
                    $new_url = $url.'&id='.$id;
                    echo($new_url);
                    echo("<a href= '$new_url'>$nom </a>");
                    echo("$artiste<br>");
                    echo("$feat1<br>");
                    echo("$feat2<br>");
                    echo("$feat3<br>");
                    echo("$feat4<br>");
                    //ECHO COVER QUAND IL Y EN AURA UNE
                    echo("<br>");
                }
            }else if(mysqli_num_rows($recherche)==0){
                echo("Oops, nous n'avons pas trouvé de single à ce nom, veuillez vérifiez l'orthographe");
                //AFFICHER LA BARRE DE RECHERCHE
            }
        }
    }else{
        $album=$_GET["album"];
        $id=$_GET["id"];
        session_start();
        $connexion=mysqli_connect("localhost","root","","moosik");
        if ($connexion){
            $recherche=mysqli_query($connexion,"SELECT * FROM album WHERE id='$id'");
            if(mysqli_num_rows($recherche)!=0){
                $extraction=mysqli_fetch_assoc($recherche);
                    $nom=$extraction["nom"];
                    $artiste=$extraction["artiste"];
                    $annee=$extraction["annee"];
                    echo("$nom<br>");
                    echo("$artiste<br>");
                    if($extraction["feat1"]!=""){
                        echo("Featuring: ");
                        echo($extraction["feat1"]);
                    }
                    if($extraction["feat2"]!=""){
                        echo($extraction["feat2"]);
                    }
                    if($extraction["feat3"]!=""){
                        echo($extraction["feat3"]);
                    }
                    if($extraction["feat4"]!=""){
                        echo($extraction["feat4"]);
                    }
                    echo("<br>");
                    echo("$annee<br>");
                    echo("<br>");
                    //ECHO COVER QUAND IL Y EN AURA UNE
                    echo("<br>");
                    $liste_morceaux=mysqli_query($connexion,"SELECT * FROM morceau WHERE artiste='$artiste' AND album='$nom'");
                    if (mysqli_num_rows($liste_morceaux)!=0){
                        while($row=mysqli_fetch_assoc($liste_morceaux)){
                            $morceau=$row["nom"];
                            echo("$morceau<br>");
                        }
                    }
            }
        }
    }

}

?>

</section>