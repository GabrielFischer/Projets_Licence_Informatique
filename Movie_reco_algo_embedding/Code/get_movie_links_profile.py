from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager
import time
import bs4
from urllib import request
import get_movie_links


global debut_url
debut_url="https://letterboxd.com/"
start_time = time.time()

def default_driver_setup():
    chrome_options=Options()
    chrome_options.add_argument("--headless")
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-dev-shm-usage")

    return webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)

#function to find how many movies the user watched
def get_movies_watched_number(user):
    #trouver le nombre de films vu par l'utilisateur, nécéssaire pour savoir le nombre de page a scrapper
    account=user
    profil_link=debut_url+account+"/"

    req=request.Request(profil_link, headers={"User-Agent": "Mozilla/5.0"})
    request_text= request.urlopen(req).read()
    page=bs4.BeautifulSoup(request_text, "lxml")
    stats=page.find("h4",{"class":"profile-statistic statistic"})
    nb_films=int(stats.find('span', class_='value').text.replace(",",""))
    print(nb_films)
    if nb_films%72==0:
        nb_pages=nb_films//72
    else:
        nb_pages=(nb_films//72)+1
    return nb_pages

#return the list of movies watched by the user
def get_movies_watched(user):
    profil_link = debut_url + user + "/"
    driver = default_driver_setup()  #driver for one user

    try:
        movies, grades = get_movie_links.get_films_url(
            page_url=profil_link + "films/",
            nb_pages=get_movies_watched_number(user),
            type_item="griditem",
            driver=driver
        )
    finally:
        driver.quit()  #we close the driver after scraping this user
        
    return movies,grades




