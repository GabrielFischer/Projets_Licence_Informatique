import json
import embedding
import numpy as np
from sklearn.metrics.pairwise import cosine_similarity
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from gensim.models import Word2Vec
import umap
import plotly.graph_objects as go

def open_data(filename, limit=None):
    file_path = f'./data/json/{filename}'
    with open(file_path, 'r', encoding='utf-8') as f:
        films_tokens = json.load(f)

    items = list(films_tokens.items())

    if limit is not None:
        items = items[:limit]

    film_titles = [title for title, _ in items]
    tokens = [token_list for _, token_list in items]

    return film_titles, tokens

def load_model(filename):
    return Word2Vec.load(f'./data/models/{filename}')

def word_similarity_matrix(model, words, fill_missing=np.nan):
    """
    Creates a cosine-similarity matrix for a list of words
    using model.wv.similarity().
    """
    n = len(words)
    matrix = np.zeros((n, n))

    for i, w1 in enumerate(words):
        for j, w2 in enumerate(words):
            if w1 in model.wv and w2 in model.wv:
                matrix[i, j] = model.wv.similarity(w1, w2)
            else:
                matrix[i, j] = fill_missing

    return pd.DataFrame(matrix, index=words, columns=words)

def get_similarity_matrix_from_prefix(model,prefix,max=20):
    return word_similarity_matrix(model,[word for word in sorted(model.wv.key_to_index.keys(),
                                                             key=lambda w: model.wv.get_vecattr(w, "count"),
                                                             reverse=True)
                                                       if str(word).split('_')[0] in prefix][:max])

def get_similarity_matrix_from_closest_words(model,researched_word,max):
    res=[word[0] for word in model.wv.most_similar(researched_word, topn=max)]
    return word_similarity_matrix(model,res)

def get_umap_visualization(tokens,film_embeddings,attributes=None):
    attr_to_code = {a: i + 1 for i, a in enumerate(attributes)}
    c = [0] * len(tokens)
    for i, sent in enumerate(tokens):
        for token in sent:
            code = attr_to_code.get(token)
            if code:
                c[i] = code
                break


    colors=["blue","yellow","green","red","black","pink","purple","orange","cyan","brown"]

    reducer = umap.UMAP(n_neighbors=25, min_dist=0.2, n_components=2, metric="cosine", random_state=23)
    emb2d = reducer.fit_transform(film_embeddings)
    data=[]
    for i in range(-1,len(attributes)):
        idx_curr_attr=[j for j, v in enumerate(c) if v == i+1]
        data.append(
            go.Scatter(
                x=emb2d[idx_curr_attr, 0],
                y=emb2d[idx_curr_attr, 1],
                mode="markers",
                name="Other" if not i+1 else attributes[i],
                marker=dict(
                    color="grey" if not i+1 else colors[i],
                    size=20,
                    opacity=0.2 if not i+1 else 0.5
                ),
                text=[film_titles[j] for j in idx_curr_attr],
                hoverinfo="text"
            )
        )
    
    fig = go.Figure(
        data=data,
        layout=go.Layout(
            title=dict(
                text=f"UMAP Movie Embedding Map",
                font=(dict(size=16)),
                x=0.5,
                xanchor="center"
            ),
            xaxis_title="UMAP 1",
            yaxis_title="UMAP 2",
            legend_title_text="Legend:"
        )    
    )

    fig.update_layout(
        legend=dict(
            itemsizing="constant",
            font=(dict(size=16)),
            orientation="h",
            yanchor="top",
            y=-0.05,
            xanchor="center",
            x=0.5
        ),
        margin=dict(b=50)
    )

    fig.update_traces(marker=dict(size=6))

    fig.show()


if __name__=="__main__":
    # Chargement COMPLET des données
    all_titles, all_tokens = open_data("data.json")

    # Entraînement Word2Vec sur l'ensemble du corpus
    #model = embedding.trainW2V(all_tokens)

    #Chargement du modèle
    model=load_model("movieW2V.model")

    film_titles, tokens = open_data("data.json")

    film_embeddings = np.array([
        embedding.film_vector(token, model) for token in tokens
    ])

    """
    sim_matrix = cosine_similarity(film_embeddings)
    df_sim = pd.DataFrame(sim_matrix, index=film_titles, columns=film_titles)
    """
    """
    for a in ["DECADE","LANGUAGE","ACTOR","THEME","GENRE"]:

        df_sim=get_similarity_matrix_from_prefix(model,[a],20)
        plt.figure(figsize=(12, 10))
        
        sns.heatmap(
            df_sim,
            cmap="viridis",
            square=True,
            cbar=True,
            annot=True
        )

        plt.title(f"Cosine similarity matrix on {a.lower()}s")
        plt.xticks(rotation=45)
        plt.yticks(rotation=0)
        plt.tight_layout()
        plt.show()
    """
    """
    df_sim=get_similarity_matrix_from_closest_words(model,"COUNTRY_france",20)
    plt.figure(figsize=(12, 10))
        
    sns.heatmap(
        df_sim,
        cmap="viridis",
        square=True,
        cbar=True,
        annot=True
    )
    plt.title(f"Top 20 closest words to \"COUNTRY_france\"")
    plt.xticks(rotation=45)
    plt.yticks(rotation=0)
    plt.tight_layout()
    plt.show()
    """

    get_umap_visualization(tokens,film_embeddings,attributes=["COUNTRY_france","COUNTRY_usa","COUNTRY_uk","COUNTRY_japan","COUNTRY_germany"])


