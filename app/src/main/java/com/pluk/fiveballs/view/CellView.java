package com.pluk.fiveballs.view;

import java.util.HashMap;
import java.util.LinkedList;

import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.pluk.fiveballs.R;
import com.pluk.fiveballs.exceptions.AlreadySelectedBallExeption;
import com.pluk.fiveballs.exceptions.EmptyCellExeption;
import com.pluk.fiveballs.exceptions.NotEmptyCellExeption;
import com.pluk.fiveballs.exceptions.UnSelectedBallExpetion;
import com.pluk.fiveballs.handlers.IGameManager;
import com.pluk.fiveballs.model.BallColor;
import com.pluk.fiveballs.model.Consts;
import com.pluk.fiveballs.model.Coord;
import com.pluk.fiveballs.model.ImageType;
import com.pluk.fiveballs.utils.MoveAnimationListener;
import com.pluk.fiveballs.utils.RemoveBallAnimationListener;
import com.pluk.fiveballs.utils.SoundUtils;
import com.pluk.fiveballs.utils.SoundUtils.SoundType;
import com.pluk.fiveballs.utils.Utils;

public class CellView implements View.OnClickListener {
	
	private static final String TAG = "CellView";
	
	private IGameManager aGame = null;
	private GameActivity activity = null;
	
	private MediaPlayer audioPlayer = null;
	
	private static boolean enableSelect = true;
	
	public CellView (IGameManager aGame, GameActivity activity, MediaPlayer mp) {
		this.aGame = aGame;
		this.activity = activity;
		this.audioPlayer = mp;
	}

	public void onClick(View v) {
		
		if (!enableSelect) {
			return;
		}
		
		ImageView ballView = (ImageView) v;
		Coord coord = getCoord(ballView);
		
		ClickBallAction action = getClick(coord);
		try {
			
			switch (action) {
				case SELECT_BALL:
					selectBall(ballView, coord);
					break;
				case UNSELECT_BALL:
					unselect();
					break;
				case SELECT_OTHER_BALL:
					unselect();
					selectBall(ballView, coord);
					break;
				case MOVE_BALL:
					moveBall(coord);
					break;
				default: // NOTHING 
					break;
			}
		} catch (EmptyCellExeption ignored) { 		  
			// Cuando se selecciona una celda que no contiene bola
			// No significa necesariamente que sea un defecto

			Log.i(TAG, ignored.getMessage());			  
			
		} catch (NotEmptyCellExeption e) {
			Log.e(TAG, e.getMessage(), e);
			Utils.displayError(activity, e.getMessage());
			
		} catch (AlreadySelectedBallExeption e) { // Cuando se selecciona una bola y se quiere seleccionar 
			Log.e(TAG, e.getMessage(), e);		  // otra sin antes deseleccionar la que esta seleccionada
			Utils.displayError(activity, e.getMessage());
		
		} catch (UnSelectedBallExpetion e) {
			Log.e(TAG, e.getMessage(), e);
			Utils.displayError(activity, e.getMessage());
	 	} catch (Exception e) {
	 		Log.e(TAG, e.getMessage(), e);
			Utils.displayError(activity, e.getMessage());
	 	}
	}
	

	/**
	 * 
	 * @throws UnSelectedBallExpetion
	 */
	private void unselect() throws UnSelectedBallExpetion {
		Coord selected = aGame.getSelected();
		Log.d(TAG, "unselectBall " +  selected);
		ImageView imageView = findByCoord(selected);
		if (GameActivity.imageType.equals(ImageType.BALLS)) {
			AnimationDrawable anim = (AnimationDrawable) imageView.getBackground();
			anim.stop();
		} else { // ImageType.SHAPES or ImageType.STARS 
			imageView.clearAnimation();
		}
		aGame.unselect();
	}
	
