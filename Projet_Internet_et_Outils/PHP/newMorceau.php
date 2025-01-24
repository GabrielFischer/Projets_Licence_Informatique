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

<section class="login-box">

<h1>Parlez-nous un peu de ce morceau</h1>

<form action="" method="post" onsubmit="alert('Votre demande à bien été prise en compte, merci !')">

    <label for="nom">Nom<sup>*</sup></label>
    <input type="text" name="nom" maxlength='100' required><br>

    <label for="artiste">Artiste<sup>*</sup></label>
    <input type="text" name="artiste" maxlength='60' required><br>

    <label for="checkbox">Un ou des featurings?</label>
    <input type="checkbox" id="checkbox">

    <div id="champsTexteContainer">
    <label for="feat1" id="labelFeat1">Featuring n°1 :</label>
    <input type="text" id="feat1" name="feat1" maxlength='60'>
    <label for="feat2" id="labelFeat2">Featuring n°2 :</label>
    <input type="text" id="feat2" name="feat2" maxlength='60'>
    <label for="feat3" id="labelFeat3">Featuring n°3 :</label>
    <input type="text" id="feat3" name="feat3" maxlength='60'>
    <label for="feat4" id="labelFeat4">Featuring n°4 :</label>
    <input type="text" id="feat4" name="feat4" maxlength='60'>
    </div>

    <style>
    /* Appliquer du style aux champs de texte cachés */
    #feat1, #feat2, #feat3, #feat4, label[for="feat1"], label[for="feat2"], label[for="feat3"], label[for="feat4"]{
    display: none;
    }
    </style>

    <script>
    // Récupérer les éléments HTML pour afficher dynamiquement feat 1,2,3 et 4
    var checkbox = document.getElementById('checkbox');
    var champsTexteContainer = document.getElementById('champsTexteContainer');
    var feat1 = document.getElementById('feat1');
    var feat2 = document.getElementById('feat2');
    var feat3 = document.getElementById('feat3');
    var feat4 = document.getElementById('feat4');
    var labelFeat1 = document.getElementById('labelFeat1');
    var labelFeat2 = document.getElementById('labelFeat2');
    var labelFeat3 = document.getElementById('labelFeat3');
    var labelFeat4 = document.getElementById('labelFeat4');

    checkbox.addEventListener('change', function() {
        if (checkbox.checked) {
            feat1.style.display = 'block';
            labelFeat1.style.display = 'block';
        }else {
            feat1.style.display = 'none';
            feat2.style.display = 'none';
            feat3.style.display = 'none';
            feat4.style.display = 'none';
            labelFeat1.style.display = 'none';
            labelFeat2.style.display = 'none';
            labelFeat3.style.display = 'none';
            labelFeat4.style.display = 'none';
            feat1.value = "";
            feat2.value = "";
            feat3.value = "";
            feat4.value = "";
        }
        feat1.addEventListener("input", function() {
            if (feat1.value.trim() != ""){
                feat2.style.display = 'block';
                labelFeat2.style.display = 'block';
            }else{
                feat2.style.display = 'none';
                labelFeat2.style.display = 'none';
                feat2.value = "";
                feat3.style.display = 'none';
                labelFeat3.style.display = 'none';
                feat3.value = "";
                feat4.style.display = 'none';
                labelFeat4.style.display = 'none';
                feat4.value = "";
            }
        })
        feat2.addEventListener("input", function() {
            if (feat2.value.trim() != ""){
                feat3.style.display = 'block';
                labelFeat3.style.display = 'block';
            }else{
                feat3.style.display = 'none';
                labelFeat3.style.display = 'none';
                feat3.value = "";
                feat4.style.display = 'none';
                labelFeat4.style.display = 'none';
                feat4.value = "";
            }
        })
        feat3.addEventListener("input", function() {
            if (feat3.value.trim() != ""){
                feat4.style.display = 'block';
                labelFeat4.style.display = 'block';
            }else{
                feat4.style.display = 'none';
                labelFeat4.style.display = 'none';
                feat4.value = "";
            }
        })
    });
    </script>

    <label for="annee">Année de parution<sup>*</sup></label>
    <input type="number" name="annee" max='9999' value="2023"required><br>
    
    <label for="album">Album (Ne rien écrire si c'est un single)</label>
    <input type="text" name="album" maxlength='60'><br>

    <p id="champObligatoire">* = Champs obligatoire</p>
    <input class ="sendbtn" type="submit" value="suite" name="send"><br>
</form>
</section>

<?php
if (isset($_POST["nom"])){
    $nom=htmlspecialchars($_POST["nom"]);
}
if (isset($_POST["artiste"])){
    $artiste=htmlspecialchars($_POST["artiste"]);
}
if (isset($_POST["annee"])){
    $annee=htmlspecialchars($_POST["annee"]);
}
if (isset($_POST["feat1"])){
    $feat1=htmlspecialchars($_POST["feat1"]);
}
if (isset($_POST["feat2"])){
    $feat2=htmlspecialchars($_POST["feat2"]);
}
if (isset($_POST["feat3"])){
    $feat3=htmlspecialchars($_POST["feat3"]);
}
if (isset($_POST["feat4"])){
    $feat4=htmlspecialchars($_POST["feat4"]);
}


if (isset($_POST["album"])){
    $album=htmlspecialchars($_POST["album"]);
}

$connexion = mysqli_connect("localhost", "root", "", "moosik");
if($connexion){
    if (isset($nom)&&isset($artiste)&&isset($annee)&&isset($album)&&!isset($feat1)&&!isset($feat2)&&!isset($feat3)&&!isset($feat4)){
        mysqli_query($connexion, "INSERT INTO new_morceau (nom,artiste,annee,album) VALUES ('$nom','$artiste','$annee','$album');");
    }else if(isset($nom)&&isset($artiste)&&isset($annee)&&isset($album)&&isset($feat1)&&!isset($feat2)&&!isset($feat3)&&!isset($feat4)){
        mysqli_query($connexion, "INSERT INTO new_morceau (nom,artiste,annee,album,feat1) VALUES ('$nom','$artiste','$annee','$album','$feat1');");
    }else if(isset($nom)&&isset($artiste)&&isset($annee)&&isset($album)&&isset($feat1)&&isset($feat2)&&!isset($feat3)&&!isset($feat4)){
        mysqli_query($connexion, "INSERT INTO new_morceau (nom,artiste,annee,album,feat1,feat2) VALUES ('$nom','$artiste','$annee','$album','$feat1','$feat2');");
    }else if(isset($nom)&&isset($artiste)&&isset($annee)&&isset($album)&&isset($feat1)&&isset($feat2)&&isset($feat3)&&!isset($feat4)){
        mysqli_query($connexion, "INSERT INTO new_morceau (nom,artiste,annee,album,feat1,feat2,feat3) VALUES ('$nom','$artiste','$annee','$album','$feat1','$feat2','$feat3');");
    }else if(isset($nom)&&isset($artiste)&&isset($annee)&&isset($album)&&isset($feat1)&&isset($feat2)&&isset($feat3)&&isset($feat4)){
        mysqli_query($connexion, "INSERT INTO new_morceau (nom,artiste,annee,album,feat1,feat2,feat3,feat4) VALUES ('$nom','$artiste','$annee','$album','$feat1','$feat2','$feat3','$feat4');");
    }
}



?>