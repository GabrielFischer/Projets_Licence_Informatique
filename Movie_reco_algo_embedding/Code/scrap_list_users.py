import bs4
from urllib import request

global debut_url
debut_url="https://letterboxd.com/"


#function that returns the list of popular members on page X
def get_usernames_from_member_table():
    url=debut_url+"members/popular/this/week/page/161/"
    req=request.Request(url, headers={"User-Agent": "Mozilla/5.0"})
    html=request.urlopen(req).read()

    soup=bs4.BeautifulSoup(html, "lxml")

    table=soup.find("table", {"class": "table-base member-table"})
    if table is None:
        return []

    usernames= []

    for a in table.find_all("a", class_="name"):
        href=a.get("href")
        if href:
            usernames.append(href.strip("/"))

    print(usernames)
    return usernames
