import re

def remove_korean_lines(input_string):
    # Regular expression pattern to match Korean characters
    korean_pattern = re.compile("[\uAC00-\uD7A3]+")

    lines = input_string.split('\n')
    filtered_lines = [line.strip().replace('\n', '').replace('\r', '') for line in lines if not korean_pattern.search(line)]
    filtered_string = '\n'.join(filtered_lines)
    cleaned_string = re.sub(r'\n+', '\n', filtered_string)
    cleaned_string = re.sub(r'(?!\n)\s+', ' ', cleaned_string)
    return cleaned_string