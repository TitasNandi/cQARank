## Features

**1. String Similarity Features**

The String Similarity Features include various String Similarity metrics that find the string match between question and related question.
* n-gram distance (n = 1,2,3)
* cosine similarity (n = 1,2,3)
* Jaccard similarity (n = 1,2,3)
* QGram distance (n = 1,2,3)
* Sorensen similarity (n = 1,2,3)
* JaroWinkler similarity
* Damerau distance
* Levenshtein distance
* Normalized Levenshtein distance
* Longest Common Subsequence

**2. Meta Data Features**

This set of features encapsulates the position of comment, if the comment is an acknowledgment and the length of the comment.

**3. Word Embedding Features**

We train word embeddings of dimension 100 using [Word2Vec](http://deeplearning4j.org/word2vec) on unannotated data. From these we compute sentence vectors and various distance metrics between question and comment.

**4. Topic features**

We train an LDA Topic Model using [Mallet](http://mallet.cs.umass.edu/topics.php) and find topic distributions in training and test data. From these topic vectors and words, we obtain various features.

**5. Keyword and Named Entity Features**

These features compute the keyword match and account for the presence of named entities in the question and comment and if these named entities belong to same entity categories etc.

**6. Stacking Features**

These features are extracted from the scores of subtask A and subtask C, which propagate useful information which can then be used to strengthen the feature set of this subtask. These scores are used as distributions over 10 points, from which various correlation coefficients can be computed.

## Evaluation
We combine all feature files, normalize it and feed it to a Support Vector Machine for binary classification. We use the SVM probability scores for ranking purposes.
The scoring measure used is Mean Average Precision (MAP). 
