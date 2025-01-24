<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MOOSIK</title>
    <link rel="stylesheet" href="../CSS/stylesheet.css">
</head>
<body>
    <header>
        <nav >
            <ul id="barreEnHaut">
                <li>
                    <a   href="index.php"><img id ="logo" src="../Images/logo_moosik.png" alt="logo_moosik"></a>
                </li>
                <li>
                    <a href="index.php?fil=tendances">Tendances</a>
                </li>
                <li>
                    <a href="preNew.php">nouveautés</a>
                </li>
            </ul>
        </nav>
        <nav>
            <ul id="icones">
                <?php 
                    $connexion = mysqli_connect("localhost", "root", "", "moosik");
                    session_start();
                    
                    
                    if (isset($_SESSION["mail"])){
                        echo "<li><a href=\"../html/prePost.html\"><img class = \"iconesHeader\" src=\"../Images/plus-symbole-blanc.png\" alt=\"icône de blanc plus pour publier nouveaux contenu\"></a></li>";
                        
                        $mail = $_SESSION['mail'];
                        $res = mysqli_query($connexion,"SELECT pdp FROM users WHERE mail= '$mail';");
                        $pdp = $res->fetch_assoc();
                        if (is_null($pdp['pdp'])){ // si pdp est null -> affiche pdp de base
                        
                            echo "<li><a href=\"pageProfil.php\"> <img class = \"iconesHeader\" src=\"../Images/compte.png\" alt=\"icône de compte\"></a> </li>";
                        }
                        else{
                            echo "<li><a href=\"pageProfil.php\"> <div class=\"pdp-header\"><img class = \"iconesHeader\" src=\"../Uploads/PhotoDeProfil/".$pdp['pdp']."\" alt=\"icône de compte\"> </div></a></li>";
                        }  
                           
                        
                        
                        echo "<li><a href=\"Profil/logout.php\"><img class = \"iconesHeader\" src=\"../Images/logout.png\" alt=\"symbole recherche\"></a></li>";
                    }
                    else{
                        echo "<li></li>";
                        echo "<li></li>";
                        echo "<li><a href=\"../Html/Profil/logIn.html\"> <img class = \"iconesHeader\" src=\"../Images/compte.png\" alt=\"icône de compte\"></a> </li>";
                    }
                    
                     ?>
                

            </ul>
        </nav>
    </header>

