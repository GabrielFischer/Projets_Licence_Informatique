import time
import json
import numpy as np
import pandas as pd
import embedding
import scrap_profile
import readcsv
import sys
import os
from gensim.models import Word2Vec

if __name__=="__main__":
    if len(sys.argv)==4:
        filename=sys.argv[1]
        profile_name= sys.argv[2]
        eval_mode=sys.argv[3] == "--eval" #true if --eval
    elif len(sys.argv)== 3:
        filename=sys.argv[1]
        profile_name=sys.argv[2]
        eval_mode=False
    else:
        raise ValueError("usage : main.py data_filename profile_name [--eval]")
    
    #split all film titles and their associated tokens
    film_titles, tokens=embedding.open_data(filename)

    file_path=f'./data/models/{filename}.model'
    if os.path.exists(file_path): #this avoids us having to recalculate the model
        print("model already learned")
        model=Word2Vec.load(f"./data/models/{filename}.model")
    else:
        print("model learning")
        model=embedding.trainW2V(tokens)
        model.save(f"./data/models/{filename}.model")

    film_embeddings = np.array([
    embedding.film_vector(token, model) for token in tokens
    ])

    #normalize each vector so that ||v|| = 1
    film_embeddings=film_embeddings / np.linalg.norm(film_embeddings, axis=1, keepdims=True)

    #creation of the user vector and possibly the test set if --eval==True
    user_vector, test_movies = scrap_profile.create_user_vec(
        profile_name,
        filename,
        model,
        film_embeddings,
        eval_mode=eval_mode,
        test_ratio=0.3
    )

    #csv file to stock results of recommendations
    csv_name=scrap_profile.similarity_with_user(user_vector, profile_name, film_embeddings, film_titles)

    #function to compute tests in order to evaluate predictions
    if eval_mode:
        scrap_profile.evaluate_predictions(
            csv_name,
            test_movies,
            profile_name
        )

    #print of results
    readcsv.recommend_by_genre(5,csv_name,"data.json", profile_name)