from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.chrome.options import Options
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from webdriver_manager.chrome import ChromeDriverManager
import time

start_time = time.time()

def default_driver_setup():
    chrome_options = Options()
    chrome_options.add_argument("--headless")
    chrome_options.add_argument("--no-sandbox")
    chrome_options.add_argument("--disable-dev-shm-usage")

    return webdriver.Chrome(service=Service(ChromeDriverManager().install()), options=chrome_options)


def get_films_url(page_url,nb_pages,type_item,driver):
    
    films_urls= []
    notes_films=[]

    for i in range(1, nb_pages+1):  #pages 1 to n
        if i == 1:
            url = page_url
        else:
            url = page_url+f"/page/{i}/"

        driver.get(url)

        #wait until the elements are present (max 10 seconds)
        try:
            WebDriverWait(driver, 10).until(
                EC.presence_of_all_elements_located((By.CLASS_NAME, type_item))
            )
        except Exception as e:
            print(f"Page {i}: aucun élément trouvé ({e})")
            continue

        films = driver.find_elements(By.CSS_SELECTOR, "." +type_item+ " a.frame")
        flag=False
        if type_item=="griditem": #we only take note when we look at the profile
            notes = driver.find_elements(By.CSS_SELECTOR, "." +type_item+ " p.poster-viewingdata")
            flag=True
        
        print(f"Page {i}: {len(films)} films trouvés")
        for j,film in enumerate(films):
            link = film.get_attribute("href")
            if link:
                films_urls.append(link[28:-1]) #remove the beggining of the link
                print(link[28:-1])
            if flag:
                    try:
                        span = notes[j].find_element(By.TAG_NAME, "span")
                        class_attr = span.get_attribute("class")
                        grade=int(class_attr.strip()[-1])
                        if grade==0: #if the last character is 0, then the score is 10...
                            grade=10
                        notes_films.append(grade) #rating given to the film (1 to 10)
                    except:
                        #no span found (film viewed but not rated)
                        notes_films.append(5) #average rating if the film is not rated

            


        #pause between pages to avoid blocking
        if i!=nb_pages:
            time.sleep(2)
        print(i)

    end_time=time.time()
    exec_duration=end_time-start_time

    print(f"Total de liens collectés : {len(films_urls)}")
    print(f"Durée d'execution : {exec_duration}")
    return films_urls,notes_films