	 /**
	 * Selecciona una bola en la pantalla
	 * 
	 * @param coord
	 * @throws AlreadySelectedBallExeption
	 * @throws EmptyCellExeption
	 */
	private void selectBall(ImageView ballView, Coord coord) throws AlreadySelectedBallExeption, EmptyCellExeption {
		Log.d(TAG, "selectBall " + coord);
		aGame.select(coord);
		
		if (GameActivity.imageType.equals(ImageType.BALLS)) {
			AnimationDrawable frameAnimation = (AnimationDrawable) ballView.getBackground();
			frameAnimation.start();
		} else { // ImageType.SHAPES or ImageType.STARS 
			BallColor ballColor = aGame.getBallColor(coord);
			Animation animation = AnimationUtils.loadAnimation(activity, BallColor.getResourceAnimation(ballColor, GameActivity.imageType));
			ballView.startAnimation(animation);
		}
	}
	
	/**
	 * Move a ball in screen
	 * @param endCoord Destino
	 * @throws UnSelectedBallExpetion
	 * @throws EmptyCellExeption
	 * @throws NotEmptyCellExeption
	 */
	private void moveBall(Coord endCoord) throws UnSelectedBallExpetion, EmptyCellExeption, NotEmptyCellExeption {
		Log.d(TAG, "moveBall " + aGame.getSelected() + " to " + endCoord);
		
		AnimationSet set = new AnimationSet(true);
		long duration = Consts.animation.translateDelayMillis;
		
		LinkedList<Coord> path = aGame.moveTo(endCoord);
		if (!path.isEmpty()) {
			int size = path.size();
			
			final BallColor ballColor = aGame.getBallColor(endCoord);
			
			Coord init = path.get(0);
			Coord end = path.get(size-1);
			
			enableSelect = false;
			ImageView ballView = findByCoord(init);
			
			// Stop rotate animation
			if (GameActivity.imageType.equals(ImageType.BALLS)) {
				AnimationDrawable anim = (AnimationDrawable) ballView.getBackground();
				anim.stop();
			} else { // ImageType.SHAPES or ImageType.STARS 
				ballView.clearAnimation();
			}

			ImageView ballTmp = activity.findViewById(R.id.ballTmp);


			for (int i=0; i < size-1; i++) {
				Coord coord1 = path.get(i);
				Coord coord2 = path.get(i+1);
				float fromXValue = 0f;
				float fromYValue = 0f;
				float toXValue = (float) coord2.getY() - coord1.getY();
				float toYValue = (float) coord2.getX() - coord1.getX();
				if (i == 0) {
					fromXValue = (float) coord1.getY() - endCoord.getY();
					fromYValue = (float) coord1.getX() - endCoord.getX();
					toXValue = (float) coord2.getY() - endCoord.getY();
					toYValue = (float) coord2.getX() - endCoord.getX();
				}
				TranslateAnimation ta = new TranslateAnimation(
		    			Animation.RELATIVE_TO_SELF, fromXValue, 
		    			Animation.RELATIVE_TO_SELF, toXValue, 
		    			Animation.RELATIVE_TO_SELF, fromYValue, 
		    			Animation.RELATIVE_TO_SELF, toYValue);
				ta.setDuration(duration);
				ta.setStartOffset(duration*(i));
				ta.setZAdjustment(1);
				ta.setFillBefore(false);
				if (i == size-2) {
					ta.setAnimationListener(new MoveAnimationListener(init, end) {
						@Override
						public void onMoveEnd(Coord init, Coord end) {
							enableSelect = true;
							ballTmp.setVisibility(View.GONE);
							updateGame();
						}
					}); 
				}
				set.addAnimation(ta);
			}

			ImageView fromBallView = findByCoord(init);
			ImageView toBallView = findByCoord(end);
			toBallView.bringToFront();
			int resourceAnimation;
			if (GameActivity.imageType.equals(ImageType.BALLS)) {
				resourceAnimation = BallColor.getResourceAnimation(ballColor, GameActivity.imageType);
			} else { // ImageType.SHAPES or ImageType.STARS
				resourceAnimation = BallColor.getImageResource(ballColor, GameActivity.imageType);
			}

			toBallView.setBackgroundResource(resourceAnimation);
			fromBallView.setBackgroundResource(R.drawable.noball);
		
			ImageView endBallView = findByCoord(endCoord);

			ballTmp.setVisibility(View.VISIBLE);
			ViewGroup.LayoutParams layoutParams = endBallView.getLayoutParams();
			ballTmp.setLayoutParams(layoutParams);
			endBallView.startAnimation(set);

		} else {
			Toast tostada = Toast.makeText(activity, activity.getResources().getString(R.string.fb_main_invalid_move), Toast.LENGTH_SHORT);
			tostada.setGravity(Gravity.BOTTOM, 5, 2);
			tostada.show();
		}
	}

