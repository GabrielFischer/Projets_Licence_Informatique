<?php 
if(isset($_POST['submitMod'])){
    $connexion = mysqli_connect("localhost", "root", "", "moosik");
    session_start();
    $mail =  $_SESSION["mail"];//Session mail stocké dans variable main pr simplifier utilisation
    
    $username =$_POST['username'];
    $bio =$_POST['bio'];
    $nom =$_POST['nom'];
    $erreur = 0;
    $res = mysqli_query($connexion,"SELECT * FROM users WHERE mail= '$mail';");
    $row = $res->fetch_assoc();
    $oldUser = $row['username'];
    $pdpNameAncien = $row['pdp'];
    
    $nouvellePDP = $_FILES['nvpdp']; 
    print_r($_FILES['nvpdp']);

    $pdpName = $_FILES['nvpdp']['name'];
    $pdpTmpName = $_FILES['nvpdp']['tmp_name'];
    $pdpSize = $_FILES['nvpdp']['size'];
    $pdpError = $_FILES['nvpdp']['error'];
    $pdpType = $_FILES['nvpdp']['type'];
    
        $pdpExt = explode('.',$pdpName);
        $pdpActualExt = strtolower(end($pdpExt));

        $autorise = array('jpg','jpeg','png');
        
        if(in_array($pdpActualExt,$autorise) ){
            if($pdpError ===0){
                if($pdpSize < 1000000){
                    if(!is_null($pdpNameAncien) ){
                        unlink("../../Uploads/PhotoDeProfil/$pdpNameAncien");
                    }
                    $pdpNameNv = uniqid('',true).".".$pdpActualExt;
                    $destination = '../../Uploads/PhotoDeProfil/'.$pdpNameNv;
                    move_uploaded_file($pdpTmpName, $destination);
                    mysqli_query($connexion,"UPDATE users SET pdp = '$pdpNameNv' WHERE mail = '$mail'");
                    
                    
                }
                $erreur = 1;
                
            }
            else{
                $erreur = 2;
            
            }
        }
        else{
            $erreur = 3;
        
        }
    
    if(!is_null($nom)){
        $nom=mysqli_real_escape_string($connexion, $nom); 
        mysqli_query($connexion,"UPDATE users SET nom = '$nom' WHERE mail = '$mail';");
    }
    if(!is_null($bio)){
        $bio=mysqli_real_escape_string($connexion, $bio); 
        mysqli_query($connexion,"UPDATE users SET bio = '$bio' WHERE mail = '$mail';");
    }
    if(!is_null($username)){
        $username=mysqli_real_escape_string($connexion, $username); 
        mysqli_query($connexion,"UPDATE users SET username = '$username' WHERE mail = '$mail';");
        mysqli_query($connexion,"UPDATE posts SET artiste = '$username'  WHERE artiste = '$oldUser';");
        
    }
    if($erreur === 0){
        header("Location: ../pageProfil.php");
        exit;
    }
    if($erreur===1){
        header("Location: ../formulaireCompte.php?tropGrand");
        exit;
    }
    if($erreur===2){
        header("Location: ../formulaireCompte.php?erreur");
        exit;
    }
    if($erreur===3){
        header("Location: ../formulaireCompte.php?formatNonAutorise");
        exit;
    }
}