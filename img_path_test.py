import os

path = "img-datasets/test"
file = open("test_paths.txt","w")
for (dirpath,dirnames,filenames) in os.walk(path):
	for filename in filenames:
		parent = str(dirpath)+"/"+str(filename)
		label = filename[0]
		line = parent + "\t" + label + '\n'
		file.write(line)
file.close()