import warnings
import logging
warnings.filterwarnings('ignore', category=FutureWarning, message='.*resume_download.*')
logging.getLogger('transformers.tokenization_utils_base').setLevel(logging.ERROR)

import torch
from transformers import BertTokenizer, BertModel
from sklearn.metrics.pairwise import cosine_similarity

model_name = "tohoku-nlp/bert-large-japanese-v2"
model = None
tokenizer = None

def get_embedding(word):
    global model, tokenizer
    if model is None:
        tokenizer = BertTokenizer.from_pretrained(model_name)
        model = BertModel.from_pretrained(model_name)

    inputs = tokenizer(word, return_tensors="pt")
    outputs = model(**inputs)
    return outputs.last_hidden_state.mean(dim=1).squeeze().detach().numpy()

def get_similarity(embedding1, embedding2):
    cosine_sim = cosine_similarity([embedding1], [embedding2])
    return cosine_sim[0][0]