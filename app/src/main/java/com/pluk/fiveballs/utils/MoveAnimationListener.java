package com.pluk.fiveballs.utils;

import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.pluk.fiveballs.model.Coord;

public abstract class MoveAnimationListener implements AnimationListener {

	private Coord init;
	private Coord end;

	public MoveAnimationListener(Coord init, Coord end) {
		this.init = init;
		this.end = end;
	}
	
	public void onAnimationEnd(Animation animation) {
		onMoveEnd(init, end);
	}

	public void onAnimationRepeat(Animation animation) {
	}

	public void onAnimationStart(Animation animation) {
	}

	public abstract void onMoveEnd(Coord init, Coord end);
}
