/*
 * Copyright (C) 2018 CW Chiu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cw.ClientJSON_listview;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cw.ClientJSON_listview.uil.UilCommon;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


public class UtilImage_bitmapLoader
{
//	private Bitmap thumbnail;
//	private AsyncTaskVideoBitmap mVideoAsyncTask;
	private SimpleImageLoadingListener mSimpleUilListener, mSimpleUilListenerForVideo;
	private ImageLoadingProgressListener mUilProgressListener;
	private ProgressBar mProgressBar;
	private ImageView mPicImageView;
    Activity mAct;

	public UtilImage_bitmapLoader(ImageView picImageView,
	                              String mPictureUriInDB,
	                              final ProgressBar progressBar,
	                              DisplayImageOptions options,
	                              Activity act )
	{
 	    setLoadingListeners();
	    mPicImageView = picImageView;
	    mProgressBar = progressBar;
	    mAct = act;
		Uri imageUri = Uri.parse(mPictureUriInDB);
		String pictureUri = imageUri.toString();
		UilCommon.imageLoader
				 .displayImage(	pictureUri,
								mPicImageView,
								options,
								mSimpleUilListener,
								mUilProgressListener);

	}

	private  void setLoadingListeners()
    {
        // set image loading listener
        mSimpleUilListener = new SimpleImageLoadingListener() 
        {
      	    @Override
      	    public void onLoadingStarted(String imageUri, View view)
      	    {
      		    mPicImageView.setVisibility(View.GONE);
      		    mProgressBar.setProgress(0);
      		    mProgressBar.setVisibility(View.VISIBLE);
      	    }

      	    @Override
      	    public void onLoadingFailed(String imageUri, View view, FailReason failReason)
      	    {
      		    mProgressBar.setVisibility(View.GONE);
      		    mPicImageView.setVisibility(View.VISIBLE);

				String message = null;
				switch (failReason.getType()) {
					case IO_ERROR:
				        message = "Input/Output error";
//						message = mAct.getResources().getString(R.string.file_not_found);
						break;
					case DECODING_ERROR:
						message = "Image can't be decoded";
						break;
					case NETWORK_DENIED:
						message = "Downloads are denied";
						break;
					case OUT_OF_MEMORY:
						message = "Out Of Memory error";
						break;
					case UNKNOWN:
						message = "Unknown error";
						break;
				}
				Toast.makeText(mAct, message, Toast.LENGTH_SHORT).show();
      	    }

      	    @Override
      	    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
      	    {
      		    super.onLoadingComplete(imageUri, view, loadedImage);
      		    mProgressBar.setVisibility(View.GONE);
      		    mPicImageView.setVisibility(View.VISIBLE);
      	    }
  		};

  		// set image loading listener for video
  		mSimpleUilListenerForVideo = new SimpleImageLoadingListener() 
  		{
  			@Override
  			public void onLoadingStarted(String imageUri, View view)
  			{
  				mPicImageView.setVisibility(View.GONE);
  				mProgressBar.setProgress(0);
  				mProgressBar.setVisibility(View.VISIBLE);
  			}

  			@Override
  			public void onLoadingFailed(String imageUri, View view, FailReason failReason)
  			{
  				mProgressBar.setVisibility(View.GONE);
  				mPicImageView.setVisibility(View.VISIBLE);

  			}

  			@Override
  			public void onLoadingComplete(final String imageUri, View view, Bitmap loadedImage)
  			{
  				super.onLoadingComplete(imageUri, view, loadedImage);
  				mProgressBar.setVisibility(View.GONE);
  				mPicImageView.setVisibility(View.VISIBLE);
  			}
  		};

  		// Set image loading process listener
  		mUilProgressListener = new ImageLoadingProgressListener() 
  		{
  			@Override
  			public void onProgressUpdate(String imageUri, View view, int current, int total)
  			{
  				mProgressBar.setProgress(Math.round(100.0f * current / total));
  			}
  		};
    }
    
}