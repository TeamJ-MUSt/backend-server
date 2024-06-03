import argparse
import fugashi
import sys
# This is our sample text.
# "Fugashi" is a Japanese snack primarily made of gluten.
#text = "空にある何かを見つめてたら12cars"
# The Tagger object holds state about the dictionary. 

#何十回 何百回 ぶつかりあって​\n何十年  何百年  昔の光が​
#空にある何かを見つめてたら それは星だって君がおしえてくれた まるでそれは僕らみたいに
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

    glue_to_front = ['助詞', '助動詞', '接尾辞'] #조사, 조동사, 접미사
    glue_back = ['数詞'] #수사, ?형용사
    chuncks = []
    back_glued = False
    for i, word in enumerate(words):
        #print(word.surface, word.feature.pos1, word.feature.pos2)
        if word.feature.pos1 in glue_to_front or back_glued:
            if len(chuncks) > 0:
                chuncks[-1] += word.surface
            else:
                chuncks.append(word.surface)
        else:
            chuncks.append(word.surface)

        back_glued = word.feature.pos1 in glue_back or word.feature.pos2 in glue_back
    print(chuncks)