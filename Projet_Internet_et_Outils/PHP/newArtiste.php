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

<h1>Qui est donc cet artiste?</h1>

<form action="" method="post" onsubmit="alert('Votre demande à bien été prise en compte, merci !')">

    <label for="nom">Nom<sup>*</sup></label>
    <input type="text" name="nom" required><br>

    <p id="qlqMorceaux">Donnez-nous quelques morceaux sortis par cet artiste sur les plateformes</p>

    <div id="champsTexteContainer">
    <label for="morceau1" id="labelmorceau1">Morceau n°1 <sup>*</sup></label><br>
    <input type="text" id="morceau1" name="morceau1" maxlength='100' required>
    <label for="morceau2" id="labelmorceau2">Morceau n°2 :</label>
    <input type="text" id="morceau2" name="morceau2" maxlength='100'>
    <label for="morceau3" id="labelmorceau3">Morceau n°3 :</label>
    <input type="text" id="morceau3" name="morceau3" maxlength='100'>
    <label for="morceau4" id="labelmorceau4">Morceau n°4 :</label>
    <input type="text" id="morceau4" name="morceau4" maxlength='100'>
    </div>

    <style>
    /* Appliquer du style aux champs de texte cachés */
    #morceau2, #morceau3, #morceau4, label[for="morceau2"], label[for="morceau3"], label[for="morceau4"]{
    display: none;
    }
    </style>

    <script>
    // Récupérer les éléments HTML pour afficher dynamiquement morceaux 1,2,3 et 4
    var champsTexteContainer = document.getElementById('champsTexteContainer');
    var morceau1 = document.getElementById('morceau1');
    var morceau2 = document.getElementById('morceau2');
    var morceau3 = document.getElementById('morceau3');
    var morceau4 = document.getElementById('morceau4');
    var labelmorceau1 = document.getElementById('labelmorceau1');
    var labelmorceau2 = document.getElementById('labelmorceau2');
    var labelmorceau3 = document.getElementById('labelmorceau3');
    var labelmorceau4 = document.getElementById('labelmorceau4');
        
        morceau1.addEventListener("input", function() {
            if (morceau1.value.trim() != ""){
                morceau2.style.display = 'block';
                labelmorceau2.style.display = 'block';
            }else{
                morceau2.style.display = 'none';
                labelmorceau2.style.display = 'none';
                morceau2.value = "";
                morceau3.style.display = 'none';
                labelmorceau3.style.display = 'none';
                morceau3.value = "";
                morceau4.style.display = 'none';
                labelmorceau4.style.display = 'none';
                morceau4.value = "";
            }
        })
        morceau2.addEventListener("input", function() {
            if (morceau2.value.trim() != ""){
                morceau3.style.display = 'block';
                labelmorceau3.style.display = 'block';
            }else{
                morceau3.style.display = 'none';
                labelmorceau3.style.display = 'none';
                morceau3.value = "";
                morceau4.style.display = 'none';
                labelmorceau4.style.display = 'none';
                morceau4.value = "";
            }
        })
        morceau3.addEventListener("input", function() {
            if (morceau3.value.trim() != ""){
                morceau4.style.display = 'block';
                labelmorceau4.style.display = 'block';
            }else{
                morceau4.style.display = 'none';
                labelmorceau4.style.display = 'none';
                morceau4.value = "";
            }
        })
    </script>

    <p id="champObligatoire">* = Champs obligatoire</p>
    <input class ="sendbtn" type="submit" value="suite" name="send"><br>
</form>
</section>

<?php
if (isset($_POST["nom"])){
    $nom=htmlspecialchars($_POST["nom"]);
}

if (isset($_POST["morceau1"])){
    $morceau1=htmlspecialchars($_POST["morceau1"]);
}
if (isset($_POST["morceau2"])){
    $morceau2=htmlspecialchars($_POST["morceau2"]);
}
if (isset($_POST["morceau3"])){
    $morceau3=htmlspecialchars($_POST["morceau3"]);
}
if (isset($_POST["morceau4"])){
    $morceau4=htmlspecialchars($_POST["morceau4"]);
}

$connexion = mysqli_connect("localhost", "root", "", "moosik");
if($connexion){
    if (isset($nom)&&!isset($morceau1)&&!isset($morceau2)&&!isset($morceau3)&&!isset($morceau4)){
        mysqli_query($connexion, "INSERT INTO new_artiste (nom) VALUES ('$nom');");
    }else if(isset($nom)&&isset($morceau1)&&!isset($morceau2)&&!isset($morceau3)&&!isset($morceau4)){
        mysqli_query($connexion, "INSERT INTO new_artiste (nom,morceau1) VALUES ('$nom','$morceau1');");
    }else if(isset($nom)&&isset($morceau1)&&isset($morceau2)&&!isset($morceau3)&&!isset($morceau4)){
        mysqli_query($connexion, "INSERT INTO new_artiste (nom,morceau1,morceau2) VALUES ('$nom','$morceau1','$morceau2');");
    }else if(isset($nom)&&isset($morceau1)&&isset($morceau2)&&isset($morceau3)&&!isset($morceau4)){
        mysqli_query($connexion, "INSERT INTO new_artiste (nom,morceau1,morceau2,morceau3) VALUES ('$nom','$morceau1','$morceau2','$morceau3');");
    }else if(isset($nom)&&isset($morceau1)&&isset($morceau2)&&isset($morceau3)&&isset($morceau4)){
        mysqli_query($connexion, "INSERT INTO new_artiste (nom,morceau1,morceau2,morceau3,morceau4) VALUES ('$nom','$morceau1','$morceau2','$morceau3','$morceau4');");
    }
}



?>