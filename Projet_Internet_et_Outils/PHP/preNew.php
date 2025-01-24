<? 

session_start();
if(!isset($_SESSION['mail'])){
    header("location: ../HTML/Profil/logIn.hmtl");
    exit;
}
?>
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

<h1>Super, du nouveau ! Qu'avez vous à nous proposer ?</h1>
<p><i>Ce site est communautaire, il nous faut donc des gens comme vous pour nous tenir au courant des dernières sorties !</i></p>
<form action="" method="post">
    <input type="radio" value="morceau" name="preNew" checked>
    <label for="morceau">Un nouveau morceau</label><br>
    <input type="radio" value="album" name="preNew">
    <label for="album">Un nouvel album</label><br>
    <input type="radio" value="artiste" name="preNew">
    <label for="artiste">Un nouvel artiste</label><br><br>
    <input class ="sendbtn" type="submit" value="suite" name="send"><br>
</form>
</section>

<?php
if (isset($_POST["preNew"])){
    $choix = $_POST["preNew"];
}
if (isset($choix)){
    if (($choix)=="morceau"){
        header("location:newMorceau.php");
        exit;
    }else if(($choix)=="album"){
        header("location:newAlbum.php");
        exit;
    }else if(($choix)=="artiste"){
        header("location:newArtiste.php");
        exit;
    }
}