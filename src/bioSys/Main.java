package bioSys;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Vector;

public class Main {
	static World world;
	static LinkedList<Animal> animals;
	static int id, worldx, worldy, weight;
	static double q;
	public static void main(String[] args) {
		int gens, genDisplay;
		//World.print("bioSys.Main.main().DeBug\n");
		animals = new LinkedList<Animal>();
		/*
		if(args.length >= 4) {
			if(args[0].equals("-load")) {
				weight = Integer.valueOf(args[2]);
				q = Double.valueOf(args[3]);
				loadFile(args[1]);
				printWorld();
			}
			else {//can be 5 if needed
				worldx = Integer.valueOf(args[0]);
				worldy = Integer.valueOf(args[1]);
				weight = Integer.valueOf(args[2]);
				q = Double.valueOf(args[3]);
				//String sysName = args[4];
				world = new World(worldx, worldy);
				populate();
				prepareWorld();
				//printWorld();
				return 0;
			}
		}
		*/
		if(args.length >= 4) {
			worldx = Integer.valueOf(args[0]);
			worldy = Integer.valueOf(args[1]);
			gens = Integer.valueOf(args[2]);
			genDisplay = Integer.valueOf(args[3]);
			world = new World(worldx, worldy);
			populate();
			prepareWorld();
			Utility.print("BioSys presents - the 1st generation of the world:\n");
			//printWorld();
		}
		else {
			System.err.println("Error - arguments list don't match. Please type again.");
			return;
		}
		id = 0;
		Utility.print("Number of Jaguars: " + jaguarNumber() + "\n");
		Utility.print("Number of Sheep: " + sheepNumber() + "\n");
		Utility.print("Number of Weed: " + weedNumber() + "\n");
		Utility.print("Number of Empty: " + emptyNumber() + "\n");
		for(int gen = 2; gen <= gens; gen++) {
			if(!alive()) {
				Utility.print("The world have died before he reached the given number of generations.");
				return;
			}
			actAll();
			Utility.print("\n");
			if((gen + 1) % genDisplay == 0) {
				Utility.print("Generation Number: " + gen + "\n");
				Utility.print("Number of Jaguars: " + jaguarNumber() + "\n");
				Utility.print("Number of Sheep: " + sheepNumber() + "\n");
				Utility.print("Number of Weed: " + weedNumber() + "\n");
				Utility.print("Number of Empty: " + emptyNumber() + "\n");
				//printWorld();
			}
		}
		/*
		int gen;
		for(gen = 0; alive(); gen++) {
			actAll();
		}
		World.print("bioSys.Main.main().DeBug - gen: " + gen + "\n");
		return gen;
		*/
	}
	private static int weedNumber() {
		int number = 0;
		for(int i = 0; i < animals.size(); i++) 
			if(animals.get(i).getClass().getSimpleName().equals("Weed")) 
				number++;
		return number;
	}
	private static int sheepNumber() {
		int number = 0;
		for(int i = 0; i < animals.size(); i++) 
			if(animals.get(i).getClass().getSimpleName().equals("Sheep")) 
				number++;
		return number;
	}
	private static int jaguarNumber() {
		int number = 0;
		for(int i = 0; i < animals.size(); i++) 
			if(animals.get(i).getClass().getSimpleName().equals("Jaguar")) 
				number++;
		return number;
	}
	private static int emptyNumber() {
		int number = 0;
		for(int i = 0; i < world.animal.length; i++) 
			for(int j = 0; j < world.animal[i].length; j++) 
				if(world.animal[i][j].getClass().getSimpleName().equals("Animal")) 
					number++;
		return number;
	}
	@SuppressWarnings("unused")
	private static void loadFile(String str) {
		Vector<String[]> arr = new Vector<String[]>();
		try(BufferedReader file = new BufferedReader(new FileReader(str))) {
			for(String line; (line = file.readLine()) != null; ) 
				arr.add(line.split("\\|"));
			worldx = arr.size();
			worldy = arr.get(0).length;
			world = new World(worldx, worldy);
			Animal pop;
			for(int i = 0; i < arr.size(); i++) 
				for(int j = 0; j < arr.get(i).length; j++) {
					if(arr.get(i)[j].equals("Weed")) 
						pop = new Weed(i, j, weight, q);
					else if(arr.get(i)[j].equals("Sheep")) 
						pop = new Sheep(i, j);
					else if(arr.get(i)[j].equals("Jaguar")) 
						pop = new Jaguar(i, j);
					else 
						pop = new Animal(i, j);
					if(!pop.getClass().getSimpleName().equals("Animal")) 
						animals.add(pop);
					world.putIn(pop);
				}
			// line is not visible here.
		}
		catch (IOException e) { e.printStackTrace(); }
	}
	static void printWorld() {
		PrintWriter out = null;
		try { Files.createDirectories(Paths.get("OutPut")); } 
		catch (IOException e) { /*e.printStackTrace();*/ }
		File file = new File("OutPut/BioSys - World.log");
		for (int i = 2; file.exists(); i++) 
			file = new File("OutPut/BioSys - World" + i + ".log");
		try { out = new PrintWriter(file); } 
		catch (FileNotFoundException localFileNotFoundException) {}
		
		Animal current;
		String str;
		for(int i = 0; i < world.animal.length; i++) {
			for(int j = 0; j < world.animal[i].length; j++) {
				current = world.animal[i][j];
				str = current.getClass().getSimpleName().charAt(0) + "(" + ((int) current.getMass()) + ", " + current.age + ")|";
				Utility.print(str);
				out.print(str);
			}
			Utility.print("\n");
			out.print("\n");
			for(int z = 0; z < world.animal.length * 7.4; z++) {
				Utility.print("-");
				out.print("-");
			}
			Utility.print("\n");
			out.print("\n");
		}
		Utility.print("\n");
		out.close();
	}
	static void prepareWorld() {
		PrintWriter out = null;
		try { Files.createDirectories(Paths.get("OutPut")); } 
		catch (IOException e) { /*e.printStackTrace();*/ }
		File file = new File("OutPut/BioSys - World.log");
		for (int i = 2; file.exists(); i++) 
			file = new File("OutPut/BioSys - World" + i + ".log");
		try { out = new PrintWriter(file); } 
		catch (FileNotFoundException localFileNotFoundException) {}
		
		Animal current;
		String str;
		for(int i = 0; i < world.animal.length; i++) {
			for(int j = 0; j < world.animal[i].length; j++) {
				current = world.animal[i][j];
				str = current.getClass().getSimpleName().charAt(0) + "|";
				out.print(str);
			}
			out.print("\n");
		}
		out.close();
	}
	static void actAll() {
		//World.print("bioSys.Main.actAll().DeBug\n");
		for(int i = 0; i < animals.size(); i ++) 
			animals.get(i).act();
	}
	static void putAll() {
		for(int i = 0; i < animals.size(); i ++) 
			world.putIn(animals.get(i));
	}
	static void updateID() {
		int i = 0;
		for(; i < animals.size(); i++) 
			animals.get(i).id = i;
		Main.id = i;
	}
	static void populate() {
		//World.print("bioSys.Main.populate().DeBug\n");
		double r;
		double check1, check2, check3;
		double p21, p31, p32, p33, p34;
		for(int i = 0; i < world.animal.length; i++) {
			//World.print("bioSys.Main.populate().DeBug - i: " + i + "\n");
			for(int j = 0; j < world.animal[0].length; j++) {
				//World.print("bioSys.Main.populate().DeBug - j: " + j + "\n");
				r = Math.random();
				Animal pop;
				/**
				 * Original Values:
				 * j - 0.108695653
				 * s - 0.380434783
				 * w - 1.52173913
				 * Test To:
				 * j - 0.1739130434782609
				 * s - 0.608695652173913
				 * w - 1.217391304347826
				 * 6th adjustments:
				 * j - 0.35
				 * s - 2.25
				 * w - 3.75
				 * ======
				 * j - 0.0608695652173913
				 * s - 1.369565217391304
				 * w - 4.565217391304348
				 * Over adjustment - * 0.7
				 * j - 0.0426086956521739
				 * s - 0.9586956521739128
				 * w - 3.195652173913043
				 */
				check1 = 3.195652173913043 / (Animal.distance(i, (worldx - 1) / 2, j, (worldy - 1) / 2) + 1);//14 / (2 + 7 + 14)
				check2 = 0.9586956521739128 * Math.cbrt(Animal.distance(i, (worldx - 1) / 2, j, (worldy - 1) / 2) - 1);//7 / (2 + 7 + 14)
				check3 = 0.0426086956521739 * (Animal.distance(i, (worldx - 1) / 2, j, (worldy - 1) / 2) - 1);//2 / (2 + 7 + 14)
				p21 = Animal.distance(worldx - 1, (worldx - 1) / 2, worldy - 1, (worldy - 1) / 2) * r;
				p31 = Animal.distance(worldx - 1, (worldx - 1) * 0.25, worldy - 1, (worldy - 1) * 0.25) * r;
				p32 = Animal.distance(worldx - 1, (worldx - 1) * 0.25, worldy - 1, (worldy - 1) * 0.75) * r;
				p33 = Animal.distance(worldx - 1, (worldx - 1) * 0.75, worldy - 1, (worldy - 1) * 0.25) * r;
				p34 = Animal.distance(worldx - 1, (worldx - 1) * 0.75, worldy - 1, (worldy - 1) * 0.75) * r;
				if(check3 > p21) 
					pop = new Jaguar(i, j);
				else if((check2 > p31) || (check2 > p32) || (check2 > p33) || (check2 > p34)) 
					pop = new Sheep(i, j);
				else if(check1 > r) 
						pop = new Weed(i, j/*, weight, q*/);
				else 
					pop = new Animal(i, j);
				if(!pop.getClass().getSimpleName().equals("Animal")) 
					animals.add(pop);
				world.putIn(pop);
			}
		}
	}
	static boolean alive() {
		//World.print("bioSys.Main.alive().DeBug\n");
		for(int i = 0; i < animals.size(); i++) {
			if(!animals.get(i).getClass().getSimpleName().equals("Weed")) 
				return true;
		}
		return false;
	}
}