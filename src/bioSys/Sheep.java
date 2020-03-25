package bioSys;
class Sheep extends Animal {
	public Sheep(int x, int y) {
		super(x, y);
		this.eats = new String[1];
		eats[0] = ("Weed");
		minWeight = 1;
		maxAge = 3650;
		setMass(minWeight + 5);
	}
}