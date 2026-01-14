from gensim.models import Word2Vec
import numpy as np
import pandas as pd
import json
import logging
import multiprocessing

#to get some logs about the model creation
logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)

#function to open the token json and return the list of those tokens.
def open_data(filename):
    file_path = f'./data/json/{filename}'
    with open(file_path, 'r') as f:
        films_tokens=json.load(f)
    film_titles=list(films_tokens.keys())
    tokens= list(films_tokens.values())
    return film_titles,tokens


#function to create and train the Word2Vec model

def trainW2V(tokens):
    model = Word2Vec(
        tokens,
        vector_size=128, #size of the generated vectors; 128 is a common choice for data of this scale
        window=100, #number of tokens that should be semantically related (100 to capture all tokens from the same movie)
        min_count=5, #minimum number of occurrences for a token to be considered; 5 avoids very rare tokens and speeds up training
        negative=5, #enables negative sampling by drawing false token pairs, helping to balance the model by learning what does not exist
        sample=1e-4,#reduces the weight of very frequent tokens such as LANGUAGE_English
        sg=1, #model architecture; 1 = Skip-Gram, generally better suited for movies
        workers=multiprocessing.cpu_count()-1, #number of threads used for training (affects only speed)
        epochs=100 #number of training iterations
    )
    return model



#function to create the vector of each film
def film_vector(tokens, model):
    vecs=[model.wv[t] for t in tokens if t in model.wv]
    if len(vecs)==0:
        return np.zeros(model.vector_size)
    return np.mean(vecs, axis=0)


#function to create the user vector
def user_weighted_vector(movie_tokens_list, movie_grades, model):
    user_vec= np.zeros(model.vector_size)

    for tokens,grade in zip(movie_tokens_list, movie_grades):
        vec=np.array([model.wv[t] for t in tokens if t in model.wv])
        if len(vec)== 0:
            continue

        film_vec=vec.mean(axis=0)

        #grade centered
        r=grade-5

        #squared weighting
        weight=np.sign(r) * (abs(r)**2)
        user_vec+=weight*film_vec

    return user_vec