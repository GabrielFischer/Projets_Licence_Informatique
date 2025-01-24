<?php include('header.php');?>
<main>
     

        <section id="feed">
            <form class="searchfield" action="index.php" method="Post">
            <select name="filtre">
                <option value="Filtre">Filtrer par...</option>
                <option value="Compte" <?php if(isset($_POST['filtre']) && $_POST['filtre'] == "Compte") echo 'selected'; //Garder selected après la recherche ?> >Compte</option> 
                <option value="Artiste" <?php if(isset($_POST['filtre']) && $_POST['filtre'] == "Artiste") echo 'selected';?> >Artiste</option> 
                <option value="Album" <?php if(isset($_POST['filtre']) && $_POST['filtre'] == "Album") echo 'selected';?> >Album</option> 
                <option value="Morceau" <?php if(isset($_POST['filtre']) && $_POST['filtre'] == "Morceau") echo 'selected';?>>Morceau</option>
                <option value="Avis" <?php if(isset($_POST['filtre']) && $_POST['filtre'] == "Avis") echo 'selected';?>>Avis</option>
            </select>
            <input class="searchfield-input" type="text" name="search" placeholder="Rechercher" <?php if(isset($_POST['search'])){$search =$_POST['search']; echo "value=\"$search\"";} //Garder la recherched dans la barre?>>
            <button class="searchButton" type="submit" name="submitsearch">chercher</button><br>

            </form>
            <div class="div-article">
                <?php 
                    if (isset(($_POST['submitsearch'])) && isset($_POST['filtre']) && isset($_POST['search'])){
                        $filtre = $_POST['filtre'];
                        htmlspecialchars($_POST['submitsearch']);
                        htmlspecialchars($_POST['search']);
                        $search = mysqli_real_escape_string($connexion,$_POST['search']);
                        if($filtre==="Filtre"){
                            $compte="SELECT * FROM users WHERE nom LIKE '%$search%';";
                            $artiste="SELECT * FROM artiste WHERE nom LIKE '%$search%';";
                            $album="SELECT * FROM album WHERE nom LIKE '%$search%';";
                            $morceau="SELECT * FROM morceau WHERE nom LIKE '%$search%';";
                            $SQL = "SELECT DISTINCT * FROM posts CROSS JOIN users ON posts.userid = users.id  WHERE users.username LIKE '%$search%' OR users.nom LIKE '%$search%' OR posts.titre LIKE '%$search%' OR posts.texte LIKE'%$search%'ORDER BY Date DESC;";
                            
                        }
                        else if($filtre==="Compte"){
                            $compte="SELECT * FROM users WHERE nom LIKE '%$search%' OR username LIKE '%$search%';";
                            $result_compte = mysqli_query($connexion,$compte);
                            $queryCompte = mysqli_num_rows($result_compte);
                            $SQL = "SELECT DISTINCT * FROM posts CROSS JOIN users ON posts.userid = users.id  WHERE users.username LIKE '%$search%' OR users.nom LIKE '%$search%'ORDER BY Date DESC;";
                        }
                        else if($filtre==="Morceau"){
                            $morceau="SELECT * FROM morceau WHERE nom LIKE '%$search%';";
                            $result_morceau = mysqli_query($connexion,$morceau);
                            $queryMorceau = mysqli_num_rows($result_morceau);
                            $SQL = "SELECT * FROM posts WHERE type = 'morceau' AND titre LIKE '%$search%' OR type = 'morceau' AND texte LIKE'%$search%' OR type = 'avis_morceau' AND titre LIKE '%$search%' ORDER BY Date DESC;";
                        }
                        else if($filtre==="Artiste"){
                            $artiste="SELECT * FROM artiste WHERE nom LIKE '%$search%';";
                            $result_artiste = mysqli_query($connexion,$artiste);
                            $queryArtiste = mysqli_num_rows($result_artiste);
                            $SQL = "SELECT * FROM posts WHERE artiste LIKE '%$search%' ORDER BY Date DESC;";
                        }
                        else if($filtre==="Album"){
                            $album="SELECT * FROM album WHERE nom LIKE '%$search%';";
                            $result_album = mysqli_query($connexion,$album);
                            $queryAlbum = mysqli_num_rows($result_album);
                            $SQL = "SELECT * FROM posts WHERE type = 'album' AND titre LIKE '%$search%' OR type = 'album' AND texte LIKE'%$search%' OR type = 'avis_album' AND titre LIKE '%$search%' ORDER BY Date DESC;";
                        }
                        else if($filtre === "Avis"){
                            $SQL = "SELECT DISTINCT * FROM posts CROSS JOIN users ON posts.userId = users.id  WHERE (type='avis_morceau' OR type='avis_album') AND (users.username LIKE '%$search%' OR users.nom LIKE '%$search%' OR posts.titre LIKE '%$search%' OR posts.texte LIKE'%$search%')ORDER BY Date DESC;";
                        }
                    }
                    else if(isset($_GET['fil']) &&$_GET['fil']==="tendances"){
                        $SQL = "SELECT * FROM posts ORDER BY likes DESC;";
                    }
                    else if(isset($_SESSION['mail'])){
                        
                        $restmp = mysqli_query($connexion,"SELECT * FROM users WHERE mail= '{$_SESSION['mail']}';");
                        $tmp = $restmp->fetch_assoc();
                        $userid = $tmp['id'];
                        $nbabo = mysqli_query($connexion,"SELECT * from abonnement WHERE id_abonne = $userid");
                        if(mysqli_num_rows($nbabo)>0){
                            $SQL = "SELECT * FROM posts INNER JOIN abonnement ON posts.userId = abonnement.id_abonnement WHERE abonnement.id_abonne=$userid ORDER BY posts.Date DESC;";
                            }
                        else{
                            $SQL = "SELECT * FROM posts ORDER BY Date DESC;";
                        }
                        }
                        
                    else{
                        $SQL = "SELECT * FROM posts ORDER BY Date DESC;";
                    }        
                    $result = mysqli_query($connexion,$SQL);
                    $queryResults = mysqli_num_rows($result);
                    /* if(isset($queryCompte)){
                        if ($queryCompte>0){
                            echo("Compte(s): <br>");
                            while($row=mysqli_fetch_assoc($result_compte)){
                                echo($row["nom"]); 
                            }
                        }
                    }
                    if(isset($queryMorceau)){
                        if ($queryMorceau>0){
                            echo("Morceau(x): <br>");
                            while($row=mysqli_fetch_assoc($result_morceau)){
                                echo($row["nom"]); 
                            }
                        }
                    }
                    if(isset($queryArtiste)){
                        if ($queryArtiste>0){
                            echo("Artiste(s): <br>");
                            while($row=mysqli_fetch_assoc($result_artiste)){
                                echo($row["nom"]); // Comme morceau
                            }
                        }
                    }
                    if(isset($queryAlbum)){
                        if ($queryAlbum>0){
                            echo("Album(s): <br>");
                            while($row=mysqli_fetch_assoc($result_album)){
                                echo($row["nom"]); // Comme morceau
                            }
                        }
                    } */
                    if ($queryResults>0){
                        while($row = mysqli_fetch_assoc($result)){
                            $idScroll=$row["id"];
                            if (isset($_GET["fil"]) && $_GET["fil"]==="tendances"){
                                $idScrollBis="?fil=tendances#publication-$idScroll";
                                echo("<div id=publication-$idScroll>");
                            }else{
                                $idScrollBis="#publication-$idScroll";
                                echo("<div id=publication-$idScroll>");
                            }
                            echo "<div class = \"article-box\">";
                            $userid = $row['userId'];
                            
                            $res= mysqli_query($connexion,"SELECT * FROM users WHERE id= '$userid';");
                            
                            if ($row2 = $res->fetch_assoc()){
                                $username = $row2['username'];
                                $nom = $row2['nom'];
                                $pdp = $row2['pdp'];
                                $usermail = $row2['mail'];
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
                                if(isset($_SESSION['mail'])){
                                    if($usermail===$_SESSION['mail']){
                                    echo"<h1>".$nom."</h1><a href=\"PageProfil.php\"><h3>@".$username."</h3></a>";
                                    }
                                    else{
                                        echo"<h1>".$nom."</h1><a href=\"PageUserAutre.php?user=$username\"><h3>@".$username."</h3></a>";
                                    }
                                }
                                else{
                                    echo"<h1>".$nom."</h1><a href=\"PageUserAutre.php?user=$username\"><h3>@".$username."</h3></a>";
                                }
                                
                                
                            }
                            else{
                                echo "<a href=\"PageUserAutre.php?user=$username\"><h3>@".$username."</h3></a>";
                            }
                            echo "</div></div>";
                            if ($row['type']==='avis_album'||$row['type']==='avis_morceau'){
                                echo"<h4>".$row["artiste"]." - ".$row["titre"]." ".$row["etoiles"]."★</h4>
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

                            //Likes
                            echo "<div class = \"like\">";
                            $id_publication=$row["id"];
                            
                            if(isset($_SESSION['mail'])){
                                $likeur=mysqli_query($connexion,"SELECT * FROM users WHERE mail='{$_SESSION["mail"]}';");
                                if(mysqli_num_rows($likeur)>0){
                                    $row3=mysqli_fetch_assoc($likeur);
                                    $userid=$row3["id"];

                                    
                                    
                                    $estLike= mysqli_query($connexion,"SELECT * FROM likes WHERE publication='$id_publication' AND user='$userid'");
                                    if(mysqli_num_rows($estLike)>0){
                                        echo("<form action ='like.php' method='post' id='like'>
                                        <input type='hidden' name='publication' id='publication' value='$id_publication'>
                                        <input type='hidden' name='userid' id='userid' value='$userid'>
                                        <input type='hidden' name='idScroll' id='idScroll' value='$idScrollBis'>
                                        <button type ='submit' name='unliker' class='likes' > <img src='../Images/unlike.png' alt='Unliker' width = 30px/></button>
                                        </form>
                                        ");
                                    }else{
                                        echo("<form action ='like.php' method='post' id='like'>
                                        <input type='hidden' name='publication' id='publication' value='$id_publication'>
                                        <input type='hidden' name='userid' id='userid' value='$userid'>
                                        <input type='hidden' name='idScroll' id='idScroll' value='$idScrollBis'>
                                        <button type ='submit' name='liker' class='likes'  > <img src='../Images/like.png' alt='Liker'width = 30px/></button>
                                        </form>
                                        ");
                                    }
                                }
                                
                            }else{
                                echo("<button type ='submit' name='liker' class='likes'  > <a href='../Html/Profil/LogIn.html'><img src='../Images/like.png' alt='Liker'width = 30px/></a></button>");
                            }
                            $SQLnbLikes = mysqli_query($connexion,"SELECT COUNT(*) FROM likes WHERE publication = $id_publication;");
                            $rowNbLikes=mysqli_fetch_assoc($SQLnbLikes);
                            $nbLikes = $rowNbLikes['COUNT(*)'];
                            echo $nbLikes;
                            echo "</div>";

                            //Admin
                            if (isset($_SESSION["mail"])){
                                $admin=mysqli_query($connexion,"SELECT * FROM users WHERE mail='{$_SESSION['mail']}';");
                                if(mysqli_num_rows($admin)>0){
                                    $row4=mysqli_fetch_assoc($admin);
                                    $estadmin=$row4["admin"];
                                    if ($estadmin==1){
                                        echo("<form action ='supprimer.php' method='post' id='supprimer'>
                                        <input type='hidden' name='publication' id='publication' value='$id_publication'>
                                        <input type='hidden' name='idScroll' id='idScroll' value='$idScrollBis'>
                                        <button type ='submit' name='supprimer' id='supprimer' class='buttonBis'>Supprimer</button>
                                        </form>
                                        "); 
                                    }
                                }
                            }


                            echo("</div>");
                        }
                    }
                ?>
            </div>
        </section>

</main>
<?php include('footer.php');?>