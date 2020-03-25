package bioSys;
//those are for locationing
class Location {
	int x, y;
	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}
}
class PLocation {
	int distance;
	public PLocation(int distance) {
		this.distance = distance;
	}
}
class FLocation extends Location {
	char flag;
	public FLocation(int x, int y) {
		super(x, y);
	}
}

class CLocation extends FLocation {
	double distance;
	public CLocation(int x, int y) {
		super(x, y);
	}
}
class Utility {
	static void print(String str) {
		System.out.print(str);
	}
}