package helmosdeep.domains;

/**
 * Sert à stocker les quatre types d'unité qui existent
 * <ul>
 * <li>Connaît sa force</li>
 * <li>Connaît sa capacité de mouvement</li>
 * <li>Possède une description qui détaille le comportement spécifique de ce
 * type d'unité</li>
 * </ul>
 */
public enum EUnitType {

	LEGERE(1, 4, "Peut se déplacer d'un case après une attaque."), MOYENNE(2, 3, null),
	LOURDE(3, 2, "n'est pas affecté par le terrain."), GENERAL(4, 3, "N'attaque pas, se défend seulement");

	private int force;
	private int mvt;
	private String descr;

	private EUnitType(int force, int mvt, String descr) {
		this.force = force;
		this.mvt = mvt;
		this.descr = descr;
	}

	public int getMvt() {
		return this.mvt;
	}

	public int getForce() {
		return this.force;
	}

	public String getDescr() {
		return this.descr;
	}

	/**
	 * 
	 * @return retourne le nom de la valeur d'énum passée en préfixe sous forme de
	 *         chaîne de caractères
	 */
	public String getUnitTypeName() {
		return this.name();
	}

}
