import csv
import pandas as pd
import json

#function to print the first/last X recommendations
def print_extremity(TopX, head_or_tail_boolean, csvName):
    #load CSV
    df = pd.read_csv(f"./data/results/cosinus_similarity_with_{csvName}", index_col=0)
    
    #one line (one user)
    s=df.iloc[0]
    
    if head_or_tail_boolean:
        top= s.sort_values(ascending=False).head(TopX)
    else:
        top= s.sort_values(ascending=False).tail(TopX)
    print(top)



def recommend_by_genre(TopX, csvName, tokens_filename, user):
    #upload the CSV (similarity scores)
    df = pd.read_csv(csvName, index_col=0)
    s = df.iloc[0]

    #load the token dictionary
    with open(f"./data/json/{tokens_filename}", "r", encoding="utf-8") as f:
        token_dict= json.load(f)

    #file of movies watched by the user
    watched_file=f"./data/list_movie_link/movie_link{user}.txt"
    try:
        with open(watched_file, "r", encoding="utf-8") as f:
            watched_movies = set(line.strip() for line in f)
    except FileNotFoundError:
        watched_movies=set()#no film viewed if file does not exist (it shouldn't).

    listGenre=["action","adventure","animation","comedy","crime","documentary",
                 "drama","family","fantasy","history","horror","music","mystery","romance",
                 "science-fiction","thriller","tv-movie","war","western"]

    #dict to stock results
    results_by_genre = {}

    for genre in listGenre:
        #find all the films that have this genre
        films_in_genre = [movie for movie, tokens in token_dict.items() if f"GENRE_{genre}" in tokens]

        #keep only the films you haven't seen (do not recommend films you've already seen)
        films_in_genre = [movie for movie in films_in_genre if movie not in watched_movies]

        #sort by score and take TopX
        if films_in_genre:
            top_movies = s[films_in_genre].sort_values(ascending=False).head(TopX)
            results_by_genre[genre]=top_movies
        else:
            results_by_genre[genre]=pd.Series(dtype=float)  #empty if no movies find

    #results printing
    for genre,movies in results_by_genre.items():
        print(f"=== {genre.upper()} ===")
        print(movies.to_string())
        print()

    