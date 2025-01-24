open Geo
open Interp

(* Code de la Section 5 du projet. *)

let sample (rect : rectangle) : point =
  {x = rect.x_min +. Random.float (rect.x_max -. rect.x_min); y = rect.y_min +. Random.float (rect.y_max -. rect.y_min)}
  
let transform_rect (t : transformation) (r : rectangle) : rectangle = match t with
  |Translate vecteur ->(*Dans le cas d'une simple translation, on applique juste la fonction translate sur chacun des coins du rectangle*)
    let pointsMinduRectangle = {x = r.x_min ; y = r.y_min} in
    let pointsMaxDuRectangle = {x = r.x_max ; y = r.y_max} in
    {x_min = (translate vecteur pointsMinduRectangle).x; x_max = (translate vecteur pointsMaxDuRectangle).x ; y_min = (translate vecteur pointsMinduRectangle).y ; y_max = (translate vecteur pointsMaxDuRectangle).y}
    (*Dans le cas d'une rotation, on suit la même logique en appliquant cette fois la fonction rotate à chaque coin du rectangle mais...*)
    |Rotate(point, angle) -> 
      let coinsDuRectangle = corners r in
      let coinsTransformes = List.map (fun corner -> rotate point angle corner) coinsDuRectangle in
      (*Puisque 'lon veut une sur approximation, on n'oublie pas d'appeler rectangle_of_list qui renvoie le plus petit rectangle droit qui conteint chacun des points de la liste donnée en paramètres*)
      rectangle_of_list coinsTransformes

let rec run_rect (prog : program) (r : rectangle) : rectangle list =match prog with
      |[] -> [r]
      |Move(t) ::reste ->
        let nouv_rect = transform_rect t r in
        r::run_rect reste nouv_rect
      |Repeat(n, p)::reste ->
        let rec repeat_n nbFois acc =
          if nbFois = 0 then acc
          else
            repeat_n (nbFois-1) (run_rect p (List.hd acc))
          in
          List.rev (repeat_n n [r]) @ run_rect reste (List.hd (repeat_n n [r]))
      |Either(prog1, prog2)::reste-> if Random.bool() then run_rect prog1 r else run_rect prog2 r

let inclusion (r : rectangle) (t : rectangle) : bool =
  r.x_min>=t.x_min && r.x_max<=t.x_max && r.y_min>=t.y_min && r.y_max<=t.y_max

let target_reached_rect (prog : program) (r : rectangle) (target : rectangle) : bool =
  (* Fonction auxiliaire qui vérifie si un rectangle est inclus dans le rectangle cible *)
  let rec is_in_target rect_list target_rect =
    match rect_list with
    | [] -> true
    | rect::rest ->
      if inclusion rect target_rect then is_in_target rest target_rect
      else false
  in
  (* Fonction principale qui analyse toutes les exécutions possibles du programme *)
  let rec analyze_program prog rect =
    match prog with
    | [] -> [rect] (* Si le programme est vide, renvoie la position initiale *)
    | Move(t)::rest ->
      let new_rect = transform_rect t rect in
      new_rect :: analyze_program rest new_rect
    | Repeat(n, sub_prog)::rest ->
      let rec repeated_sub_prog n acc_rect =
        if n <= 0 then acc_rect
        else
          let sub_rects = analyze_program sub_prog acc_rect in
          repeated_sub_prog (n - 1) (List.hd (List.rev sub_rects))
      in
      let rect_after_repeats = repeated_sub_prog n rect in
      analyze_program rest rect_after_repeats
    | Either(prog1, prog2)::rest ->
      let rects1 = analyze_program (prog1 @ rest) rect in
      let rects2 = analyze_program (prog2 @ rest) rect in
      rects1 @ rects2
  in

  (* Exécute le programme et obtient tous les rectangles traversés *)
  let all_rects = analyze_program prog r in

  (* Vérifie si tous les rectangles sont inclus dans le rectangle cible *)
  is_in_target all_rects target
  

let rec run_polymorphe (apply_transform : transformation -> 'a -> 'a) (prog : program) (i : 'a) : 'a list =
  let rec process_instruction acc instr =
    let current_i = List.hd acc in
    match instr with
    | Move t ->
        let new_i = apply_transform t current_i in
        new_i :: acc
    | Repeat (n, subprog) ->
        let rec repeat n acc =
          if n <= 0 then acc
          else
            let new_is = run_polymorphe apply_transform subprog (List.hd acc) in
            repeat (n - 1) (List.rev_append new_is acc)
        in
        repeat n acc
    | Either (p1, p2) ->
        let chosen_prog = if Random.bool () then p1 else p2 in
        run_polymorphe apply_transform chosen_prog current_i @ acc
  in
  List.fold_left (fun acc instr -> process_instruction acc instr) [i] prog |> List.rev

(* Fonction d'union des rectangles *)
let union_rectangle (r1 : rectangle) (r2 : rectangle) : rectangle =
  {
    x_min = min r1.x_min r2.x_min;
    y_min = min r1.y_min r2.y_min;
    x_max = max r1.x_max r2.x_max;
    y_max = max r1.y_max r2.y_max;
  }


let rec over_approximate (prog : program) (r : rectangle) : rectangle =
  match prog with
  | [] -> r (* Aucun changement pour un programme vide *)
  | Move t :: rest ->
      let r' = transform_rect t r in
      over_approximate rest r'
  | Repeat (n, sub_prog) :: rest ->
      let rec repeat_approximation n acc =
        if n <= 0 then acc
        else
          let acc' = over_approximate sub_prog acc in
          repeat_approximation (n - 1) (union_rectangle acc acc')
      in
      let r' = repeat_approximation n r in
      over_approximate rest r'
  | Either (prog1, prog2) :: rest ->
      let r1 = over_approximate prog1 r in
      let r2 = over_approximate prog2 r in
      let r_union = union_rectangle r1 r2 in
      over_approximate rest r_union

let feasible_target_reached (prog : program) (r : rectangle) (target : rectangle) : bool =
  let approx = over_approximate prog r in
  inclusion approx target 
