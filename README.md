# QARank - Answer Selection and Ranking Tool for Community Question Answering sites
QARank is licensed under ASL 2.0 and other lenient licenses, allowing its use for academic and commercial purposes without restrictions.

## Downloading QARank
* Download the jar file of the project from [here](https://github.com/tudarmstadt-lt/QASelection/releases/download/release1/QARank.jar).
* Alternatively, download the [zip](https://github.com/tudarmstadt-lt/QASelection/archive/master.zip) of the java project and import it as a **maven** project in eclipse for experimentation.

## Downloading Data
* QARank requires a training file, a test file and unannotated data to train models.
* The system was trained and tested on **[SemEval 2016 - Task 3: Community Question Answering](http://alt.qcri.org/semeval2016/task3/) - Subtask A** data.
* Create a directory named **xml_files** in your local machine.
* The training+dev data can be downloaded from [here](http://alt.qcri.org/semeval2016/task3/data/uploads/semeval2016-task3-cqa-ql-traindev-v3.2.zip).
* The original test data can be downloaded from
[here](http://alt.qcri.org/semeval2016/task3/data/uploads/semeval2016_task3_tests.zip).
* After unzipping this folder, move to `semeval2016-task3-cqa-ql-traindev-v3.2/v3.2/train/`. The entire training data for Task 3 can be found here. 
 * Choose any of the *subtask A* train files for training and copy it to *xml_files* directory. Rename the training file **train.xml**.
 * Alternatively, combine various training xml files into one file *train.xml* for larger training data. Make sure to preserve the XML tree structure while doing this.
* Similarly, choose one of the *subtask A* files in `semeval2016-task3-cqa-ql-traindev-v3.2/v3.2/dev/` or `semeval2016_task3_tests/SemEval2016_task3_test/English/` as test data and rename it **test.xml**.
* The unannotated data can be downloaded from [here](http://alt.qcri.org/semeval2016/task3/data/uploads/QL-unannotated-data-subtaskA.xml.zip).
 * After unzipping the this folder, move to `QL-unannotated-data-subtaskA.xml/QL-unannotated-data-subtaskA.xml` and copy this file to *xml_files* directory and rename it **unannotated.xml**.
 * This file is large and requires a lot of memory to train models. To avoid larger training time, one can use training data for the same task. Make sure to rename the file to **unannotated.xml**.
* Download the *python scripts* required to run the system from [here](https://github.com/tudarmstadt-lt/QASelection/releases/download/release1/resources.zip).
  * Unzip this `resources` folder in a suitable place.
* The trained word embeddings on the large unannotated data can be found [here](https://github.com/tudarmstadt-lt/QASelection/releases/download/release1/vectors_unannotated.txt).

## Running QARank
* Run QARank jar as
```
java -Xmx10g -jar QARank.jar [absolute-path-to-xml_files-folder] [absolute-path-to-resources-folder]
```
* The system will generate all folders and required files.
* The final MAP scores of the system and the SVM accuracy can be found in **result_files/final_scores.txt** file.
* Users can run the system on a different dataset, given the training and test files are in the format as in SemEval 2016 - Task 3.  
* The evaluation scripts used in the system can be looked up [here](http://alt.qcri.org/semeval2016/task3/data/uploads/semeval2016_task3_submissions_and_score.zip).

## Contents
* [Introduction](https://github.com/tudarmstadt-lt/QASelection/blob/master/src/main/java/cqa/doc/Home.md)
* [Features](https://github.com/tudarmstadt-lt/QASelection/blob/master/src/main/java/cqa/doc/Features.md)
* [File Format](https://github.com/tudarmstadt-lt/QASelection/blob/master/src/main/java/cqa/doc/file_format.md)
* [Slides](https://github.com/tudarmstadt-lt/QASelection/releases/download/release1/conference_presentation.pdf)


