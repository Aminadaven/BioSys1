package bioSys;
class Jaguar extends Animal {
	public Jaguar(int x, int y) {
		super(x, y);
		this.eats = new String[1];
		eats[0] = ("Sheep");
		minWeight = 65;
		maxAge = 3650;
		setMass(minWeight + 5);
	}
}