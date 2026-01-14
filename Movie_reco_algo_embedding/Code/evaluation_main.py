import numpy as np
import pandas as pd
import embedding
import scrap_profile
import scrap_list_users
import sys
import os
from gensim.models import Word2Vec
import statsmodels.api as sm


#A separate file from main.py is used to perform the various tests to validate the results

if __name__=="__main__":
    eval_mode=True
    test_ratio=0.3

    if len(sys.argv) == 2:
        filename=sys.argv[1]
    else:
        raise ValueError("usage : evaluation_main.py data_filename")
    
    film_titles,tokens=embedding.open_data(filename)

    file_path=f'./data/models/{filename}.model'
    if os.path.exists(file_path):
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
    film_embeddings = film_embeddings / np.linalg.norm(film_embeddings, axis=1, keepdims=True)


    #get 30 random users
    list_users=scrap_list_users.get_usernames_from_member_table()


    nb_films_vus=[]
    spearman_corr_list=[]
    spearman_p_list=[]

    for profile_name in list_users:

        user_vector, test_movies = scrap_profile.create_user_vec(
            profile_name,
            filename,
            model,
            film_embeddings,
            eval_mode=eval_mode,
            test_ratio=test_ratio
        )

        csv_name=scrap_profile.similarity_with_user(user_vector, profile_name, film_embeddings, film_titles)

        if eval_mode:
            spearman_corr,spearman_p = scrap_profile.spearman_test(
                csv_name,
                test_movies,
                profile_name
            )
        
        #since we multiplied the total number of films by the test_ratio
        nb_films_vus.append(len(test_movies)/test_ratio)
        spearman_corr_list.append(spearman_corr)
        spearman_p_list.append(spearman_p)


    import matplotlib
    matplotlib.use('Agg')
    import matplotlib.pyplot as plt

    plt.scatter(nb_films_vus, spearman_corr_list, color="blue")
    plt.xlabel("Nombre de films vus")
    plt.ylabel("Coefficient Spearman")
    plt.title("Coefficient Spearman vs nombre de films vus pour chaque utilisateur")
    plt.savefig("correlation_coef_spearman_plot.png")
    plt.close()

    plt.scatter(nb_films_vus, spearman_p_list, color="blue")
    plt.xlabel("Nombre de films vus")
    plt.ylabel("P-value")
    plt.title("P-value vs nombre de films vus pour chaque utilisateur")
    plt.savefig("correlation_p-value_spearman_plot.png")
    plt.close()
    
    #spearman coef
    X=sm.add_constant(nb_films_vus) #column of 1, biais
    y=spearman_corr_list

    model=sm.OLS(y, X) #OLS : Ordinary Least Squares
    results=model.fit()

    print(results.summary())


    #p-value
    X=sm.add_constant(nb_films_vus)
    y=spearman_p_list

    model=sm.OLS(y, X)
    results=model.fit()

    print(results.summary())

        