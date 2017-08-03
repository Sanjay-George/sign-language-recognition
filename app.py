import os
from flask import Flask,render_template,jsonify,request 
from utils import *
from sklearn.externals import joblib

app=Flask(__name__)

UPLOAD_FOLDER = 'uploads/'
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER  

cache={}


@app.route("/")
def hello():
	cache['start_frames']=[]
	return "Hello world"

@app.route('/upload',methods=['POST','GET'])
def upload():
	if request.method=='POST':
		resp={}
		file = request.files['file']
		index=request.form['index']
		ctr=request.form['counter']		
		predicted=[None for i in range(3)]
		print(index)
		print(ctr)

		clf =[joblib.load('final.pkl') for i in range(3)]

		if index=='-1':
			print('hey')
			j=0
			for i in [3,round(int(ctr)/2),int(ctr)-3]:
				fname='sample_picture'+str(i)+'.jpg'
				pth=os.path.join(app.config['UPLOAD_FOLDER'],fname)
				ppath=pre_process(pth)
				hist=hog('img_proc.jpg')
				res=clf[j].predict(hist.flatten())
				predicted[j]=res[0]
				j+=1
			print(predicted)	
			resp['status']=200
			resp['message']=max(predicted,key=predicted.count)
			return jsonify(resp)
			
		
		if file:
			filename=file.filename
			f=filename.split('.')
			filename=f[0]+index+'.'+f[1]
			path=os.path.join(app.config['UPLOAD_FOLDER'],filename)
			file.save(path)
			size=os.stat(path).st_size

			
			#processed_path=pre_process(path)
			#print(processed_path)
		
			# #hog 
			#hist=hog('img_proc.jpg')			
			#predicted = clf.predict(hist.flatten())
			
			#Generate response
			resp['status']=200
			resp['message']="File uploaded successfully"
			return jsonify(resp)
		else:
			resp['status']=123
			resp['message']="No File found"
			return jsonify(resp)

	else:
		#return render_template('upload.html')
		return "Upload"



if __name__=="__main__":
	app.run(host='0.0.0.0',debug=True,threaded=True)
