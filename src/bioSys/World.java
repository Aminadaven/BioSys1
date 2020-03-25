package bioSys;
public class World {
	Animal[][] animal;
	public World(int x, int y) {
		animal = new Animal[x][y];
		}
	void initiate() {
		for(int i = 0; i < animal.length; i++) {
			for(int j = 0; j < animal[i].length; j++) {
				animal[i][j] = new Animal(i, j);
			}
		}
	}
	void putIn(Animal obj) {
		animal[obj.getX()][obj.getY()] = obj;
	}
	void printWorld() {
		Animal current;
		String str;
		for(int i = 0; i < animal.length; i++) {
			for(int j = 0; j < animal[i].length; j++) {
				current = animal[j][i];
				str = current.getClass().getSimpleName() + "(" + current.getMass() + ", " + current.age + ")|";
				Utility.print(str);
			}
			Utility.print("\n");
			for(int z = 0; z < animal.length * 40; z++) 
				Utility.print("-");
			Utility.print("\n");
		}
		Utility.print("\n");
	}
}