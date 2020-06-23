package search_library.sample_problems;

/**
 * This class represents a enum for the Romania problem from Russel book.<br>
 * All the cities of the problem is setted here.
 * 
 * @author Alison Carrera
 *
 */

public enum RomaniaCities {

	// Cities of the problem.
	ARAD(0), BUCARESTE(12), CRAIOVA(7), DOBRETA(6), EFORIE(16), FAGARAS(10), GIURGIU(13), HIRSOVA(15), IASI(18), LUGOJ(
			4), MEHADIA(5), NEAMT(19), PITESTI(11), RIMNICU_VILCEA(9), SIBIU(8), TIMISOARA(3), URZICENI(14), ORADEA(2), VASLUI(
			17), ZERIND(1);

	public int identifier = 0;

	RomaniaCities(int id) {
		this.identifier = id;
	}

	int getnodeValue() {
		return this.identifier;
	}

	/**
	 * This method converts int to enum type.
	 * 
	 * @param id The integer identifier of the city.
	 * @return The Enum object of the city.
	 */
	static RomaniaCities convert(int id) {
		for (RomaniaCities c : RomaniaCities.values()) {
			if (c.identifier == id) {
				return c;
			}
		}
		return null;
	}
}
