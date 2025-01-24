<?php 
    session_start();
    if(!isset($_SESSION['mail'])){
        header("location: ../../Html/Profil/logIn.html");
        exit;
    }
    $connexion = mysqli_connect("localhost", "root", "", "moosik");
    $titre = htmlspecialchars($_POST[('titre')]); //Pour se protéger contre les attaques XSS
    $texte = htmlspecialchars($_POST[('texte')]);
    if(isset($titre)||isset($texte)){
        $res = mysqli_query($connexion,"SELECT * FROM users WHERE mail= '{$_SESSION['mail']}';");
        $row = $res->fetch_assoc();
        $userId= $row['id'];
        mysqli_query($connexion, "INSERT INTO posts (type,Titre,userId,texte) VALUES('article','$titre','$userId','$texte')");
        header("location:../index.php");
        exit;
    }
    else{
        header("location:../../Html/postTexte.html");
        exit;
    }
?>