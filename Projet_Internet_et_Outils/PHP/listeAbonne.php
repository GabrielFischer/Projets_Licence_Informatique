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
<?php
session_start();
$connexion = mysqli_connect("localhost", "root", "", "moosik");
if (isset($connexion)){
    if (isset($_SESSION["id"])){
        if(isset($_GET["userid"])){
            if($_SESSION["id"]==$_GET["userid"]){
                echo("<h2>Vos abonnés: </h2>");
                $liste=mysqli_query($connexion,"SELECT * FROM abonnement WHERE id_abonnement='{$_GET['userid']}';");
                if (mysqli_num_rows($liste)>0){
                    while($row=mysqli_fetch_assoc($liste)){
                        $abonne=$row["id_abonne"];
                        $name=mysqli_query($connexion,"SELECT * FROM users WHERE id='$abonne';");
                        if (mysqli_num_rows($name)>0){
                            $row2=mysqli_fetch_assoc($name);
                            $username=$row2['username'];
                            $nom = $row2['nom'];
                            $pdp = $row2['pdp'];
                            $usermail = $row2['mail'];
                            echo "<div class = \"article-box\">";
                            echo "<div class=\" cont\"><div class=\"mini-pdp\">";
                            if(is_null($row2['pdp'])){
                                echo"<img src=\"../Images/compte-noir.png\" height=50px>";
                            }
                            else{
                                echo"<img src=\"../Uploads/PhotoDeProfil/".$pdp."\" height=50px>";
                            }
                            echo "</div><div class =\"name-container\">";
                            echo"<h1>".$nom."</h1><a href=\"PageUserAutre.php?user=$username\"><h3>@".$username."</h3></a>";
                            
                            echo "</div></div></div>";
                        }
                    }
                }
            }else{
                echo("Vous n'êtes pas autorisé à accéder à cette page");
            }
        }else{
            echo("bizarre");
        }
    }else{
        echo("test");
    }
}