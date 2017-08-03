from sklearn.metrics import confusion_matrix,roc_curve
from sklearn.externals import joblib
import numpy as np 
import os
import csv
from sklearn import svm
from sklearn import model_selection
from sklearn import linear_model
from sklearn import metrics
from utils import *
import pickle
import time
from sklearn.externals import joblib

def prec(num):
    return "%0.5f"%num

outfile = open("output.txt","w")

clf = svm.LinearSVC()
outfile.write(str(clf))

images=[]
labels=[]

with open("test_paths.txt",'r') as file:
    lines = file.readlines()

for line in lines:
    image_path,label=line.split()

    processed_path=pre_process(image_path)
    hist=hog('img_proc.jpg')
    
    
    images.append(hist.flatten())
    labels.append(label)

clf = joblib.load('train.pkl')
predicted = clf.predict(images)

print(labels)
print(predicted)

for i in range(len(predicted)):
	outfile.write(str(labels[i])+"-"+str(predicted[i])+"\n")
score = clf.score(images,labels)
outfile.write("Score:"+str(score)+"\n")

outfile.close()