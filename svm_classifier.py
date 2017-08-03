from sklearn.metrics import confusion_matrix,roc_curve
from sklearn.externals import joblib
import numpy as np 
import os
import csv
from sklearn import svm
from sklearn import model_selection
from sklearn import cross_validation
from sklearn import linear_model
from sklearn.grid_search import GridSearchCV
import matplotlib.pyplot as plt
from sklearn import metrics
from utils import *
import pickle
import time

def prec(num):
	return "%0.5f"%num

outfile = open("output/svm_output_8-05.txt","a")


images=[]
labels=[]

with open("image_path.txt",'r') as file:
	lines = file.readlines()

for line in lines:
	image_path,label=line.split()

	processed_path=pre_process(image_path)
	hist=hog('img_proc.jpg')
	#hist=fft('img_proc.jpg')
	
	images.append(hist.flatten())
	labels.append(label)


print("\nDividing dataset using `train_test_split()` -:\n")
outfile.write("\n\ntrain_test_split() on dataset:\n\n")	
training_images, testing_images, training_labels, testing_labels = model_selection.train_test_split(images,labels, test_size=0.25, random_state=0)



#clf = svm.LinearSVC()
clf=svm.SVC(kernel='poly', C= 1, degree= 2, gamma=0.01, coef0= 10)



# param_grid = {'kernel': ['linear'],'C': [1e0, 1e1, 1e2, 1e3, 1e4]}

# param_grid = {'kernel': ['poly'],'C': [1e0, 1e1, 1e2, 1e3],'degree': [2, 4],'coef0': [1e0, 1e1, 1e2],'gamma': [1e-3, 1e-2, 1e-1]}

# param_grid = {'kernel': ['rbf'],'C': [1e0, 1e1, 1e2, 1e3, 1e4],'gamma': [1e-4, 1e-3, 1e-2, 1e-1]}

# param_grid = {'kernel': ['sigmoid'],'gamma': [1e-3, 1e-2, 1e-1, 1e0, 1e1, 1e2],'C': [1e-1, 1e0, 1e1, 1e2, 1e3, 1e5],'coef0': [-1e4, -1e3, -1e2, -1e1, 1e0, 1e1, 1e2]}



# svc=svm.SVC(class_weight='auto')
# param_grid = {'kernel': ['poly'],'C': [1e0, 1e1, 1e2, 1e3],'degree': [2, 4],'coef0': [1e0, 1e1, 1e2],'gamma': [1e-3, 1e-2, 1e-1]}
# strat_2fold=cross_validation.StratifiedKFold(training_labels,n_folds=2)
# clf=GridSearchCV(svc,param_grid,n_jobs=1,cv=strat_2fold)

#outfile.write('\nBest Parameters:'+str(clf.best_params_))
outfile.write(str(clf))
t2=time.time()
clf = clf.fit(training_images,training_labels)

t_time=round(time.time()-t2,2)
t3=time.time()
score = clf.score(testing_images,testing_labels)
joblib.dump(clf, 'final.pkl')
print("Dumped")	
predicted = clf.predict(testing_images)
t4=round(time.time()-t3,2)
print(prec(score))

outfile.write('Training time:'+str(t_time)+'s\n')
outfile.write('Prediction time:'+str(t4)+'s\n')
outfile.write(prec(clf.score(testing_images, testing_labels))+'\n')
outfile.write(metrics.classification_report(testing_labels, predicted)+'\n')
outfile.write(str(metrics.confusion_matrix(testing_labels, predicted)))
outfile.close()