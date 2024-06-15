package com.pluk.fiveballs.model;

import com.pluk.fiveballs.R;


/**
 * Representa los diferentes colores que pueden tener las bolas
 * 
 * @author santilod
 *
 */
public enum BallColor {

	RED, YELLOW, BLUE, ORANGE, CELESTE, GREEN, VIOLET;
	
	/**
	 * Se utiliza para el debug, 
	 * @param ballColor color de una bola
	 * @return Devuelve la inicial del nombre del color de la bola que se 
	 * recibe por parametro
	 */
	public static char getBallColorChar(BallColor ballColor) {
		switch (ballColor) {
			case RED: return 'R';
			case BLUE: return 'B';
			case CELESTE: return 'C';
			case GREEN: return 'G';
			case ORANGE: return 'O';
			case VIOLET: return 'V';
			case YELLOW: return 'Y';
			default: return 'X';
		}
	}
	
	public static BallColor getBallColor(int color) {
		switch (color) {
			case 0: return BallColor.RED;
			case 1: return BallColor.YELLOW;  
			case 2: return BallColor.BLUE;
			case 3: return BallColor.ORANGE;
			case 4: return BallColor.CELESTE;
			case 5: return BallColor.GREEN;
			case 6: return BallColor.VIOLET;
			default: 
				throw new IllegalArgumentException("Invalid ballColor index");
		}
	}
	
	public static int getImageResource(BallColor ballColor, ImageType imageType) {
		if (imageType == ImageType.BALLS) {
			switch (ballColor) {
				case BLUE: return R.drawable.blue1;
				case CELESTE: return R.drawable.celeste1; 
				case GREEN: return R.drawable.green1;
				case ORANGE: return R.drawable.orange1;
				case RED: return R.drawable.red1;
				case VIOLET: return R.drawable.violet1;
				case YELLOW: return R.drawable.yellow1;
			}	
		} else if (imageType == ImageType.STARS) {
			switch (ballColor) {
				case BLUE: return R.drawable.star_blue;
				case CELESTE: return R.drawable.star_celeste;
				case GREEN: return R.drawable.star_green;
				case ORANGE: return R.drawable.star_orange;
				case RED: return R.drawable.star_red;
				case VIOLET: return R.drawable.star_violet;
				case YELLOW: return R.drawable.star_yellow;
			}
		} else {
			switch (ballColor) {
				case BLUE: return R.drawable.shapes_blue;
				case CELESTE: return R.drawable.shapes_celeste;
				case GREEN: return R.drawable.shapes_green;
				case ORANGE: return R.drawable.shapes_orange;
				case RED: return R.drawable.shapes_red;
				case VIOLET: return R.drawable.shapes_violet;
				case YELLOW: return R.drawable.shapes_yellow;
			}
		}
		return -1;
	}
	
	public static int getResourceAnimation(BallColor ballColor, ImageType imageType) {
		if (imageType == ImageType.BALLS) {
			switch (ballColor) {
				case BLUE: 
					return R.anim.blue_animation;
				case CELESTE: 
					return R.anim.celeste_animation; 
				case GREEN: 
					return R.anim.green_animation; 
				case ORANGE: 
					return R.anim.orange_animation; 
				case RED: 
					return R.anim.red_animation; 
				case VIOLET: 
					return R.anim.violet_animation; 
				case YELLOW:
				default:
					return R.anim.yellow_animation; 
			}
		} else {
			return R.anim.rotate_animation;
		}
	}
}
