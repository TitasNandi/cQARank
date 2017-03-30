# QARank - Answer Selection and Ranking Tool for Community Question Answering sites
QARank is licensed under ASL 2.0 and other lenient licenses, allowing its use for academic and commercial purposes without restrictions.

## Downloading QARank
* Download the jar file of the project from [here](https://github.com/TitasNandi/cQARank/releases/download/1.0/QARank.jar).
* Alternatively, download the [zip](https://github.com/TitasNandi/QARank/archive/1.0.zip) of the java project and import it as a **maven** project in eclipse for experimentation.

## Downloading Data
* QARank requires a training xml file, a test xml file and unannotated data to train models.
* The system was trained and tested on **[SemEval 2017 - Task 3: Community Question Answering](http://alt.qcri.org/semeval2017/task3/) Subtask A** data.
* Create a directory named **xml_files** in your local machine.
* The training+dev data can be downloaded from [here](http://alt.qcri.org/semeval2016/task3/data/uploads/semeval2016-task3-cqa-ql-traindev-v3.2.zip).
* The test data for 2017 can be downloaded from
[here](http://alt.qcri.org/semeval2017/task3/data/uploads/semeval2017_task3_test_input_abcd.zip).
* After unzipping this folder, move to `semeval2016-task3-cqa-ql-traindev-v3.2/v3.2/train/`. The entire training data for Task 3 can be found here. 
 * Choose any of the *subtask A* train files for training and copy it to *xml_files* directory. Rename the training file **train.xml**.
 * Alternatively, combine various training xml files into one file *train.xml* for larger training data. Make sure to preserve the XML tree structure while doing this.
* Similarly, choose one of the *subtask A* files in `semeval2016-task3-cqa-ql-traindev-v3.2/v3.2/dev/` or `semeval2016_task3_tests/SemEval2016_task3_test/English/` as test data and rename it **test.xml**.
* The unannotated data can be downloaded from [here](http://alt.qcri.org/semeval2016/task3/data/uploads/QL-unannotated-data-subtaskA.xml.zip).
* Download the *python scripts* required to run the system from [here](https://github.com/TitasNandi/cQARank/releases/download/1.0/resources_QARank.zip).
 * Unzip this `resources_QARank` folder in a suitable place.
* The trained word embeddings on the large unannotated data can be found [here](https://github.com/TitasNandi/cQARank/releases/download/1.0/vectors_unannotated.txt).

## Running QARank
* Running QARank can be done with two formats of input xml files
  * If the training and test files are specific to subtask A (does not contain Original Questions), then run the jar with *0* as the flag
  ```
  java -Xmx10g -jar QARank.jar [absolute-path-to-xml_files-folder] [absolute-path-to-resources-folder] 0
  ```
  * If the training and test files contain both Original and Related Questions, then run the jar with *1* as the flag
   ```
  java -Xmx10g -jar QARank.jar [absolute-path-to-xml_files-folder] [absolute-path-to-resources-folder] 1
  ```
* The system will generate all folders and required files.
* The final MAP scores of the system and the SVM accuracy can be found in **result_files/final_scores.txt** file.
* Users can run the system on a different dataset, given the training and test files are in the format as in SemEval 2017 - Task 3.  
* The evaluation scripts used in the system can be looked up [here](http://alt.qcri.org/semeval2017/task3/data/uploads/semeval2017_task3_submissions_and_scores.zip).

## Contents
* [Introduction](https://github.com/TitasNandi/cQARank/blob/master/QARank/src/main/java/doc/Home.md)
* [Features](https://github.com/TitasNandi/cQARank/blob/master/QARank/src/main/java/doc/Features.md)
* [File Format](https://github.com/TitasNandi/cQARank/blob/master/QARank/src/main/java/doc/file_format.md)
* [Slides](https://github.com/TitasNandi/cQARank/releases/download/1.0/cQARank_prezi.pdf)


