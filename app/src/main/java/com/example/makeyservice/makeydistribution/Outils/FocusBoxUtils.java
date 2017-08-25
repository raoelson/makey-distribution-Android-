package com.example.makeyservice.makeydistribution.Outils;

import android.content.Context;
import android.graphics.Point;
import android.util.Size;
import android.view.Display;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by makeyservice on 29/06/2017.
 */

public class FocusBoxUtils {
    private static int MIN_PREVIEW_PIXELS = 470 * 320;
    private static int MAX_PREVIEW_PIXELS = 800 * 600;

    public static Point getScreenResolution(Context context) {

        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();

        int width = display.getWidth();
        int height = display.getHeight();

        return new Point(width, height);

    }

    public static Point getCameraResolution(Context context, Size camera) {
        return findBestPreviewSizeValue(camera, getScreenResolution(context));
    }

    public static Point findBestPreviewSizeValue(Size parameters,
                                                 Point screenResolution) {

        List<Size> parSizes = new ArrayList<Size>();
        parSizes.add(parameters);
        Collections.sort(parSizes, new Comparator<Size>() {

            @Override
            public int compare(Size a, Size b) {
                int aPixels = a.getHeight() * a.getWidth();
                int bPixels = b.getHeight() * b.getWidth();
                if (bPixels < aPixels) {
                    return -1;
                }
                if (bPixels > aPixels) {
                    return 1;
                }
                return 0;
            }
        });

        Point bestSize = null;
        float screenAspectRatio = (float) screenResolution.x / (float) screenResolution.y;

        float diff = Float.POSITIVE_INFINITY;
        for (Size supportedPreviewSize : parSizes) {
            int realWidth = supportedPreviewSize.getWidth();
            int realHeight = supportedPreviewSize.getHeight();
            int pixels = realWidth * realHeight;
            if (pixels < MIN_PREVIEW_PIXELS || pixels > MAX_PREVIEW_PIXELS) {
                continue;
            }
            boolean isCandidatePortrait = realWidth < realHeight;
            int maybeFlippedWidth = isCandidatePortrait ? realHeight : realWidth;
            int maybeFlippedHeight = isCandidatePortrait ? realWidth : realHeight;
            if (maybeFlippedWidth == screenResolution.x && maybeFlippedHeight == screenResolution.y) {
                return new Point(realWidth, realHeight);
            }
            float aspectRatio = (float) maybeFlippedWidth / (float) maybeFlippedHeight;
            float newDiff = Math.abs(aspectRatio - screenAspectRatio);
            if (newDiff < diff) {
                bestSize = new Point(realWidth, realHeight);
                diff = newDiff;
            }
        }

        if (bestSize == null) {
            bestSize = new Point(parameters.getWidth(), parameters.getHeight());
        }
        return bestSize;
    }
}
