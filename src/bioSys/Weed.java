package bioSys;
public class Weed extends Animal {
	double q, m;
	int generateWeight, maxWeight;
	public Weed(int x, int y, int weight, double q) {
		super(x, y);
		this.eats = new String[0];
		minWeight = 0;
		maxAge = 30000; // should be around 30000
		this.setMass(weight);
		this.q = q;
		m = 1;
		generateWeight = 10000;
	}
	public Weed(int x, int y) {
		super(x, y);
		this.eats = new String[0];
		minWeight = 0;
		maxAge = 30000; // should be around 30000
		setMass(320);
		q = 1.02;//2.5% growth per day
		m = 1;
		generateWeight = 10000;
	}
	public void act() {
		if(getMass() >= generateWeight) {
			if(generate()) 
				m = 1;
			else 
				m = 0.5;
		}
		else if(getMass() >= 3 * generateWeight) {
			if(generate()) 
				m = 0.5;
			else 
				m = 1 / q;
		}
		else 
			m = 1;
		eat();
		setMass(getMass() + 1);
		passDay();
	}
	private void eat() {
		setMass(getMass() * q * m);
	}
	private boolean generate() {
		FLocation[] map = this.map();
		for(int i = 0; i < map.length; i++) {
			if(map[i].flag == 2) 
				if(isNeighbors(map[i], this.getX(), this.getY())) {
					Weed child = new Weed(map[i].x, map[i].y);
					child.q = q;
					child.setMass(getMass() / 2);
					world.putIn(child);
					Main.animals.add(child);
					setMass(getMass() / 2);
					setMass(getMass() - 1);
					return true;
				}
		}
		return false;
	}
}