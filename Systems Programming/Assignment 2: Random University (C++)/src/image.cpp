#include "../include/image.h"

ImageLoader::ImageLoader():m_image(){}

ImageLoader::ImageLoader(int width, int height): m_image(width, height, CV_8UC3){}

ImageLoader::ImageLoader(const ImageLoader &I):m_image(I.m_image){}

ImageLoader::ImageLoader(const string& fileName): m_image(imread(fileName)){
  if (!m_image.data){
    cout << "Failed loading " << fileName << endl;
  }
}

ImageLoader::~ImageLoader(){
  m_image.release();
}

 ImageLoader& ImageLoader::operator=(const ImageLoader &I){
	if (this != &I){
		(this->m_image).release();
		this->m_image = I.m_image;
	}
	return *this;
}

void ImageLoader::displayImage(){
	// create image window named "My image"
	namedWindow("My image");
	// show the image on window
	imshow("My image", m_image);
	// wait key for 5000 ms
	waitKey(5000);
	cvDestroyWindow("My image");
}

cv::Mat& ImageLoader::getImage(){
    return m_image;
}

void ImageLoader::saveImage(const string& fileName){
    imwrite(fileName, m_image);
}

void ImageOperations::rgb_to_greyscale(cv::Mat& src, cv::Mat& dst){
    cv::cvtColor(src,dst,CV_RGB2GRAY);
    cv::Mat temp = dst.clone();
    cv::cvtColor(temp,dst,CV_GRAY2RGB);
    src.release();
    temp.release();
}

void ImageOperations::resize(cv::Mat& src, cv::Mat& dst){
    cv::resize(src,dst,dst.size());
    src.release();
}

void ImageOperations::copy_paste_image(cv::Mat& original, cv::Mat& destination, int xLocation){
    if(original.size().height > destination.size().height)
        throw ("original image is higher that destination image");
    cv::Rect roi(xLocation, 0, original.size().width, original.size().height);
    cv::Mat imageROI (destination, roi);
    original.copyTo(imageROI);
    original.release();
}
