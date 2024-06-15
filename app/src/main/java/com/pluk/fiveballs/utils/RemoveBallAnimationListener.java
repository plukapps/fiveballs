package com.pluk.fiveballs.utils;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public abstract class RemoveBallAnimationListener implements AnimationListener {

	private ImageView imageView;
	private boolean enabled;

	public RemoveBallAnimationListener(ImageView imageView, boolean enabled) {
		this.imageView = imageView;
		this.enabled = enabled;
	}
	
	public void onAnimationEnd(Animation animation) {
		onRemoveEnd(imageView, enabled);
	}

	public abstract void onRemoveEnd(ImageView imageView, boolean enabled);

	public void onAnimationRepeat(Animation animation) {
	}

	public void onAnimationStart(Animation animation) {
	}

}
