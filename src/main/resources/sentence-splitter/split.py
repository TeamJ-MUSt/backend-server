import argparse
import fugashi
import sys
# This is our sample text.
# "Fugashi" is a Japanese snack primarily made of gluten.
#text = "空にある何かを見つめてたら12cars"
# The Tagger object holds state about the dictionary. 

#空にある何かを見つめてたらそれは星だって君がおしえてくれたまるでそれは僕らみたいに
sys.stdout.reconfigure(encoding='utf-8')
def parse_arguments():
    parser = argparse.ArgumentParser(description="Splits a sentence into multiple chunks")
    parser.add_argument('query', type=str, help='The sentence to split')

    args = parser.parse_args()

    return args.query.strip()

if __name__ == "__main__":
    query = parse_arguments()

    tagger = fugashi.Tagger()

    words = [word for word in tagger(query)]

    chuncks = ['']
    for word in words:
        chuncks[-1] += word.surface
        if '助' in word.feature.pos1 or '助' in word.feature.pos2:
            if chuncks[-1] != '':
                chuncks.append('')

    print(chuncks)