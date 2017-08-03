package com.example.vishwashrisairam.opencvcamera2;

/**
 * Created by Vishwashrisairam on 2/11/2017.
 */

public class NativeClass {
    public native static int convertGray(long MatAddrRgba,long MatAddrGray);
    public native static void faceDetection(long addrRgba);
}
