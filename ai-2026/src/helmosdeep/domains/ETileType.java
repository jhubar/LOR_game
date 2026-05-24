package helmosdeep.domains;

import java.awt.Color;

/**
 * Sert à stocker les trois types de terrain / tuile qui existent.
 * <ul>
 * <li>Possède un nom</li>
 * <li>Connaît son coût de mouvement</li>
 * <li>Connaît sa couleur</li>
 * </ul>
 */
public enum ETileType {

	LAND("land", 1, Color.yellow), FOREST("forest", 2, Color.green), MOUNTAIN("mouantain", 3, Color.gray);

	private String nom;
	private int coutMvt;
	private Color couleur;

	private ETileType(String nom, int coutMvt, Color couleur) {
		this.nom = nom;
		this.coutMvt = coutMvt;
		this.couleur = couleur;
	}

	public int getCoutMvt() {
		return this.coutMvt;
	}

	public String getNom() {
		return this.nom;
	}

	public Color getCouleur() {
		return this.couleur;
	}

	/** Libellé affiché dans le panneau Situation (ex. {@code PLAINE [1]}). */
	public String getDisplayLabel() {
		return switch (this) {
		case LAND -> "PLAINE [%d]".formatted(this.coutMvt);
		case FOREST -> "FORÊT [%d]".formatted(this.coutMvt);
		case MOUNTAIN -> "MONTAGNE [%d]".formatted(this.coutMvt);
		};
	}

	public static ETileType getTileTypeWithLetter(char c) throws IllegalArgumentException {
		switch (c) {
		case 'L':
			return ETileType.LAND;
		case 'F':
			return ETileType.FOREST;
		case 'M':
			return ETileType.MOUNTAIN;
		default:
			throw new IllegalArgumentException();
		}

	}

}