	/**
	 * Actualiza toda la informacion sobre la interfaz. 
	 */
	public void updateGame() {
		try {
			if (aGame.hasBallsInLine()) {
				playAudio(SoundType.BUBBLES);
				removeBalls();
			} else {
				HashMap<Coord, BallColor> newBalls = aGame.addBalls(aGame.getCurrentNextBalls());
				showBalls(newBalls);
				if (aGame.hasBallsInLine()) {
					removeBalls();
				}
			
				// actualizo las bolas siguientes
				nextBalls();
			}
			
			// Si quede sin bolas en el tablero.
			while (aGame.emptyGrid()) {
				HashMap<Coord, BallColor> newBalls = aGame.addBalls(aGame.getCurrentNextBalls());
				showBalls(newBalls);
				if (aGame.hasBallsInLine()) {
					removeBalls();
				}
				nextBalls();
			} 
			
			updateScore();
			
			// Si no hay mas lugares libres, se muestra la puntuacion
			if (aGame.freeCells().size() == 0) {
				activity.showDialog(GameActivity.DIALOG_NEW_SCORE);
			}
			
		} catch (Exception e) {
	 		Log.e(TAG, e.getMessage(), e);
			Utils.displayError(activity, e.getMessage());
	 	}
	}

	/**
	 * Actualizacion del score
	 */
	private void updateScore() {
		// Se actuliza el score
		int score = aGame.getScore();
		activity.updateScore(score);
	}

