import bs4
from urllib import request


def get_page_html(link)->bs4.BeautifulSoup:
    req = request.Request(link, headers={"User-Agent": "Mozilla/5.0"})

    request_text = request.urlopen(req).read()
    return bs4.BeautifulSoup(request_text, "lxml")


def get_cast_data(page:bs4.BeautifulSoup):
    actor_dictionnary={}
    try:
        actors_links=page.find('div',id="tab-cast").find_all('a')
        
        for actor in actors_links:
            if 'tooltip' in actor.get("class"):
                actor_link=actor.get("href")[7:-1]
                if actor_link not in actor_dictionnary:
                    actor_dictionnary["ACTOR_"+actor_link]=actor.text
    except:
        print("No actor!!!!")
    return actor_dictionnary


def get_directors_data(page):
    director_dictionnary={}
    try:
        div_crew_links=page.find('div',id="tab-crew").find_all('a')

        for link in div_crew_links:
            if "/director/" in link.get("href"):
               director_dictionnary["DIRECTOR_"+link.get("href")[10:-1]]=link.text
            else:
                break #All the directors have been found so we skip any links that may be after
    except:
        print("No director!!!")
    return director_dictionnary


def get_release_decade(page):
    try:
        decade=(int(page.find("span", {"class": "releasedate"}).text)//10)*10
        return {"DECADE_"+str(decade):decade}
    except:
        print("No decade!!!")


def get_details_data(page):
    studio_dictionnary,country_dictionnary,language_dictionnary={},{},{}
    try:
        div_details_links=page.find('div',id="tab-details").find_all('a')
        for link in div_details_links:
            if "/studio/" in link.get("href"):
                studio_dictionnary["STUDIO_"+link.get("href")[8:-1]]=link.text
            elif "/films/country/" in link.get("href"):
                country_dictionnary["COUNTRY_"+link.get("href")[15:-1]]=link.text
            elif "/films/language/" in link.get("href"):
                language_dictionnary["LANGUAGE_"+link.get("href")[16:-1]]=link.text
    
    except:
        print("No details!!!")
            
    return country_dictionnary,studio_dictionnary,language_dictionnary
    

def get_genres_data(page):
    genres_dictionnary={}
    try:
        div_genres_links=page.find('div',id="tab-genres").find_all('a')
        
        for link in div_genres_links:
            if "/films/genre/" in link.get("href"):
                genres_dictionnary["GENRE_"+link.get("href")[13:-1]]=link.text
            else:
                break #All the genres have been found so we skip any links that may be after
    except:
        print("No genres!!!")
    return genres_dictionnary


def get_useful_data(page):
    actor_dict=get_cast_data(page)
    director_dict=get_directors_data(page)
    country_dict,studio_dict,language_dict=get_details_data(page)
    genres_dict=get_genres_data(page)
    release_decade=get_release_decade(page)
    useful_data={"Actors":actor_dict,
                 "Directors":director_dict,
                 "Countries":country_dict,
                 "Studio":studio_dict,
                 "Language":language_dict,
                 "Genres":genres_dict,
                 "Decade":release_decade
                 }
    
    return useful_data

def get_movie_tokens(link):
    page=get_page_html(f"https://letterboxd.com/film/{link}/")
    useful=get_useful_data(page)
    movie_tokens=[]
    print(useful)
    for attribute in useful:
        movie_tokens+=list(useful[attribute].keys())[:10]
    return movie_tokens