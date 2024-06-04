# lyrics-fetcher
Python script that will search for japanese song's lyrics.

## Environment
Python 3.9.12

## How to run
1. Clone this repository
```
git clone https://github.com/TeamJ-MUSt/lyrics-fetcher
cd lyrics-fetcher
```
2. Install `requirements.txt`
```
pip install -r requirements.txt
```
3. Run `search.py` or `lyrics.py` with arguments

### search.py
Running this will get search results of the given query from bugs. It will give a list of dictionaries, where each dictionary has music title, music id, artist name, and thumbnail url. Music's id is used to fetch lyrics.


usage: `search.py [-h] [--out OUT] [--verbose] query`

positional arguments:  
- `query`: Search query string, or Input file path of search queries. Whether it is a file or not is determined by the dot(.).  

optional arguments:  
- `-h`, `--help`: Show help message  
- `--out OUT`: Output file path. Outputs to standard output if not specified.  
- `--verbose`: Prints current queries and progress. Defaults to `False`
```
// Single query, output to file, log process
python search.py BETELGEUSE --out result.txt --verbose

// Multiple queries, output to standard output
python search.py queries.txt
```

### lyrics.py
Running this will get the lyrics of a given music id from bugs.


usage: `lyrics.py [-h] [--out OUT] [--verbose] music_id`

positional arguments:  
- `music_id`: Music's id, or Input file path of music ids. Whether it is a file or not is determined by the dot(.).

optional arguments:  
- `-h`, `--help`: Show help message  
- `--out OUT`: Output file path. Outputs to standard output if not specified.  
- `--verbose`: Prints current queries and progress. Defaults to `False`
```
// Single id, output to file, log process
python lyrics.py 83275198 --out result.txt --verbose

// Multiple ids, output to standard output
python lyrics.py music_ids.txt
```