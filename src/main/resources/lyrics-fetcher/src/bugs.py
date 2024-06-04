import time
import re
import random
import requests

from bs4 import BeautifulSoup as Soup

max_delay = 5
verbose = False


def set_max_delay(delay : float):
    if delay < 1:
        delay = 1
    max_delay = delay

def search_results(search_keyword : str):
    bugs_search_url = f"https://music.bugs.co.kr/search/integrated?q={search_keyword}"

    __random_delay()
    search_results = __find_music_info_from_search_result(bugs_search_url)
    return search_results


def find_lyrics(music_id : str):
    music_page_url = f"https://music.bugs.co.kr/track/{music_id}"
    __random_delay()
    lyrics = __get_lyrics_from_music_page(music_page_url)
    if lyrics is not None:
        return lyrics
    else:
        return ""

def __print_error(*args, **kwargs):
    if verbose:
        print(*args, **kwargs)

def __random_delay():
    time.sleep(random.random() * (max_delay - 1) + 1)

def __extract_bugsmusic_listen_parameter(input_string: str):
    # Define the regular expression pattern
    pattern = r"bugs\.music\.listen\('([^']+)',true\)"
    # Search for the pattern in the input string
    match = re.search(pattern, input_string)
    if match:
        # Extract the XXX part (group 1 in the match)
        parameter = match.group(1)
        return parameter
    else:
        return None
    
def __find_music_info_from_search_result(url: str):
    
    results = []
    try:
        response = requests.get(url)
        if response.status_code == 200:
            soup = Soup(response.content, 'html.parser')
            song_tr_elements = soup.find('div', id = 'DEFAULT0').find_all('tr', attrs={'trackid': True})
            for song_tr_element in song_tr_elements:
                result = {"music_id":"", "title":"", "artist":"", "thumbnailUrl":""}
                th_element = song_tr_element.find('th', scope='row')
                thumbnail_a_element = song_tr_element.find('a', class_='thumbnail')
                
                #thumbnail
                if thumbnail_a_element:
                    img_tag = thumbnail_a_element.find('img')
                    if img_tag:
                        result["thumbnailUrl"] = img_tag.get('src')
                        result["thumbnailUrl_large"] = img_tag.get('src').replace('/50/','/200/')
                if th_element:  
                    p_tag = th_element.find('p')
                    if p_tag:
                        a_tag = p_tag.find('a')
                        if a_tag:
                            # Song title
                            title = a_tag.get('title')
                            if title:
                                result["title"] = title
                            # Artist
                            td_tag = th_element.find_next_sibling('td')
                            p_artist = td_tag.find('p', class_='artist')
                            if p_artist:
                                a_tag2 = p_artist.find('a')
                                if a_tag2:
                                    result["artist"] = a_tag2.get_text()
                            # Lyrics
                            onclick_event = a_tag.get('onclick')
                            if onclick_event:
                                result['music_id'] = __extract_bugsmusic_listen_parameter(onclick_event)

                results.append(result)
        else:
            __print_error("Error: Unable to fetch the content from the URL")
    except Exception as e:
        __print_error(f"An error occurred: {e}")
    return results


def __get_lyrics_from_music_page(url: str):
    try:
        response = requests.get(url)
        if response.status_code == 200:
            soup = Soup(response.content, 'html.parser')
            lyrics_container_div = soup.find('div', class_='lyricsContainer')
            if lyrics_container_div:
                xmp_tag = lyrics_container_div.find('xmp')
                if xmp_tag:
                    return xmp_tag.get_text()
                else:
                    __print_error("No <xmp> tag found inside the <div> with class 'lyricsContainer'")
            else:
                __print_error("No <div> tag with class 'lyricsContainer' found")
        else:
            __print_error("Error: Unable to fetch the content from the URL")
    except Exception as e:
        __print_error(f"An error occurred: {e}")
    return None
