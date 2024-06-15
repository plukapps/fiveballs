package com.pluk.fiveballs.handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

	/**
	 * @param args
	 * @throws FiveMoreExeption 
	 */
	public static void main(String[] args) {
		
//		IGameManager game = FactoryGame.getIGameManager();
//		
//		LinkedList<BallColor> initBalls = game.getNextColorBalls();
//		game.addBalls(initBalls);
//		
//		LinkedList<BallColor> nextBalls = game.getNextColorBalls();
//		
//		while (true) {
//			try {
//				String grid = game.getPanelGrid();
//				System.out.println(grid);
//				
//				System.out.print("Command: ");
//				
//				String string = leerString();
//				String[] split = string.split(" ");
//				
//				String comand = split[0];
//				int i = Integer.parseInt(split[1]);
//				int j = Integer.parseInt(split[2]);
//				
//				if (i<0 || i>8) {
//					System.out.println("Las coordenadas deben ser entre 0 y 8");
//					continue;
//				}
//				if (j<0 || j>8) {
//					System.out.println("Las coordenadas deben ser entre 0 y 8");
//					continue;
//				}
//				
//				if (comand.equals("s")) {
//					Coord coord = new Coord(i,j);
//					game.select(coord);
//				} else if (comand.equals("m")) {
//					Coord coord = new Coord(i,j);
//					game.moveTo(coord);
//					if (game.hasBallsInLine()) {
//						game.removeBalls();
//					} else {
//						game.addBalls(nextBalls);
//						nextBalls = game.getNextColorBalls();
//					}
//				} else if (comand.equals("u")){
//					game.unselect();
//				} else if (comand.equals("q")){
//					break;
//				} else {
//					System.out.println("Comando Incorrecto"); 
//				}
//
//			} catch (FiveMoreExeption e) {
//				System.out.println(e.getMessage());
//				continue;
//			}
//		}
//		
		
	}
	
	public static int leerInt(){
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		while (true){
			try {
				String frase = br.readLine();
				return  Integer.parseInt(frase);
			}			
			catch (Exception e) {
				System.out.println("Error : Datos ingresados incorrectamente");
				System.out.println("Ingresar Nuevamente :");
			}
		}	
	}

	public static String leerString(){
		InputStreamReader isr = new InputStreamReader(System.in);
		BufferedReader br = new BufferedReader(isr);
		while (true){
			try {
				String frase = br.readLine();
				return frase;
			}
			catch (Exception e) {
				System.out.println("Error : Datos ingresados incorrectamente");
				System.out.println("Ingresar Nuevamente :");
			}
		}
	}


}
