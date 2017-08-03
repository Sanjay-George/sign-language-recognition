import cv2
import numpy as np
import imutils

def resize(img):
    # resize the image to 100X100 by maintaining the same aspect ratio
    r = 256.0 / img.shape[1]
    dim = (256, int(img.shape[0] * r))
    return cv2.resize(img, dim, interpolation = cv2.INTER_AREA)


def resize1(img):
    # resize the image to 100X100 by maintaining the same aspect ratio
    r = 100.0 / img.shape[1]
    dim = (100, int(img.shape[0] * r))
    return cv2.resize(img, dim, interpolation = cv2.INTER_AREA)


def morph(img):
    kernel = np.ones((5,5),np.uint8)
    erosion = cv2.erode(img,kernel,iterations = 1)
    dilation = cv2.dilate(erosion,kernel,iterations = 1)

    return dilation


def pre_process(filename):
    img=cv2.imread(filename)

    #reduced=resize(img)
    reduced=img
    ycrcb = cv2.cvtColor(reduced,cv2.COLOR_BGR2YCR_CB)

    skin_ycrcb_mint = np.array((0, 133, 77))
    skin_ycrcb_maxt = np.array((255, 173, 127))
    skin_ycrcb = cv2.inRange(ycrcb, skin_ycrcb_mint, skin_ycrcb_maxt)

    skin=cv2.bitwise_and(reduced,reduced, mask = skin_ycrcb)

    kernel = cv2.getStructuringElement(cv2.MORPH_ELLIPSE, (3,3))
    skin = cv2.erode(skin, kernel, iterations = 3)
    skin= cv2.dilate(skin, kernel, iterations = 2)
    edges= cv2.Canny(skin,55,105)

    (_,cnts, _) = cv2.findContours(edges.copy(), cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    cnts = sorted(cnts, key = cv2.contourArea, reverse = True)
    screenCnt = None


    for i, c in enumerate(cnts[:3]):
        cv2.drawContours(edges, cnts, i, (255, 0, 0), 3)

    edges = cv2.resize(edges, (100, 100))
    save_path='uploads/img_proc.jpg'
    cv2.imwrite('img_proc.jpg',edges)
    
    return save_path

def pre_process2(filename):
    img=cv2.imread(filename)

    dst = cv2.fastNlMeansDenoisingColored(img,None,10,10,7,21)
    gray = cv2.cvtColor(dst, cv2.COLOR_BGR2GRAY)

    # convolute with proper kernels
    #laplacian = cv2.Laplacian(gray,cv2.CV_64F)
    sobelx = cv2.Sobel(gray,cv2.CV_64F,1,0,ksize=5)  # x
    #sobely = cv2.Sobel(gray,cv2.CV_64F,0,1,ksize=5)  # y
    #canny = cv2.Canny(gray,200,200)

    resized = cv2.resize(sobelx, (100, 100))
    save_path='uploads/img_proc.jpg'
    cv2.imwrite('img_proc.jpg',sobelx)
    
    return save_path

def hog(img):
	winSize = (16,16)
	blockSize = (16,16) #only (16,16) supported
	blockStride = (8,8)
	cellSize = (8,8) # only(8,8) supported
	nbins = 9
	derivAperture = 1
	winSigma = 4. #guassian smoothing parameter
	histogramNormType = 0
	L2HysThreshold = 2.0000000000000001e-01
	gammaCorrection = 0
	nlevels = 64

	image = cv2.imread(img)
	#hog=cv2.HOGDescriptor()
	hog = cv2.HOGDescriptor(winSize,blockSize,blockStride,cellSize,nbins,derivAperture,winSigma,
	                       histogramNormType,L2HysThreshold,gammaCorrection,nlevels)
	#compute(img[, winStride[, padding[, locations]]]) -> descriptors
	winStride = (4,4)
	padding = (8,8)
	locations = ((10,20),)
	hist = hog.compute(image,winStride,padding,locations)
	hist=hog.compute(image)
	return hist

def fft(filename):
    img = cv2.imread('img-datasets/test/C.jpg',0)

    dft = cv2.dft(np.float32(img),flags = cv2.DFT_COMPLEX_OUTPUT)
    dft_shift = np.fft.fftshift(dft)

    magnitude_spectrum = 20*np.log(cv2.magnitude(dft_shift[:,:,0],dft_shift[:,:,1]))

    return magnitude_spectrum