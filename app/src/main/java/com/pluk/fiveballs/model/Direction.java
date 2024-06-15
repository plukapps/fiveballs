package com.pluk.fiveballs.model;

public enum Direction {
	SOUTH, NORTH, EAST, WEST, SOUTHEAST, SOUTHWEST, NORTHWEST, NORTHESAT;
	
	/**
	 * Busca la direccion contraria a la direccion <code>dir</code>.
	 * @param dir
	 * @return
	 */
	public static Direction getOposite(Direction dir) {
		switch (dir) {
			case SOUTH: return Direction.NORTH;
			case NORTH: return Direction.SOUTH;
			case EAST: return Direction.WEST;
			case WEST: return Direction.EAST;
			case SOUTHEAST: return Direction.NORTHWEST;
			case SOUTHWEST: return Direction.NORTHESAT;
			case NORTHESAT: return Direction.SOUTHWEST;
			default : return Direction.SOUTHEAST;
		}
	}
}
