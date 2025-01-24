<?php 
if(isset($_POST['submitMusique'])){
    $connexion = mysqli_connect("localhost", "root", "", "moosik");
    session_start();
    $mail =  $_SESSION["mail"];//Session mail stocké dans variable main pr simplifier utilisation
    $titre =htmlspecialchars($_POST['titre']);
    $res = mysqli_query($connexion,"SELECT * FROM users WHERE mail= '$mail';");
    $row = $res->fetch_assoc();
    $userid = $row['id'];
    $username = $row['username'];
    $musiqueUpload = $_FILES['musique']; 
    

    $name = $_FILES['musique']['name'];
    $TmpName = $_FILES['musique']['tmp_name'];
    $Error = $_FILES['musique']['error'];
    $Type = $_FILES['musique']['type'];

    $musExt = explode('.',$name);
    $musActualExt = strtolower(end($musExt));

    $autorise = array('mp3','m4a');
    $erreur = 0;
    in_array($musActualExt,$autorise);
    if(in_array($musActualExt,$autorise) ){
        if($Error ===0){
            
            $musiqueNameNv = uniqid('',true).".".$musActualExt;
            $destination = '../../Uploads/Musique/'.$musiqueNameNv;
            move_uploaded_file($TmpName, $destination);
            $titre;
            mysqli_query($connexion,"INSERT INTO posts (type,titre,artiste,userid,morceau) VALUES('morceau','$titre','$username ','$userid','$musiqueNameNv')");
            $tmp = mysqli_query($connexion,"SELECT * FROM artiste WHERE nom='$username';");
            if(mysqli_num_rows($tmp)===0){
                mysqli_query($connexion,"INSERT INTO artiste (nom,nb_albums,nb_morceaux) VALUES('$username',0,1);");
            }
            mysqli_query($connexion,"INSERT INTO morceau (nom,artiste) VALUES('$titre','$username');");
            mysqli_query($connexion,"UPDATE artiste SET nb_morceaux= (SELECT COUNT(*) FROM morceau WHERE artiste = '$username');");
            header("Location: ../index.php");
            exit;
            
            
            
        }
        else{
            $erreur = 2;
        
        }
    }
    else{
        $erreur = 3;
    
    }
    if($erreur===2){
        header("Location: ../../Html/postMusique.html?erreur");
        exit;
    }
    if($erreur===3){
        header("Location: ../../Html/postMusique.html?formatNonAutorise");
        exit;
    }
}