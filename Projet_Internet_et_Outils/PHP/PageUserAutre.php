<?php include("header.php");
    
    if(!isset($_GET["user"])){
        header("location;index.php");
        exit;
    }
    else{
        $user=$_GET["user"];
        $connexion = mysqli_connect("localhost", "root", "", "moosik"); // variable connection
        $result=mysqli_query($connexion,"SELECT * FROM users WHERE username='$user'");
        if(mysqli_num_rows($result)===0){
            header("location;index.php");
            exit;
        }
        else{
            $tmp= $result->fetch_assoc();
            $usermail = $tmp['mail'];
            $userid=$tmp['id'];
            
        }
        
    }

?>

<div class="profil">
<section class = "compte">    
        <div class="profile-pic">
            <label class="-label" for="file">
                <span class="glyphicon glyphicon-camera"></span>
                
            </label>
            
            <img src=<?php 
                $mail = $usermail ;//Session mail stocké dans variable main pr simplifier utilisation
                $connexion = mysqli_connect("localhost", "root", "", "moosik"); // variable connexion
                $res = mysqli_query($connexion,"SELECT pdp FROM users WHERE mail= '$mail';");
                $pdp = $res->fetch_assoc();
                if (is_null($pdp['pdp'])){ // si pdp est null -> affiche pdp de base
                    
                    echo "../Images/compte.png";
                }
                else{
                    echo "../Uploads/PhotoDeProfil/".$pdp['pdp'];
                }?>  id="output" width="200" />
        </div>
        <div id="infoCompte">
            <?php $result = mysqli_query($connexion, "SELECT * FROM users WHERE mail= '$mail';"); // resultat de requete sql dans result
                

            if ($row = $result->fetch_assoc()){ //commande pour utiliser resultat et stocker dans une variable row
                $nom = $row['nom'];
                $username = $row['username'];
                $bio = $row['bio'];
                $userid = $row['id'];
                if(!is_null($nom)){
                    echo "<h1>".$nom."</h1>";
                }  
                echo ("<h3>@".$username."</h3>"); //utilisation de variable dannsn echo
                if(!is_null($bio)){
                    echo "<h4 id=\"bio\">$bio</h4>";
                }
                if(isset($_SESSION['mail'])){
                    $id_abonnement=$userid;
                    $abonne=mysqli_query($connexion,"SELECT * FROM users WHERE mail='{$_SESSION["mail"]}';");
                    $row=mysqli_fetch_assoc($abonne);
                    
                    $id_abonne=$row["id"];
                    
                   
                    $estAbonne=mysqli_query($connexion, "SELECT * FROM abonnement WHERE id_abonne='$id_abonne' AND id_abonnement='$id_abonnement';");
                    if (mysqli_num_rows($estAbonne)>0){
                        echo ("<form id='abonner' action='abonnement.php' method='post'>
                        <input type='hidden' name='id_abonnement' id='id_abonnement' value='$id_abonnement'>
                        <input type='hidden' name ='user' id='user' value='$user'>
                        <input type='hidden' name ='abonnes' id='abonnes' value='{$tmp['abonnés']}'>
                        <button type ='submit' name='desabonnement' id='desabonnement' class='buttonBis'> Se désabonner </button>
                        </form>");
                        
                    }else{
                        echo ("<form id='abonner' action='abonnement.php' method='post'>
                        <input type='hidden' name='id_abonnement' id='id_abonnement' value='$id_abonnement'>
                        <input type='hidden' name ='user' id='user' value='$user'>
                        <input type='hidden' name ='abonnes' id='abonnes' value='{$tmp['abonnés']}'>
                        <button type ='submit' name='abonnement' id='abonnement' class='buttonBis'> S'abonner </button>
                        </form>");
                        
                    }
                }
                
            
                echo ("<div id=\"abo\"> <h2>Abbonés ".$tmp['abonnés']."  -  "."</h2><h2> Abonnements ".$tmp['abonnements']."</h2></div>");
            }
            ?>

            <?php
            if(isset($_SESSION["mail"])){
                
            }
            ?>
        

    </section>

    
    
    <div class="article-profil">
        <div class = "div-article">
            <?php 
                $result = mysqli_query($connexion,"SELECT * FROM posts WHERE userid='$userid' ORDER BY Date DESC;");
                $queryResults = mysqli_num_rows($result);
                if ($queryResults>0){
                while($row = mysqli_fetch_assoc($result)){
                    echo "<div class = \"article-box\">";
                    
                    $res= mysqli_query($connexion,"SELECT * FROM users WHERE id= '$userid';");
                    
                    if ($row2 = $res->fetch_assoc()){
                        $username = $row2['username'];
                        $nom = $row2['nom'];
                        $pdp = $row2['pdp'];
                    }
                    echo "<div class=\" cont\"><div class=\"mini-pdp\">";
                    if(is_null($row2['pdp'])){
                        echo"<img src=\"../Images/compte-noir.png\" height=50px>";
                    }
                    else{
                        echo"<img src=\"../Uploads/PhotoDeProfil/".$pdp."\" height=50px>";
                    }
                    echo "</div><div class =\"name-container\">";
                    if (!is_null($nom)){
                        echo"<h1>".$nom."</h1><a href=\"\"><h3>@".$username."</h3></a>";
                    }
                    else{
                        echo "<h1>".$username."</h1>";
                    }
                    echo "</div></div>";
                    if ($row['type']==='avis_album'||$row['type']==='avis_morceau'){
                        echo"<h4>".$row["titre"]." ".$row["etoiles"]."★</h4>
                        <p>".$row['texte']."</p>
                        ";
                    }
                    else if($row['type']==='article'){
                        echo"<h4>".$row["titre"]."</h4>
                        <p>".$row['texte']."</p>
                        ";
                    }
                    else if($row['type']==='morceau'){
                        $cheminMorceau=$row["morceau"];
                        echo"<h4>".$row["titre"]."</h4>
                        <audio controls>
                            <source src=\"../Uploads/Musique/$cheminMorceau\" type=\"audio/mpeg\">
                        Your browser does not support the audio element.
                        </audio>";
                    }
                    echo "</div>";
                }
            }?>

        </div>


    </div>
</div>


<?php include('footer.php');?>