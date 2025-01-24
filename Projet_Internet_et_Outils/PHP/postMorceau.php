<? 

session_start();
if(!isset($_SESSION['mail'])){
    header("location: ../HTML/Profil/logIn.hmtl");
    exit;
}
?>
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

<section class="post-morceau">
<div class="login-box">
<h1>Nouveau post</h1>

<form action="" method="get" id="artiste">
    <label for="artiste">Qui est l'artiste de ce morceau ?</label>
    <select name="artiste" id="artiste">
        <option value="rien">--Veuillez choisir un artiste--</option>
        <?php
        $mysqli = mysqli_connect("localhost", "root", "", "moosik");
        if($mysqli->connect_error) {
        die("Erreur de connexion : " . $mysqli->connect_error);
        }
        $result = $mysqli->query("SELECT nom FROM artiste ORDER BY nom ASC");

        while($row = $result->fetch_assoc()) {
        echo "<option value='".$row['nom']."'".($_GET['artiste'] == $row['nom'] ?' selected' : '').">".$row['nom']."</option>"; //déroule les sons de l'artiste dans la base de donnée et le garde selected après sa validation
        }

        $mysqli->close();
        ?>
  </select>
  <br> <br><input class ="sendbtn" type="submit" value="Suivant"><br><br>
</form>

<?php 
if(isset($_GET['artiste'])){
    $artiste = $_GET['artiste'];
}
if (isset($artiste)){
    if ($artiste != 'rien'){
        echo ('<form action ="finMorceau.php" method="get" onsubmit="return validerFormulaire()">
        <label for="morceau">Choisissez un morceau de cet artiste </label>
        <select name="morceau" id="morceau">
        <option value="rien">--Veuillez choisir un morceau--</option>');

        $mysqli = mysqli_connect("localhost", "root", "", "moosik");

        if($mysqli->connect_error) {
        die("Erreur de connexion : " . $mysqli->connect_error);
        }

        $result = $mysqli->query("SELECT nom FROM morceau WHERE artiste='$artiste' ORDER BY nom ASC");

        while($row = $result->fetch_assoc()) {
        echo "<option value=\"" . $row['nom'] . "\">" . $row['nom'] . "</option>";
        }

        $mysqli->close();

        echo '<script>';
        echo 'function validerFormulaire() {';
        echo '  var morceauSelect = document.getElementById(\'morceau\').value;';
        echo '  if (morceauSelect === \'rien\') {';
        echo '    alert(\'Veuillez sélectionner un morceau.\');';
        echo '    return false;';
        echo '  }';
        echo '  return true;';
        echo '}';
        echo '</script>';

        echo('</select>
        <input type="hidden" name="artiste" value="'.$artiste.'">
        <br> <br><input class ="sendbtn" type="submit" value="Suivant"><br><br>
        </form>');
    }
}
?>
</div>


