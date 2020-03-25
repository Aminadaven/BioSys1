package bioSys;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Animal {
	//racial data of the animal
	String[] eats;
	double minWeight;
	int maxAge;
	
	//data about the animal's location
	private int x;
	private int y;
	World world = Main.world;
	int id;
	
	//personal data of the animal
	private double mass;
	int age;
	boolean male;
	boolean waiting;
	public Animal(int x, int y) {
		if(Main.id != 0) Main.updateID();
		////World.print("Constructor().DeBug - Main.id: " + Main.id + "\n");
		this.id = Main.id;
		if(!this.getClass().getSimpleName().equals("Animal")) 
			Main.id++;
		this.setX(x);
		this.setY(y);
		age = 0;
		waiting = false;
		if(Math.random() > 0.6) male = true;
		else male = false;
	}
	public Animal() {
		if(Main.id != 0) Main.updateID();
		////World.print("Constructor2().DeBug - Main.id: " + Main.id + "\n");
		this.id = Main.id;
		if(!this.getClass().getSimpleName().equals("Animal")) 
			Main.id++;
		age = 0;
		waiting = false;
		if(Math.random() > 0.6) male = true;
		else male = false;
	}
	public double getMass() {
		return mass;
	}
	public void setMass(double mass) {
		this.mass = mass;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public void act() {
		if(!waiting) {
			Location[] locations;
			//running away from predators - trying to run as far as possible, also if possible tries to eat in the run
			if(trim(searchPredator()).length > 0) 
				runAway();
			//generation
			else if(!male && getMass() >= (2 * (minWeight + 5) + 1) && trim(searchMale()).length > 0) {
				////World.print("\nGeneration!\n");
				Animal male = world.animal[trim(searchMale())[0].x][trim(searchMale())[0].y];
				male.waiting = true;
				//waiting = true;
				generateWith(male);
			}
			//eating the best meal possible
			else if(trim(searchFood()).length > 0) {
				////World.print("\nact().DeBug - searching for food \n");
				locations = trim(searchFood());
				double max = world.animal[locations[0].x][locations[0].y].getMass();
				Animal maxAnimal = world.animal[locations[0].x][locations[0].y];
				for(int i = 0; i < locations.length; i++) {
					if(world.animal[locations[i].x][locations[i].y].getMass() > max) {
						max = world.animal[locations[i].x][locations[i].y].getMass();
						maxAnimal = world.animal[locations[i].x][locations[i].y];
					}
				}
				eat(maxAnimal);
			}
			else {
				/** standing in place in order to save energy, needs checking 
				 * 2 options:
				 * 1. waiting a number of days then move on so the animal wont starve in the place - 
				 * sounds like the most logical to me. however, i should test and find what is the best value 
				 * 2. moving anyway - 
				 * also very logical because if one is lost in the desert he will to get out of it quickly
				// */
				CLocation[] empty = new CLocation[8];
				int j = 0;
				for(int i = 0; i < empty.length; i++) 
					if(map()[i].flag == 2) {
						////World.print("\nact().DeBug - map()[i].i: " + i + "\n");
						////World.print("\nact().DeBug - empty[j].j: " + j + "\n");
						empty[j] = new CLocation(map()[i].x, map()[i].y);
						empty[j].flag = map()[i].flag;
						j++;
					}
				empty = trim(empty);
				int cx = world.animal.length / 2;//center x or opposite
				int cy = world.animal[0].length / 2;//center y or opposite
				for(int i = 0; i < empty.length; i++) 
					empty[i].distance = distance(empty[i].x, cx, empty[i].y, cy);
				if(empty.length > 0) {
					CLocation cell = empty[0];
					for(int i = 1; i < empty.length; i++) 
						if(empty[i].distance < cell.distance) 
							cell = empty[i];
					moveTo(cell.x, cell.y);
				}
			}
		}
		else {
			//Utility.print(getClass().getSimpleName() + " have just waited a day\n");
			setMass(getMass() - 1);
			waiting = false;
		}
		passDay();
	}
	static double distance(double x1, double x2, double y1, double y2) {
		return Math.cbrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	public void die() {
		world.animal[getX()][getY()] = new Animal(getX(), getY());
		setMass(0);
		Main.updateID();
		////World.print("die().DeBug - id: " + id + "\n");
		Main.animals.remove(id);
		try {
			this.finalize();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	boolean isNeighbors(Location cell, int x, int y) {
		return ((Math.abs(cell.x - x) == 1) && (Math.abs(cell.y - y) == 1 || cell.y - y == 0)
				|| (Math.abs(cell.y - y) == 1) && (Math.abs(cell.x - x) == 1 || cell.x - x == 0));
	}
	private void generateWith(Animal male) {
		@SuppressWarnings("rawtypes")
		Constructor[] con = getClass().getConstructors();
		FLocation[] map = map();
		for(int i = 0; i < map.length; i++) {
			if(map[i].flag == 2) 
				if(isNeighbors(map[i], male.getX(), male.getY())) {
					try {
						setMass(getMass() - (minWeight + 5));
						setMass(getMass() - 1);
						Animal child = (Animal) con[0].newInstance(map[i].x, map[i].y);
						world.putIn(child);
						Main.animals.add(child);
						break;
					} catch (InstantiationException | IllegalAccessException
							| IllegalArgumentException
							| InvocationTargetException e) {
						e.printStackTrace();
					}
				}
		}
	}
 	private void moveTo(int x, int y) {
		Animal empty = new Animal(this.getX(), this.getY());
		world.putIn(empty);
		this.setX(x);
		this.setY(y);
		world.putIn(this);
		this.setMass(0.99 * this.getMass());
	}
	void passDay() {
		setMass(getMass() - 1);
		age++;
		if(death()) 
			this.die();
	}
	private boolean death() {
		return (getMass() < minWeight || age > maxAge);
	}
	//provides map of the area. enemy = 3, empty = 2, food = 1, else = 0
	FLocation[] map() {
		FLocation[] locations = new FLocation[8];
		//x - 1
		locations[0] = new FLocation(getX() - 1, getY() - 1);
		if(predator(getX() - 1, getY() - 1))
			locations[0].flag = 3;
		else if(empty(getX() - 1, getY() - 1)) 
			locations[0].flag = 2;
		else if(food(getX() - 1, getY() - 1))
			locations[0].flag = 1;
		else
			locations[0].flag = 0;
		locations[1] = new FLocation(getX() - 1, getY());
		if(predator(getX() - 1, getY()))
			locations[1].flag = 3;
		else if(empty(getX() - 1, getY())) 
			locations[1].flag = 2;
		else if(food(getX() - 1, getY()))
			locations[1].flag = 1;
		else
			locations[1].flag = 0;
		locations[2] = new FLocation(getX() - 1, getY() + 1);
		if(predator(getX() - 1, getY() + 1))
			locations[2].flag = 3;
		else if(empty(getX() - 1, getY() + 1)) 
			locations[2].flag = 2;
		else if(food(getX() - 1, getY() + 1))
			locations[2].flag = 1;
		else
			locations[2].flag = 0;
		//x
		locations[3] = new FLocation(getX(), getY() - 1);
		if(predator(getX(), getY() - 1))
			locations[3].flag = 3;
		else if(empty(getX(), getY() - 1)) 
			locations[3].flag = 2;
		else if(food(getX(), getY() - 1))
			locations[3].flag = 1;
		else
			locations[3].flag = 0;
		locations[4] = new FLocation(getX(), getY() + 1);
		if(predator(getX(), getY() + 1))
			locations[4].flag = 3;
		else if(empty(getX(), getY() + 1)) 
			locations[4].flag = 2;
		else if(food(getX(), getY() + 1))
			locations[4].flag = 1;
		else
			locations[4].flag = 0;
		//x + 1
		locations[5] = new FLocation(getX() + 1, getY() - 1);
		if(predator(getX() + 1, getY() - 1))
			locations[5].flag = 3;
		else if(empty(getX() + 1, getY() - 1)) 
			locations[5].flag = 2;
		else if(food(getX() + 1, getY() - 1))
			locations[5].flag = 1;
		else
			locations[5].flag = 0;
		locations[6] = new FLocation(getX() + 1, getY());
		if(predator(getX() + 1, getY()))
			locations[6].flag = 3;
		else if(empty(getX() + 1, getY())) 
			locations[6].flag = 2;
		else if(food(getX() + 1, getY()))
			locations[6].flag = 1;
		else
			locations[6].flag = 0;
		locations[7] = new FLocation(getX() + 1, getY() + 1);
		if(predator(getX() + 1, getY() + 1))
			locations[7].flag = 3;
		else if(empty(getX() + 1, getY() + 1)) 
			locations[7].flag = 2;
		else if(food(getX() + 1, getY() + 1))
			locations[7].flag = 1;
		else
			locations[7].flag = 0;
		return locations;
	}
	private Location[] searchPredator() {
		Location[] locations = new Location[8];
		//x - 1
		if(predator(getX() - 1, getY() - 1))
			locations[0] = new Location(getX() - 1, getY() - 1);
		if(predator(getX() - 1, getY()))
			locations[1] = new Location(getX() - 1, getY());
		if(predator(getX() - 1, getY() + 1))
			locations[2] = new Location(getX() - 1, getY() + 1);
		//x
		if(predator(getX(), getY() - 1))
			locations[3] = new Location(getX(), getY() - 1);
		if(predator(getX(), getY() + 1))
			locations[4] = new Location(getX(), getY() + 1);
		//x + 1
		if(predator(getX() + 1, getY() - 1))
			locations[5] = new Location(getX() + 1, getY() - 1);
		if(predator(getX() + 1, getY()))
			locations[6] = new Location(getX() + 1, getY());
		if(predator(getX() + 1, getY() + 1))
			locations[7] = new Location(getX() + 1, getY() + 1);
		return locations;
	}
	private void eat(Animal animal) {
		setMass(0.99 * getMass());
		if(animal.getClass().getSimpleName().equals("Weed")) {
			//the animal shall get 95% of the food that went from the weed or * 0.95
			/*
			if(getMass() * 0.45 < 20) {
				setMass(getMass() + 14.25);
				animal.setMass(animal.getMass() - 15);
			}
			else if(getMass() * 0.45 > 60) {
				setMass(getMass() + 57);
				animal.setMass(animal.getMass() - 60);
			}
			else {
				setMass(getMass() + (getMass() * 0.4275));
				animal.setMass(animal.getMass() - (getMass() * 0.45));
			}
			*/
			setMass(getMass() + 14.25);
			animal.setMass(animal.getMass() - 15);
			if(animal.getMass() <= 0) {
				int newx = animal.getX();
				int newy = animal.getY();
				animal.die();
				moveTo(newx, newy);
			}
			//Utility.print(this.getClass().getSimpleName() + " have just ate some weed\n");
		}
		else {
			//Utility.print(this.getClass().getSimpleName() + " have just ate " + animal.getClass().getSimpleName() + "\n");
			setMass(getMass() + (0.9 * animal.getMass()));
			int newx = animal.getX();
			int newy = animal.getY();
			animal.die();
			moveTo(newx, newy);
			waiting = true;
		}
	}
	private Location[] searchFood() {
		Location[] locations = new Location[8];
		//x - 1
		if(food(getX() - 1, getY() - 1))
			locations[0] = new Location(getX() - 1, getY() - 1);
		if(food(getX() - 1, getY()))
			locations[1] = new Location(getX() - 1, getY());
		if(food(getX() - 1, getY() + 1))
			locations[2] = new Location(getX() - 1, getY() + 1);
		//x
		if(food(getX(), getY() - 1))
			locations[3] = new Location(getX(), getY() - 1);
		if(food(getX(), getY() + 1))
			locations[4] = new Location(getX(), getY() + 1);
		//x + 1
		if(food(getX() + 1, getY() - 1))
			locations[5] = new Location(getX() + 1, getY() - 1);
		if(food(getX() + 1, getY()))
			locations[6] = new Location(getX() + 1, getY());
		if(food(getX() + 1, getY() + 1))
			locations[7] = new Location(getX() + 1, getY() + 1);
		return locations;
	}
	private boolean food(int x, int y) {
		if(x >= world.animal.length || y >= world.animal[0].length || x < 0 || y < 0)
			return false;
		for(int i = 0; i < eats.length; i++) {
			if(world.animal[x][y].getClass().getSimpleName().equals(eats[i])) 
				return true;
		}
		return false;
	}
	private void runAway() {
		//variables
		CLocation[] cpm;
		FLocation[] map = map();
		PLocation[] cell = new PLocation[8];
		for(int i = 0; i < cell.length; i++) {
			cell[i] = new PLocation(0);
		}
		Location[] predators = trim(searchPredator());
		//checking minimal distance
		int size = 0;
		for(int i = 0; i < map.length; i++) {
			if(map[i].flag == 2 || map[i].flag == 1) {
				cell[i].distance = 3;
				for(int j = 0; j < predators.length; j++) {
					////World.print("predator: " + predators[j].x + ", " + predators[j].y + "\n");
					cell[i].distance = distance(map[i], predators[j]) < cell[i].distance ? 
							distance(map[i], predators[j]) : cell[i].distance;
				}
				////World.print("minDistance: " + cell[i].distance + "\n");
				if(cell[i].distance == 2) 
					size++;
			}
		}
		cpm = new CLocation[size];
		size = 0;
		for(int i = 0; i < map.length; i++) {
			if((map[i].flag == 2 || map[i].flag == 1) && (cell[i].distance == 2)) {
				cpm[size] = new CLocation(map[i].x, map[i].y);
				cpm[size].flag = map[i].flag;
				cpm[size].distance = cell[i].distance;
				size++;
			}
		}
		//checking if the animal can eat in the runAway
		Animal max = new Animal(0, 0);
		max.setMass(-1);
		for(int i = 0; i < cpm.length; i++) {
			if(cpm[i].flag == 1 && world.animal[cpm[i].x][cpm[i].y].getMass() > max.getMass()) {
				max = world.animal[cpm[i].x][cpm[i].y];
			}
		}
		if((max.getMass() != -1) && (!max.getClass().getSimpleName().equals("Weed"))) {
			////World.print("\nEating Away!\n");
			////World.print(max.getClass().getSimpleName() + "\n");
			////World.print(max.x + ", " + max.y + "\n");
			eat(max);
		}
		else if(cpm.length > 0) {
			//World.print("\nRunning Away!\n");
			moveTo(cpm[0].x, cpm[0].y);
		}
		else {
			//World.print("\nShitting Away!\n");
		}
	}
	private int distance(Location loc1, Location loc2) {
		int x1, x2, y1, y2;
		x1 = loc1.x;
		x2 = loc2.x;
		y1 = loc1.y;
		y2 = loc2.y;
		if(Math.abs(x1 - x2) == 2 || Math.abs(y1 - y2) == 2) {
			return 2;
		}
		return 1;
	}
	private boolean empty(Object obj) {
		return obj.getClass() == (null) || obj.getClass().getSimpleName().equals("Animal");
	}
	private boolean empty(int x, int y) {
		if(x >= world.animal.length || y >= world.animal[0].length || x < 0 || y < 0)
			return false;
		return (empty(world.animal[x][y]));
	}
	private boolean predator(int x, int y) {
		if(x >= world.animal.length || y >= world.animal[0].length || x < 0 || y < 0)
			return false;
		if(world.animal[x][y].eats == null) 
			return false;
		for(int i = 0; i < world.animal[x][y].eats.length; i++) {
			if(getClass().getSimpleName().equals(world.animal[x][y].eats[i])) 
				return true;
		}
		return false;
	}
	private Location[] searchMale() {
		Location[] locations = new Location[8];
		//x - 1
		if(male(getX() - 1, getY() - 1))
			locations[0] = new Location(getX() - 1, getY() - 1);
		if(male(getX() - 1, getY()))
			locations[1] = new Location(getX() - 1, getY());
		if(male(getX() - 1, getY() + 1))
			locations[2] = new Location(getX() - 1, getY() + 1);
		//x
		if(male(getX(), getY() - 1))
			locations[3] = new Location(getX(), getY() - 1);
		if(male(getX(), getY() + 1))
			locations[4] = new Location(getX(), getY() + 1);
		//x + 1
		if(male(getX() + 1, getY() - 1))
			locations[5] = new Location(getX() + 1, getY() - 1);
		if(male(getX() + 1, getY()))
			locations[6] = new Location(getX() + 1, getY());
		if(male(getX() + 1, getY() + 1))
			locations[7] = new Location(getX() + 1, getY() + 1);
		return locations;
	}
	private boolean male(int x, int y) {
		if(x >= world.animal.length || y >= world.animal[0].length || x < 0 || y < 0)
			return false;
		return (world.animal[x][y].getClass().equals(this.getClass()) && world.animal[x][y].male);
	}
	private Location[] trim(Location[] arr) {
		Location[] newArr;
		int counter = 0;
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] != null)
				counter++;
		}
		newArr = new Location[counter];
		counter = 0;
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] != null) {
				newArr[counter] = arr[i];
				counter++;
			}
		}
		return newArr;
	}	
	private CLocation[] trim(CLocation[] arr) {
		CLocation[] newArr;
		int counter = 0;
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] != null)
				counter++;
		}
		newArr = new CLocation[counter];
		counter = 0;
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] != null) {
				newArr[counter] = arr[i];
				counter++;
			}
		}
		return newArr;
	}
}