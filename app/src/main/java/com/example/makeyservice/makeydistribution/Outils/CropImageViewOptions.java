package com.example.makeyservice.makeydistribution.Outils;

import android.util.Pair;

import com.theartofdev.edmodo.cropper.CropImageView;

/**
 * Created by makeyservice on 28/06/2017.
 */

public final class CropImageViewOptions {

    public CropImageView.ScaleType scaleType = CropImageView.ScaleType.CENTER_INSIDE;

    public CropImageView.CropShape cropShape = CropImageView.CropShape.RECTANGLE;

    public CropImageView.Guidelines guidelines = CropImageView.Guidelines.ON_TOUCH;

    public Pair<Integer, Integer> aspectRatio = new Pair<>(1, 1);

    public boolean autoZoomEnabled;

    public int maxZoomLevel;

    public boolean fixAspectRatio;

    public boolean multitouch;

    public boolean showCropOverlay;

    public boolean showProgressBar;

    public boolean flipHorizontally;

    public boolean flipVertically;
}
