import argparse
import json
import os

from src import bugs
from src import utils

bugs.max_delay = 3

def is_file(string):
    _, file_extension = os.path.splitext(string)
    return bool(file_extension)

def get_query_result(search_query, verbose = False):
    query = search_query.strip()
    if verbose:
        print("Querying:", query)
    result = bugs.search_results(query)
    return result

def main():
    parser = argparse.ArgumentParser(description='Fetch lyrics from bugs')
    parser.add_argument('query', help='Search query string, or Input file path of search queries. Whether it is a file or not is determined by the dot(.).')
    parser.add_argument('--out', help='Output file path. Outputs to standard output if not specified.')
    parser.add_argument('--verbose', action='store_true', help='Prints current queries and progress')
    args = parser.parse_args()

    if not args.query:
        if args.verbose:
            print("Error: Please provide an query using --query")
        return
    bugs.verbose = args.verbose
    results = []
    if is_file(args.query):
        with open(args.query, 'r', encoding='UTF-8') as file:
            for line in file:
                query_result = get_query_result(line.strip(), args.verbose)
                results.append(query_result)
    else:
        query_result = get_query_result(args.query, args.verbose)
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