from ytmusicapi import YTMusic

from ytmusicapi import YTMusic


def search_song(song_name: str, artist: str):
    ytmusic = YTMusic()
    search_results = ytmusic.search(f'{song_name} {artist}')
    
    for result in search_results:
        if result['resultType'] == 'video':
            return result.get('videoId')
    
    return None

def get_lyric_id(browse_id : str):
    ytmusic = YTMusic()
    playlist = ytmusic.get_watch_playlist(browse_id)
    if 'lyrics' in playlist.keys():
        return playlist['lyrics']
    return None

def get_lyrics(browse_id):
    ytmusic = YTMusic()
    lyrics_result = ytmusic.get_lyrics(browse_id)
    if 'lyrics' in lyrics_result.keys():
        return lyrics_result['lyrics']
    return None


# Example usage:
song_name = "Bohemian Rhapsody"
artist = "Queen"
browse_id = search_song(song_name, artist)
print("Browse ID:", browse_id)

lyric_id = get_lyric_id(browse_id)
print("Lyric ID:", lyric_id)

lyrics = get_lyrics(lyric_id)
print(lyrics)