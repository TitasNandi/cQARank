
**QCRank** is a **freely available open source tool** for External Comment Selection and Ranking in Community Question Answering sites. The tool has been trained and tested on the **SemEval 2017 Task 3** data. Though the tool has been applied on CQA sites' data until now, it can easily be used for **feature extraction** and **comment ranking and classification** on data available from other sources as well.

The system when tested on the test data of SemEval 2017 for subtask C gives a MAP score of **15.46** and our submission stood at the 1st position overall. The detailed results can be checked [here](http://alt.qcri.org/semeval2017/task3/data/uploads/semeval2017_task3_results.pdf). 

The tool takes a **feature based** approach for comment selection and ranking. The tool combines various **String Similarity, word embedding, topic modeling keyword, dialogue and numerous domain specific features** to build a robust Answer Selection Model. The software components, the training data and all data used for feature generation are distributed under permissive licenses, thus this ranker tool can be used in academic and commercial settings without restrictions or fees.
