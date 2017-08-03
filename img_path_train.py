import os 

path="img-datasets"

file=open("image_path.txt","w")

for(dirpath,dirnames,filenames) in os.walk(path):
	print(str(len(filenames)))
	for filename in filenames:
		parent=str(dirpath)+"/"+str(filename)
		label=dirpath[len(dirpath)-1]
		line = parent +"\t" + label +"\n"
		file.write(line)	


file.close()