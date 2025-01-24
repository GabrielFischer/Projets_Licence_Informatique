open Geo
open Random
open List
open Graphics
open Arg
(* Code de la Section 4 du projet. *)

type instruction =
  Move of transformation
| Repeat of int * program
| Either of program * program 
and program = instruction list

let rec is_deterministic (prog : program) : bool = match prog with
  |[] -> true (*le programme est déterministe si il est vide*)
  |Move(_)::reste -> is_deterministic reste (*on appelle la fonction sur le reste du programme*)
  |Repeat(_, secondProgramme)::reste -> is_deterministic secondProgramme && is_deterministic reste
  (*On vérifie à la fois le programme qui doit être répété et le reste du programme*)
  |Either(_, _)::_ -> false
  (*Si on a un Either non vide, alors le programme est forcément non déterministe*)
;;

let rec unfold_repeat (prog : program) : program = match prog with
  |[]->[];
  |Move(instruc)::reste -> Move(instruc) :: unfold_repeat reste (*On continue si ce n'est pas repeat*)
  |Repeat(nb,secondProgramme)::reste -> 
    if nb>0 then
      secondProgramme @ unfold_repeat (Repeat((nb-1),secondProgramme) :: reste)
      (*si nb>0 alors on concatène n fois secondProgramme au reste*)
    else unfold_repeat reste
      (*sinon on n'a plus besoin de concatener secondProgramme on traite juste le reste*)
  |Either(prog1,prog2)::reste -> 
    Either (unfold_repeat prog1, unfold_repeat prog2) :: unfold_repeat reste
    (*Déplie les programmes du either*)
;;

let rec run_det (prog : program) (p : point) : point list = match prog with
  |[] -> [p] (*Si le programme est vide alors on retourne une liste contenant la position de départ du robot*)
  |_ -> let progSansRepeat = unfold_repeat prog in (*sinon, on déroule le programme afin de le traiter sans répétitions*)
  let rec parcoursProgSansRepeat acc prog = match prog with
    |[]-> acc (*retour de acc qui contient toutes les positions*)
    |Move(transformation)::reste -> let nouvellePosition = transform transformation (List.hd acc) in
      parcoursProgSansRepeat (nouvellePosition::acc) reste (*on ajoute la nouvelle position à la liste et on appelle le parcours sur le reste du programme*)
    |Repeat(_, _)::reste -> failwith "Pas de répétitions" (*cette ligne est là pour éviter les warnings de non traitance de tous les cas du type instruction*)
    |Either(_, _)::_-> failwith "Programme non déterministe" (*de même ici, cette ligne sert à supprimer les warnings*)
  in
  List.rev (parcoursProgSansRepeat [p] progSansRepeat)(*enfin, on retourne le rev de la liste car nous avons ajouté toutes les nouvelles positions en tête de liste à chaque fois*)

let target_reached_det (prog : program) (p : point) (target : rectangle) : bool =
  let pt_list = run_det prog p in (*liste des points par lesquels la tortue est passée*)
  let fct_point_final lst = 
    match List.rev lst with
    | [] -> failwith "La liste est vide"
    |x :: _ -> x (*retourne le dernier point visité*)
  in
  let point_final = fct_point_final pt_list in
  in_rectangle target point_final (*fonction de geo.ml qui teste si un point est dans un rectangle*)
;;

let run (prog : program) (p : point) : point list = match prog with
|[] -> [p] (*Si le programme est vide alors on retourne une liste contenant la position de départ du robot*)
|_ -> let progSansRepeat = unfold_repeat prog in (*sinon, on déroule le programme afin de le traiter sans répétitions*)
let rec parcoursProgSansRepeat acc prog = match prog with
  |[]-> acc (*retour de acc qui contient toutes les positions*)
  |Move(transformation)::reste -> let nouvellePosition = transform transformation (List.hd acc) in
    parcoursProgSansRepeat (nouvellePosition::acc) reste (*on ajoute la nouvelle position à la liste et on appelle le parcours sur le reste du programme*)
  |Repeat(_, _)::reste -> failwith "Pas de répétitions" (*cette ligne est là pour éviter les warnings de non traitance de tous les cas du type instruction*)
  |Either(prog1,prog2)::reste-> 
    if Random.bool() then (*Choisi au hasard si on prend le prog 1 ou 2 du either*)
      parcoursProgSansRepeat acc (prog1@reste)
    else  
      parcoursProgSansRepeat acc (prog2@reste)
in
List.rev (parcoursProgSansRepeat [p] progSansRepeat)(*enfin, on retourne le rev de la liste car nous avons ajouté toutes les nouvelles positions en tête de liste à chaque fois*)

let conversionRepeatEnListe (n : int) (sousprog : program) : program =
  (* on appelle unfold_repeat pour se débarrasser des Repeat imbriqués*)
  let progAplati = unfold_repeat sousprog in
    List.init n (fun _ -> progAplati) |> List.flatten

let rec all_choices (prog : program) : program list =match prog with
|[] -> [[]] (*si liste vide*)
|Move t :: reste ->
    List.map (fun possibility -> Move t::possibility) (all_choices reste)
|Repeat (n, subprog) :: reste ->
    let progDeplie = conversionRepeatEnListe n subprog in
    let possRestantes = all_choices reste in
    let possAdeplier = all_choices progDeplie in
    List.flatten (List.map(fun possDepliee ->
            List.map (fun possibilteeRestante -> possDepliee @ possibilteeRestante) possRestantes) possAdeplier
    )
    (*Le but ici est de rendre les Repeat déterministes, puis d'ajouter toutes les
    possibilités à partir du reste du programme*)
| Either (prog1, prog2) :: reste ->
    let possprog1 = all_choices prog1 in
    let possprog2 = all_choices prog2 in
    let possRestantes = all_choices reste in
    List.flatten (List.map(fun possibiliteeProg1 ->
            List.map (fun possibilteeRestante -> possibiliteeProg1 @ possibilteeRestante) possRestantes) possprog1)
    @
    List.flatten (
      List.map
        (fun possibiliteeProg2 ->
            List.map (fun possibiliteeRestante -> possibiliteeProg2 @ possibiliteeRestante) possRestantes) possprog2)
      (*combinaisons des possibilitées pour les 2 options de Either*)


let target_reached (prog : program) (p : point) (r : rectangle) : bool =
  let listeParcoursPossibles = all_choices prog in (*Crée une liste des parcours possibles*)
  List.for_all (fun programme -> target_reached_det programme p r) listeParcoursPossibles
  (*List.for_all retourne true si toutes les itérations donnent true*)


(*Fonction d'affichage du prompt ainsi que de la lecture de l'entrée*)
let rec lectureEntree prompt =
  print_string prompt;
  flush stdout;
  try
    float_of_string (read_line ()) (*Conversion de la chaïne en float*)
  with Failure _ ->
    print_endline "Erreur : Veuillez entrer un nombre valide.";
    lectureEntree prompt

(*Fonction qui détermine si -abs est activé ou non*)
let rec absOuPas() =
    print_string "Activer -abs (o/n) : ";
    flush stdout;
    match read_line() with
    |"o" | "O" | "Oui" | "oui" -> true
    |"n" | "N" |"Non" | "non" -> false
    |_ ->
      print_endline "Erreur, veuillez entrer une réponse valide (o/n)";
      absOuPas()

(*Fonction qui gère la taille de la fenêtre*)
let rec optionSize prompt =
  print_string prompt;
  flush stdout;
  let input = read_line () in
  if input = "" then
    (1200, 1080) (*valeur par défaut pour la taille de la fenêtre*)
  else
    match String.split_on_char ' ' input with
    |["-size"; w; h] ->
      (try
        let width = int_of_string w in
        let height = int_of_string h in
        if width > 0 && height>0 then(width, height)
        else (print_endline "Erreur : La largeur et la hauteur doivent être supérieures à 0"; optionSize prompt)
      with Failure _ ->
        print_endline "Erreur : Entrez deux entiers valides après -size";
        optionSize prompt)
    |_ ->
      print_endline "Erreur : Utiliser le format -size W H";
      optionSize prompt
(*Cette fonction gère toutes les options liées à la couleur des éléments*)
let rec optionBCouFCouRCouPC prompt =
  print_string prompt;
  flush stdout;
  let input = read_line () in
  if input = "" then
    ("none", 0, 0, 0)
  else
    match String.split_on_char ' ' input with
    |[option; r; v; b] when List.mem option ["-bc"; "-fc"; "-rc"; "-pc"] -> (*permet la généricité de cette fonction, 
    elle est donc utilisable pour chacune des options liées à la couleur*)
      (try
        let rouge= int_of_string r in
        let vert = int_of_string v in
        let bleu = int_of_string b in
        if rouge >= 0 && rouge <= 255 && vert >=0 && vert <= 255 && bleu >= 0 && bleu<=255 then
          (option, rouge, vert, bleu)
        else (print_endline "Erreur : les valeurs r v et b doivent être comprises entre 0 et 255";
          optionBCouFCouRCouPC prompt)
      with Failure _ ->
        print_endline "Erreur : entrez 3 entiers valides après -bc ou -fc ou -rc ou -pc";
        optionBCouFCouRCouPC prompt)
    |_ ->
      print_endline "Erreur : utiliser le format -bc r v b ou -fc r v b ou -rc r v b ou -pc r v b";
      optionBCouFCouRCouPC prompt


let set_backgroundCustom r v b =
  set_color (rgb r v b);
  fill_rect 0 0 (size_x()) (size_y())

let dessineRectangle rect couleur =
  set_color couleur;
  draw_rect (int_of_float rect.x_min) (int_of_float rect.y_min) (int_of_float (rect.x_max -. rect.x_min)) (int_of_float (rect.y_max -. rect.y_min))

(*Cette fonction permet de garder le point au centre du rectangle, notamment lorsque la position courante change*)
let centreRectangle rect centre =
  let largeur = rect.x_max -. rect.x_min in
  let hauteur = rect.y_max -. rect.y_min in
  { x_min = centre.x -. largeur /. 2.0;
    x_max = centre.x +. largeur /. 2.0;
    y_min = centre.y -. hauteur /. 2.0;
    y_max = centre.y +. hauteur /. 2.0 }

let main () =
  Random.self_init ();
  try (*Ici, on demande si l'utilisateur veut activer des options spécifiques ou non, s'il veut, alors il suit les instructions, sinon appuyer sur Enter le fait passer à la suite, et attribue les valeurs par défaut à l'élement qui a été ignoré*)
    let (width, height) = try optionSize "Entrez la taille de la fenêtre (-size W H) ou appuez sur Enter si vous voulez la taille par défaut (1200, 1080) : " with _ -> (1200, 1080) in
    let (bgColorOption, bgr, bgv, bgb) = try optionBCouFCouRCouPC "Entrez une couleur pour l'arrière plan (-bc r v b) ou appuyez sur Enter si vous n'en voulez pas : " with _ -> ("none", 0,0,0) in
    let (fgColorOption, fgr, fgv, fgb) = try optionBCouFCouRCouPC "Entrez une couleur pour l'avant-plan (-fc r v b) ou appuyez sur Enter si vous n'en voulez pas : " with _ -> ("none",0,0,0) in
    let (rectColorOption, rectr, rectv, rectb) = try optionBCouFCouRCouPC "Entrez une couleur pour le rectangle (-rc r v b) ou appuyez sur Enter si vous n'en voulez pas : " with _ -> ("none",0,0,0) in
    let (ptColorOption, ptr, ptv, ptb) = try optionBCouFCouRCouPC "Entrez une couleur pour le point (-pc r v b) ou appuyez sur Enter si vous n'en voulez pas : " with _ -> ("none", 0,0,0) in
    let showRectangle = absOuPas () in (*On demande si l'utilisateur veut que le rectangle soit également affiché*)
    let numProgramme =
      try
        print_string "Entrez le numéro du programme (1, 2 ou 3) : "; (*On demande quel programme appliquer au point*)
        flush stdout;
      int_of_string (read_line ())
      with Failure _ ->
        print_endline "Erreur : veuillez entrer un nombre entre 1 et 3";
        0
    in
  

    open_graph (" " ^ string_of_int width ^ "x" ^ string_of_int height);
    set_window_title "Affichage";
    (match bgColorOption with
    |"-bc" -> set_backgroundCustom bgr bgv bgb (*gestion de la couleur de l'arrière-plan*)
    | _ -> ());

    let ptInitial = {x=0.0; y=0.0} in
    let prog = match numProgramme with (*Déclaration des programmes que le robot devra suivre*)
      | 1 -> [Move(Translate {x = float_of_int (width/2); y = float_of_int(height/2)});
              Move(Translate{x = 0.0; y = 100.0});
              Repeat(5, [Move(Translate{x = -10.0; y= 5.0})]);
              Repeat(5, [Move(Translate{x = -10.0; y=0.0})]);
              Repeat(7, [Move(Translate{x = -10.0; y = -5.0})]);
              Repeat(5, [Move(Translate{x = 0.0; y = -10.0})]);
              Repeat(3, [Move(Translate{x = 5.0; y= -5.0})]);
              Repeat(25, [Move(Translate{x= 3.0; y = -5.0});
                          Move(Translate{x = 3.0; y = 0.0})]);
              Repeat(25, [Move(Translate{x=3.0; y=0.0});
                          Move(Translate{x=3.0; y = 5.0})]);
              Repeat(3, [Move(Translate{x = 5.0; y = 5.0})]);
              Repeat(5, [Move(Translate{x=0.0; y= 10.0})]);
              Repeat(7, [Move(Translate{x = -10.0; y = 5.0})]);
              Repeat(5, [Move(Translate{x = -10.0; y=0.0})]);
              Repeat(5, [Move(Translate{x = -10.0; y= -5.0})]);
        ]
      | 2 -> [
              Move(Translate {x = float_of_int (width/2); y = float_of_int(height/2)});
              Repeat(25, [Move(Translate{x = -5.0; y = -10.0})]);
              Repeat(17, [Move(Translate{x= 10.0; y = 7.0})]);
              Repeat(21, [Move(Translate{x = -10.0; y = 0.0})]);
              Repeat(17, [Move(Translate{x = 10.0; y = -7.0})]);
              Repeat(25, [Move(Translate{x = -5.0; y = 10.0})]);
              Repeat(12, [Move(Translate{x = 10.0; y = 0.0})])
        ]
      |3 -> [
          Move(Translate {x = float_of_int (width/2); y = float_of_int(height/2)});
          Repeat(20, [Move(Translate{x = 10.0; y = 0.0})]);
          Repeat(20, [Move(Translate{x = 0.0; y = 10.0})]);
          Repeat(20, [Move(Translate{x = -10.0; y = 0.0})]);
          Repeat(20, [Move(Translate{x = 0.0; y = -10.0})])
        ]
      |_ ->
        print_endline "Aucun programme sélectionné, affichage du point à l'origine";
        []
    in
    
    let ptColor =  (*gestion de la couleur du point*)
      match ptColorOption with
      |"-pc" -> rgb ptr ptv ptb
      |_ -> match fgColorOption with
            |"-fc" -> rgb fgr fgv fgb
            |_ -> red
    in
    set_color ptColor;
    fill_circle (int_of_float ptInitial.x) (int_of_float ptInitial.y) 5;
    Unix.sleep 1;

    let points = run prog ptInitial in (*appel de la fonction run pour construire les positions successives du robot*)
    if showRectangle then (
      let x_min = lectureEntree "Entrez la coordonnée x_min : " in (*on demande ici à l'utilisateur de rentrer les coordonnées du rectangle, 
    sachant que le rectangle doit contenir le point de coordonnée (0.0)*)
      let x_max = lectureEntree "Entrez la coordonnée x_max : " in
      let y_min = lectureEntree "Entrez la coordonnée y_min : " in
      let y_max = lectureEntree "Entrez la coordonnée y_max : " in
      let rectInitial = {x_min ; x_max; y_min; y_max} in

      if not (in_rectangle rectInitial {x=0.0; y=0.0}) then (
        failwith "Rectangle specifie ne contient pas l'origine (0.0)" (*on met fin au programme si le rectangle ne contient pas les oordonnées (0.0)*)
      )else (
        
        let rectColor = (*gestion de la couleur du rectangle*)
          match rectColorOption with
          |"-rc" -> rgb rectr rectv rectb
          |_ -> match fgColorOption with
            |"-fc" -> rgb fgr fgv fgb
            |_ -> blue
        in
        let rec affiche_etape point rect = (*permet l'affichage de chaque position du robot lors de l'execution du programme sélectionné*)
          dessineRectangle rect rectColor;
          set_color ptColor;
          fill_circle (int_of_float point.x) (int_of_float point.y) 5;
          Unix.sleep 1
        in
        affiche_etape ptInitial (centreRectangle rectInitial ptInitial);

        let rectangleInitCentre = centreRectangle rectInitial ptInitial in
        affiche_etape ptInitial rectangleInitCentre;

        let rec affiche_etapes points rectPrec = 
          match points with
          |[] -> ()
          |point::reste ->
            let rectCourant = centreRectangle rectInitial point in
            affiche_etape point rectCourant;
            affiche_etapes reste rectCourant
        in
        affiche_etapes points rectangleInitCentre;
      )
    ) else (
        List.iter (fun p ->
          set_color ptColor;
          fill_circle (int_of_float p.x) (int_of_float p.y) 5;
          Unix.sleep 1;
        ) points
    );
      
    ignore (read_key ()); (*appuyer sur une touche du clavier pour interrompre le processus lorsque le programme séectionné
    a termné son exécution*)
    close_graph ()
  with
 |exn ->
  print_endline (Printexc.to_string exn);
  close_graph()
let () = main()