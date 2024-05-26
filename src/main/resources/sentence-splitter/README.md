# nlp-module


## Environment
Python 3.9.12

## How to run
1. Clone this repository
```
git clone https://github.com/TeamJ-MUSt/sentence-splitter
cd nlp-module
```
2. Install `requirements.txt` and [UniDic](https://github.com/polm/unidic-py?tab=readme-ov-file). Installing UniDic will take a while.
```
pip install -r requirements.txt
python -m unidic download
```

### split.py
This will split a given sentence.

usage: `python context_definition.py [-h] query`

positional argument `query`: The sentence to split

```
python split.py 空にある何かを見つめてたらそれは星だって君がおしえてくれたまるでそれは僕らみたいに
```

Outputs the list of chuncks.
