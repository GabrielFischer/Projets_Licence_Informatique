<?php include('header.php');
//include('fonction.php')
if(!isset($_SESSION['mail'])){
    header("location: ../HTML/Profil/logIn.hmtl");
    exit;
}
?>


    <div class="profil">
    <section class = "compte">    
        <div class="profile-pic">
            <label class="-label" for="file">
                <span class="glyphicon glyphicon-camera"></span>
                
            </label>
            
            <img src=<?php 
                $mail =  $_SESSION["mail"];//Session mail stocké dans variable main pr simplifier utilisation
                $connexion = mysqli_connect("localhost", "root", "", "moosik"); // variable connection
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
                $_SESSION["id"]=$userid;

                if(!is_null($nom)){
                    echo "<h1>".$nom."</h1>";
                }  
                echo ("<h3>@".$username."</h3>"); //utilisation de variable dannsn echo
                if(!is_null($bio)){
                    echo "<h4 id=\"bio\">$bio</h4>";
                }
                
                echo ("<a class=\"buttonBis\" href=\"formulaireCompte.php\">Modifier mon compte</a>");
            
                echo ("<div id=\"abo\"> <h2><a href='listeAbonne.php?userid=$userid'>Abbonés </a> ".$row['abonnés']." </h2><h2> "."  " ."  -  "."<a href='listeAbonnement.php?userid=$userid'>Abonnements </a>".$row['abonnements']."</h2></div>");
            }
            ?>

        </div>

    </section>

    
    
    <section class="article-profil">
        <div class = "div-article">
            <?php 
                $result = mysqli_query($connexion,"SELECT * FROM posts WHERE userId='$userid' ORDER BY Date DESC;");
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
                    $id_publication=$row["id"];

                        


                                echo("<form action ='supprimer.php' method='post' id='supprimer'>
                                <input type='hidden' name='publication' id='publication' value='$id_publication'>
                                <input type='hidden' name='idScroll' id='idScroll' value='pageProfil.php'>
                                <button type ='submit' name='supprimer' id='supprimer' class='buttonBis'>Supprimer</button>
                                </form>
                                </div>"); 
                            
                        
                    
                }
            }?>

        </div>
    </section>
    </div>





<?php include('footer.php');?>