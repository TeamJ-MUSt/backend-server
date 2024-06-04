import argparse
import json
import os
import sys
from src import bugs
from src import utils

sys.stdout.reconfigure(encoding='utf-8')
bugs.max_delay = 3

def is_file(string):
    _, file_extension = os.path.splitext(string)
    return bool(file_extension)

def get_lyrics(music_id, verbose = False):
    music_id = music_id.strip()
    if verbose:
        print("Getting lyrics:", music_id)
    try:
        result = bugs.find_lyrics(music_id)
        result = utils.remove_korean_lines(result)
        return result
    except:
        if verbose:
            print("Couldn't find lyrics of given id:", music_id)
    return ""

def main():
    parser = argparse.ArgumentParser(description='Fetch lyrics from bugs')
    parser.add_argument('music_id', help='Music\'s id, or Input file path of music ids. Whether it is a file or not is determined by the dot(.).')
    parser.add_argument('--out', help='Output file path. Outputs to standard output if not specified.')
    parser.add_argument('--verbose', action='store_true', help='Prints current queries and progress')
    args = parser.parse_args()

    if not args.music_id:
        if args.verbose:
            print("Error: Please provide an music id")
        return
    bugs.verbose = args.verbose
    results = []
    if is_file(args.music_id):
        with open(args.music_id, 'r', encoding='UTF-8') as file:
            for line in file:
                query_result = get_lyrics(line.strip(), args.verbose)
                results.append(query_result)
    else:
        query_result = get_lyrics(args.music_id, args.verbose)
        results.append(query_result)

    if not args.out:
        json_str = json.dumps(results, separators=(',', ':'), ensure_ascii=False)
        print(json_str)
    else:
        with open(args.out, 'w', encoding='UTF-8') as json_file:
            json.dump(results, json_file, indent=4, ensure_ascii=False)
            if args.verbose:
                print("Saved results as json:", args.out)

if __name__ == "__main__":
    main()