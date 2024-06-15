package com.pluk.fiveballs.model;

public final class Consts {
	 
	 public static final class game {
		 public static final int gridsize = 9;
		 public static final int inlinemin= 5;
		 public static final int countNextBalls = 3;
		 public static final int countBallsColors= 7;
		 //La cantidad de pixels necesarios para los bordes (10 bordes de 1px) + un offset (5px)
		 public static final int pixelsBordes = 15;
		 //La cantidad de puntajes máximos que se almacenan
		 public static final int TOP_SCORES_MAX = 10;
	}
	 
	 public static final class preferences {
		 public static final String SOUND_ENABLED_PREFERENCE_KEY	= "sound";
	 }
	 
	 
	 public static final class ranking {
		 public static final int size = 10;
	 }
	 
	 public static final class animation {
		 public static final long scaleDelayMillis 		= 200;
		 public static final long translateDelayMillis 	= 25;
		 public static final long removeBallsMillis 	= 100;
	 }
}
