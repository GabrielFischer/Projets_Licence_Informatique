<?php
if (isset($_POST["prePost"])){
    $choix=$_POST["prePost"];
}

if (isset($choix)){
    if (($choix)=="texte"){
        header("location:../../Html/postTexte.html");
        exit;
    }else if(($choix)=="musique"){
        header("location:../../Html/postMusique.html");
        exit;
    }else if(($choix)=="album"){
        header("location:../postAlbum.php");
        exit;
    }else if(($choix)=="morceau"){
        header("location:../postMorceau.php");
        exit;
    }
}
?>