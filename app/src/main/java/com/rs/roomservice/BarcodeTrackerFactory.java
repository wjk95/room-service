package com.rs.roomservice;


import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;
import com.rs.roomservice.ui.camera.GraphicOverlay;

/**
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 */
class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {

    private BarcodeGraphicTracker.BarcodeDetectorListener mDetectorListener;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    BarcodeTrackerFactory(GraphicOverlay<BarcodeGraphic> barcodeGraphicOverlay, BarcodeGraphicTracker.BarcodeDetectorListener detectorListener) {
        mGraphicOverlay = barcodeGraphicOverlay;
        mDetectorListener = detectorListener;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay);
        BarcodeGraphicTracker graphicTracker = new BarcodeGraphicTracker(mGraphicOverlay, graphic);
        if(mDetectorListener != null)
            graphicTracker.setDetectorListener(mDetectorListener);
        return graphicTracker;
    }

}