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
        $artiste=$_GET["artiste"];
    }
    if(isset($_GET['morceau'])){
        $morceau=$_GET["morceau"];
    }
    if (isset($morceau)&&isset($artiste)){
        $connexion = mysqli_connect("localhost", "root", "", "moosik");
        session_start();
        if($connexion->connect_error) {
            die("Erreur de connexion : " . $connexion->connect_error);
        }

        if(isset($_SESSION['mail'])){
            echo("<form action='' method='post'>
            <label for='note' id='labelNote'>Quelle note attribuez-vous à cet morceau?</label>
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
            <input type='text' id='texte' name='texte'><br>
            <br><input class =\"sendbtn\" type=\"submit\" value=\"envoyer\"><br><br>
            </form>");

            if(isset($_POST["note"])){
                $note=$_POST["note"];
            }
            if(isset($_POST["texte"])){
                $texte=$_POST["texte"];
                $texte=mysqli_real_escape_string($connexion, $texte); //nettoyage des guillemets au cas où
            }

            if (isset($note)&&isset($texte)){
                $restmp = mysqli_query($connexion,"SELECT * FROM users WHERE mail= '{$_SESSION['mail']}';");
                $tmp = $restmp->fetch_assoc();
                $userid = $tmp['id'];
                $modif = mysqli_query($connexion,"UPDATE posts SET etoiles='$note', texte='$texte' WHERE userid='$userid' AND titre='$morceau' AND artiste='$artiste';");
                if($modif){
                echo ("<script>alert('Votre modification à été enregistrée, merci !');</script>");
                echo ("<script>window.location.replace('index.php');</script>"); 
                }else{
                    echo ("<script>alert('Oops, une erreur est survenue...');</script>");
                    echo ("<script>window.location.replace('index.php');</script>");
                }
            }   
        }else{
            header("location:Profil/LogIn.php");
        }

    }

?></div>