	/**
	 * Muestra las siguientes bolas a venir
	 */
	private void nextBalls() {
		
		float fromXScale = 0.3f;
		float toXScale = 1.0f;
		float fromYScale = 0.3f;
		float toYScale = 1.0f;
	    float pivotX = 0.5f;
	    float pivotY = 0.5f;
	    Animation animation = new ScaleAnimation(fromXScale, toXScale, fromYScale, toYScale, 
	    		Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
		animation.setDuration(Consts.animation.scaleDelayMillis);
		animation.setFillAfter(false);
		
		// Next balls
		LinkedList<BallColor> nextBalls = aGame.getNextColorBalls();;
		ImageView nextball1 = (ImageView) activity.findViewById(R.id.NextBall1);
		ImageView nextball2 = (ImageView) activity.findViewById(R.id.NextBall2);
		ImageView nextball3 = (ImageView) activity.findViewById(R.id.NextBall3);
		
		nextball1.setImageResource(BallColor.getImageResource(nextBalls.get(0), GameActivity.imageType));
		nextball2.setImageResource(BallColor.getImageResource(nextBalls.get(1), GameActivity.imageType));
		nextball3.setImageResource(BallColor.getImageResource(nextBalls.get(2), GameActivity.imageType));
		nextball1.startAnimation(animation);
		nextball2.startAnimation(animation);
		nextball3.startAnimation(animation);
	}

	/**
	 * Devuelve una accion al seleccionar una bola 
	 * @param selected coordenada seleccionada
	 * @return Retorna una valor de enumerado que representa una accion a realizar
	 */
	private ClickBallAction getClick(Coord selected) {
		
		if (!aGame.hasSelected()) {
			if (aGame.hasBall(selected)) {
				return ClickBallAction.SELECT_BALL;
			} else {
				return ClickBallAction.NOTHING;
			}
 		} else {
 			if (aGame.getSelected().equals(selected)) {
 				return ClickBallAction.UNSELECT_BALL;	
 			} else if (aGame.hasBall(selected)) {
 				return ClickBallAction.SELECT_OTHER_BALL;	
 			} else {
 				playAudio(SoundType.BUBBLE_SHORT);
 				return ClickBallAction.MOVE_BALL;
 			}
 		}
	}
	
	/**
	 * Agrega una bola al tablero con animacion
	 * @param coord
	 * @param ballColor
	 */
	public void addBall(Coord coord, BallColor ballColor) {
		Log.d(TAG, "addBall " + coord);
		
		float fromXScale = 0.3f;
		float toXScale = 1.0f;
		float fromYScale = 0.3f;
		float toYScale = 1.0f;
	    float pivotX = 0.5f;
	    float pivotY = 0.5f;
	    Animation animation = new ScaleAnimation(fromXScale, toXScale, fromYScale, toYScale, 
	    		Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
		animation.setDuration(200);
		animation.setFillAfter(false);
		
		ImageView imageView = findByCoord(coord);
		int resourceAnimation;
		if (GameActivity.imageType.equals(ImageType.BALLS)) {
			resourceAnimation = BallColor.getResourceAnimation(ballColor, GameActivity.imageType);
		} else { // ImageType.SHAPES or ImageType.STARS 
			resourceAnimation = BallColor.getImageResource(ballColor, GameActivity.imageType);
		}
		imageView.setBackgroundResource(resourceAnimation); 
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.startAnimation(animation);
	}
	
	/**
	 * Devuelve la coordenada que se encuentra la celda del frame.
	 * 
	 * @return
	 */
	private Coord getCoord(ImageView ballImage) {
		String resourceName = activity.getResources().getResourceName(ballImage.getId());
		// resource name must be start with prefix 'ball'
		String prefix = "com.pluk.fiveballs:id/ball";
		if (resourceName.startsWith(prefix)) {
			char cx = resourceName.charAt(prefix.length());
			int x = Integer.parseInt(String.valueOf(cx));
			char cy = resourceName.charAt(prefix.length()+1);
			int y = Integer.parseInt(String.valueOf(cy));
			return new Coord(x, y);
		} else {
			throw new Resources.NotFoundException(resourceName);
		}
	}
	
	/**
	 * Find cell ImageView by coordinate
	 * 	
	 * @param i
	 * @param j
	 * @return
	 */
	public ImageView findByCoord(int i, int j) {
		int id = activity.getResources().getIdentifier("ball"+i+j, "id" , "com.pluk.fiveballs");
		return (ImageView) activity.findViewById(id);
	}
	
	/**
	 * Find cell ImageView by coordinate
	 * 
	 * @param coord
	 * @return
	 */
	public ImageView findByCoord(Coord coord) {
		return findByCoord(coord.getX(), coord.getY());
	}

	/**
	 * Borra las bolas del tablero que se encuentran en linea.
	 */
	private void removeBalls() throws EmptyCellExeption {
		// Se toman las coordenadas bolas que hay que borrar
		final LinkedList<Coord> coords = aGame.getCoordsInLine();
		aGame.removeBalls();
//		
		// Se deshabilita la seleccion de bolas hasta que termine la animacion de borrar.
		enableSelect = false;
		
		
		final float scaleMin = 1.0f;
		final float scaleMax = 0f;
		final float pivot = 0.5f;
		final int pivotType = Animation.RELATIVE_TO_SELF;
		
		int size = coords.size();
		for (int i = 0; i < size; i++) {
			Coord coord = coords.get(i);
			ImageView imageView = findByCoord(coord);
			Animation removeAnimation = new ScaleAnimation(scaleMin, scaleMax, scaleMin, scaleMax,  pivotType, pivot, pivotType, pivot);
			removeAnimation.setDuration(Consts.animation.removeBallsMillis);
			removeAnimation.setStartOffset(Consts.animation.removeBallsMillis*i);
			removeAnimation.setFillAfter(false);
			boolean last = i == size -1;
			removeAnimation.setAnimationListener(new RemoveBallAnimationListener(imageView, last) {
				@Override
				public void onRemoveEnd(ImageView imageView, boolean enabled) {
					imageView.setBackgroundResource(R.drawable.noball);
					if (enabled) {
						enableSelect = true;
					}
				}
			});
			imageView.startAnimation(removeAnimation);
		}
		
		
	
	}
	
    /**
     * Add new Balls into screen 
     * @param newBalls
     */
    private void showBalls(HashMap<Coord, BallColor> newBalls) {
		for (Coord coord : newBalls.keySet()) {
			BallColor ballColor = newBalls.get(coord);
			addBall(coord, ballColor);	
		}
	}
	
    private void playAudio(SoundType soundType){
    	GameActivity fm = (GameActivity) activity;
    	if (fm.isAudioEnabled()){
    		SoundUtils.play(activity, audioPlayer, soundType);
    	}
    }
	
}