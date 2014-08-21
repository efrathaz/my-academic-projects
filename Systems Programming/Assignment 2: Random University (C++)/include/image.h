#ifndef IMAGE_H_
#define IMAGE_H_
#include <highgui.h>
#include <string>
#include <cv.h>
#include <iostream>

using namespace cv;
using namespace std;

class ImageLoader{
private:
    cv:: Mat m_image;

public:
	ImageLoader();
    /** create a new image with the size=width*height */
    ImageLoader(int width, int height);
    ImageLoader(const ImageLoader&);
    /** import an image from a file location */
    ImageLoader(const std::string& fileName);
    /** display an image on screen */
    void displayImage();
    /** matrix getter */
    cv:: Mat& getImage();
    /** save image to filename */
    void saveImage(const std::string& filename);
    ImageLoader& operator=(const ImageLoader&);
    virtual ~ImageLoader();
};

class ImageOperations{
public:
    /** Convert src from a color rgb picture to a greyscales picture. The result is stored inside dst */
    void rgb_to_greyscale(cv::Mat& src, cv::Mat& dst);
    /** Resize original picture into the dimension of image destination */
    void resize(cv::Mat& src, cv::Mat& dst);
    /** Copy image src and paste it inside image dst at location (xLocation,0) */
    void copy_paste_image(cv::Mat& src, cv::Mat& dst, int xLocation);
};

#endif /* IMAGE_H_ */
