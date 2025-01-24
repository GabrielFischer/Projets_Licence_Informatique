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
<div class="login-box">
<h1>Nouveau post</h1>



<?php
if(isset($_GET['artiste'])){
    $artiste=htmlspecialchars($_GET["artiste"]);
}
if(isset($_GET['album'])){
    $album=htmlspecialchars($_GET["album"]);
}
if (isset($album)&&isset($artiste)){
    $connexion = mysqli_connect("localhost", "root", "", "moosik");
    session_start();
    if($connexion->connect_error) {
        die("Erreur de connexion : " . $connexion->connect_error);
    }

    if(isset($_SESSION['mail'])){
        $restmp = mysqli_query($connexion,"SELECT * FROM users WHERE mail= '{$_SESSION['mail']}';");
        $tmp = $restmp->fetch_assoc();
        $userid = $tmp['id'];
        $exist=mysqli_query($connexion, "SELECT * FROM album WHERE nom='$album' AND artiste='$artiste';");
        if (mysqli_num_rows($exist)!=0){

            $result = mysqli_query($connexion, "SELECT * FROM posts WHERE userId='$userid' AND titre='$album' AND artiste='$artiste';");

            if(mysqli_num_rows($result)!=0){
                $note_enr=mysqli_fetch_assoc($result);
                $note_enr=$note_enr["etoiles"];
                $resultbis = mysqli_query($connexion, "SELECT * FROM posts WHERE userid='$userid' AND titre='$album' AND artiste='$artiste';");
                $avis_enr=mysqli_fetch_assoc($resultbis);
                
                $avis_enr=$avis_enr["texte"];
                echo("<h2>Vous avez déjà noté cet album</h2><br>");
                echo("<p>La note que vous lui avez attribué: </p>");
                echo("$note_enr<br>");
                if(isset($avis_enr)){
                    echo("<p>L'avis que vous lui avez attribué: </p>");
                    echo("$avis_enr <br>");
                }
                echo("<a href='modifAlbum.php?album=$album&artiste=$artiste'>Modifier</a> <br> ");

            }else{
                echo("<form action='' method='post'>
                <label for='note' id='labelNote'>Quelle note attribuez-vous à cet album?</label>
                <select name='note' id='note' required>
                    <option value='' selected>Veuillez sélectionner une note</option>
                    <option value='0'>0★</option>
                    <option value='1'>1★</option>
                    <option value='2'>2★</option>
                    <option value='3'>3★</option>
                    <option value='4'>4★</option>
                    <option value='5'>5★</option>
                    <option value='6'>6★</option>
                    <option value='7'>7★</option>
                    <option value='8'>8★</option>
                    <option value='9'>9★</option>
                    <option value='10'>10★</option>
                </select><br><br>

                <label for='texte'>Expliquez-nous ce choix</label>
                <input type='text' id='texte' name='texte' maxlength='400'><br>
                <br> <br><input class =\"sendbtn\" type=\"submit\" value=\"envoyer\"><br><br>
                </form>");
                
                if(isset($_POST["note"])){
                    $note=$_POST["note"];
                }
                if(isset($_POST["texte"])){
                    $texte=htmlspecialchars($_POST["texte"]);
                    $texte=mysqli_real_escape_string($connexion, $texte);
                }
                if(isset($_GET['artiste'])){
                    $artiste=$_GET["artiste"];
                }
                if(isset($_GET['album'])){
                    $album=$_GET["album"];
                }
                if (isset($album)&&isset($artiste)&&isset($note)&&isset($texte)){
                    $connexion = mysqli_connect("localhost", "root", "", "moosik");
                   
                    if($connexion->connect_error) {
                        die("Erreur de connexion : " . $connexion->connect_error);
                    }
                
                    if(isset($_SESSION['mail'])){
                        $result = mysqli_query($connexion, "SELECT * FROM posts WHERE userId='$userid' AND titre='$album' AND artiste='$artiste';");
                
                        if(mysqli_num_rows($result)==0){
                            mysqli_query($connexion, "INSERT INTO posts (type,Titre,Artiste,userId,etoiles,texte) VALUES('avis_album','$album','$artiste','$userid','$note','$texte')");
                            header("location:index.php");
                        }
                    }else{
                        header("location:Profil/LogIn.php");
                    }
                }

            }
        }else{
            echo("Oops, il semblerait que nous n'avons pas réussi à trouver cet album.");
        }
    }else{
        header("Location:Profil/LogIn.php");
    }
}else{
    echo("Oops, il semblerait que nous n'avons pas réussi à trouver cet album.");
}
?>
</div>



