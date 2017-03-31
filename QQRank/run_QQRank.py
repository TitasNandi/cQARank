import sys
import urllib
import zipfile
import os
import shutil
import subprocess

#Print Download Progress
def dlProgress(count, blockSize, totalSize):
      percent = int(count*blockSize*100/totalSize)
      sys.stdout.write("%2d%%" % percent)
      sys.stdout.write("\b\b\b")
      sys.stdout.flush()

#Download Data from SemEval site 
def download_data(dir_path):
	print ('Downloading semeval2016-task3-cqa-ql-traindev-v3.2.zip')
	urllib.urlretrieve ('http://alt.qcri.org/semeval2016/task3/data/uploads/semeval2016-task3-cqa-ql-traindev-v3.2.zip', 'semeval2016-task3-cqa-ql-traindev-v3.2.zip', reporthook=dlProgress)
	z = zipfile.ZipFile(dir_path+'/semeval2016-task3-cqa-ql-traindev-v3.2.zip')
	z.extractall(dir_path)
	z.close()
	print('\nDownloading QQRank.jar')
	urllib.urlretrieve ('https://github.com/TitasNandi/cQARank/releases/download/1.0/QQRank.jar', 'QQRank.jar', reporthook=dlProgress)
	print('\nDownloading resources_QQRank.zip')
	urllib.urlretrieve ('https://github.com/TitasNandi/cQARank/releases/download/1.0/resources_QQRank.zip', 'resources_QQRank.zip', reporthook=dlProgress)
	z = zipfile.ZipFile(dir_path+'/resources_QQRank.zip')
	z.extractall(dir_path)
	z.close()

#Create all required folders and subfolders and move files
def create_folders(dir_path):
	train_dir = dir_path+'/v3.2/train'
	test_dir = dir_path+'/v3.2/dev'
	print ('\nCreating required directories')
	if not os.path.exists(dir_path+'/QQRank'):
		os.makedirs(dir_path+'/QQRank')

	if not os.path.exists(dir_path+'/QQRank/xml_files'):
		os.makedirs(dir_path+'/QQRank/xml_files')
	xml_dir = dir_path+'/QQRank/xml_files'
	# A particular train file is used. Can be changed to train the system on a different file
	shutil.copy(train_dir+'/SemEval2016-Task3-CQA-QL-train-part2.xml', xml_dir)
	os.rename(xml_dir+'/SemEval2016-Task3-CQA-QL-train-part2.xml', xml_dir+'/train.xml')
	# A particular test file is used. Can be changed to test the system on a different file
	shutil.copy(test_dir+'/SemEval2016-Task3-CQA-QL-dev.xml', xml_dir)
	os.rename(xml_dir+'/SemEval2016-Task3-CQA-QL-dev.xml', xml_dir+'/test.xml')

if __name__ == '__main__':	
	dir_path = os.path.dirname(os.path.realpath(__file__))
	# Downloads data. Comment this to avoid repeated downloads
	download_data(dir_path)
	# Creates required folders
	create_folders(dir_path)
	xml_dir = dir_path+'/QQRank/xml_files'
	#Run the jar file
	subprocess.call(['java', '-jar', dir_path+'/QQRank.jar', xml_dir, dir_path+'/resources_QQRank'])