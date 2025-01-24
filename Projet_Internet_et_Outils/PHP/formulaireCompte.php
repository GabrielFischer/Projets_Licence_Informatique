<?php include('header.php');
    if(!isset($_SESSION['mail'])){
        header("location: ../HTML/Profil/logIn.hmtl");
        exit;
    }
    $connexion = mysqli_connect("localhost", "root", "", "moosik");
    $mail =  $_SESSION["mail"];//Session mail stocké dans variable main pr simplifier utilisation
    $result = mysqli_query($connexion,"SELECT * FROM users WHERE mail= '$mail';");
    if ($row = $result->fetch_assoc()){
        $username = $row['username'];
        $nom = $row['nom'];
        $bio = $row['bio'];
    }
?>
    <section class ="login-box">
        <h1>Modifiez votre profil</h1>
        <form action="envoieForm/PostModification.php" method="POST" enctype="multipart/form-data">
            <div class = "user-box">
                <input type="file" name="nvpdp" id=""> <br>
                <label for="nvpdp">Photo de Profil (jpg, jpeg ou png)</label> <br>
            </div>
            <div class = "user-box">
                <input type="text" name="nom" <?php if(!is_null($nom)){echo "value=\"$nom\"";}?>><br>
                <label for="nom">Nom</label>
            </div>
            <div class = "user-box">
                <input type="text" name="username"<?php echo "value =\"$username\""?>><br>
                <label for="username">Nom d'Utilisateur</label>
            </div>
            <div class = "user-box">
                <input type="text" name="bio" <?php if(!is_null($bio)){echo "value=\"$bio\"";}?> ><br>
                <label for="bio">Biographie</label>
            </div>


            <button class ="sendbtn" type="submit" name = "submitMod">Enregistrer</button>

        </form>
    </section>

