import get_movie_links_profile
import embedding
import time
import json
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
import pandas as pd
import random
import matplotlib.pyplot as plt
from scipy.stats import pearsonr, spearmanr
import seaborn as sns

#function that takes a profile and creates its average vector
def create_user_vec(profile_name, data_filename, model, film_embeddings, eval_mode=False, test_ratio=0.3):
    start_time=time.time()
    user=profile_name
    list_movie_link, list_movie_grade=get_movie_links_profile.get_movies_watched(user)
    with open(f"./data/list_movie_link/movie_link{user}.txt",'w', encoding="utf-8") as f:
        for element in list_movie_link:
            f.write(element + "\n")

    with open(f"./data/json/{data_filename}","r") as list_token:
        list_token=json.load(list_token)

    profile_data_dico={}
    movie_tokens_list= []
    filtered_links=[]
    filtered_grades= []

    for movie_link,grade in zip(list_movie_link, list_movie_grade):
        if list_movie_link.index(movie_link)%10==0:
            print(f"{movie_link} ({list_movie_link.index(movie_link)})")
        if movie_link in list_token: #if we've already scraped the tokens for the database, we'll just retrieve them
            movie_tokens = list_token[movie_link]
            profile_data_dico[movie_link] = movie_tokens
            movie_tokens_list.append(movie_tokens)
            filtered_links.append(movie_link)
            filtered_grades.append(grade)
        else: #otherwise, we ignore it, it almost never happens (except for films with almost 0 views)
            print(f"{movie_link} not in databse, ignored")

    end_time=time.time()
    print(f"Durée d'exécution:{end_time-start_time}s")

    with open(f"./data/json/data_profile_{user}.json",'w') as json_file:
        json.dump(profile_data_dico,json_file,indent=4)

    movies=list(zip(movie_tokens_list, filtered_grades, filtered_links))

    if eval_mode:
        random.shuffle(movies)
        split_idx = int(len(movies) * (1 - test_ratio))
        train_movies = movies[:split_idx]
        test_movies = movies[split_idx:]
    else:
        train_movies = movies
        test_movies = []

    train_tokens=[m[0] for m in train_movies]
    train_grades=[m[1] for m in train_movies]

    user_vector=embedding.user_weighted_vector(
        train_tokens,
        train_grades,
        model=model
    )

    #centering
    vec_user_centered=user_vector-film_embeddings.mean(axis=0)

    #normalization
    norm=np.linalg.norm(vec_user_centered)
    if norm>0:
        vec_user_centered/= norm

    return vec_user_centered,test_movies


def similarity_with_user(user_vector, profile_name, film_embeddings, film_titles):

    #scalar product = cosine similarity here
    scores=film_embeddings @ user_vector

    df_score_profile = pd.DataFrame(
        scores.reshape(1, -1),
        index=[profile_name],
        columns=film_titles
    )

    csv_name = f'./data/results/score_with_{profile_name}.csv'
    df_score_profile.to_csv(csv_name, index=True)

    return csv_name



def evaluate_predictions(csv_name, test_movies, profile_name):
    df_sim = pd.read_csv(csv_name, index_col=0)
    rows=[]

    for tokens,true_grade, movie_link in test_movies:
        if movie_link in df_sim.columns:
            predicted_score = df_sim.loc[profile_name, movie_link]
            rows.append({
                "movie": movie_link,
                "true_grade": true_grade,
                "predicted_similarity": predicted_score
            })

    df_eval = pd.DataFrame(rows)

    #sorted by actual rating (descending)
    df_eval = df_eval.sort_values("true_grade", ascending=False)

    df_eval.to_csv(
        f"./data/results/eval_{profile_name}.csv",
        index=False
    )

    print("\nFilms de validation (triés par note réelle) :\n")
    print(df_eval.to_string(index=False))

    #load the evaluation results
    df_eval = pd.read_csv(f"./data/results/eval_{profile_name}.csv")

    #retrieve the two columns
    true_grades = df_eval['true_grade']
    pred_scores = df_eval['predicted_similarity']

    #pearson correlation
    pearson_corr, pearson_p = pearsonr(true_grades, pred_scores)

    #spearman correlation
    spearman_corr, spearman_p = spearmanr(true_grades, pred_scores)

    print("Correlation analysis :")
    print(f"Pearson r = {pearson_corr:.3f}, p-value = {pearson_p:.4f}")
    print(f"Spearman rho = {spearman_corr:.3f}, p-value = {spearman_p:.4f}")

    # Boxplots
    sns.boxplot(
        x="true_grade",
        y="predicted_similarity",
        data=df_eval,
        showfliers=False
    )

    # Points avec jitter
    sns.stripplot(
        x="true_grade",
        y="predicted_similarity",
        data=df_eval,
        jitter=True,
        color="black",
        alpha=0.6
    )

    plt.xlabel("True grade")
    plt.ylabel("Pred grade")
    plt.title("Boxplots with jitter points")
    plt.show()



    return df_eval



def spearman_test(csv_name, test_movies, profile_name):
    df_sim = pd.read_csv(csv_name, index_col=0)

    rows = []

    for tokens, true_grade, movie_link in test_movies:
        if movie_link in df_sim.columns:
            predicted_score = df_sim.loc[profile_name, movie_link]
            rows.append({
                "movie": movie_link,
                "true_grade": true_grade,
                "predicted_similarity": predicted_score
            })

    df_eval = pd.DataFrame(rows)

    #sorted by actual rating (descending)
    df_eval = df_eval.sort_values("true_grade", ascending=False)

    df_eval.to_csv(
        f"./data/results/eval_{profile_name}.csv",
        index=False
    )

    #load the evaluation results
    df_eval = pd.read_csv(f"./data/results/eval_{profile_name}.csv")

    #retrieve the two columns
    true_grades = df_eval['true_grade']
    pred_scores = df_eval['predicted_similarity']

    #spearman correlation
    spearman_corr,spearman_p=spearmanr(true_grades, pred_scores)

    print(f"Spearman rho = {spearman_corr:.3f}, p-value = {spearman_p:.4f}")

    return spearman_corr,spearman_p


