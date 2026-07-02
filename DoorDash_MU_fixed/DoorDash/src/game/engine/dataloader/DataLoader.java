package game.engine.dataloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import game.engine.exceptions.InvalidCSVFormat;
import game.engine.Role;
import game.engine.cards.*;
import game.engine.cells.*;
import game.engine.monsters.*;

public class DataLoader {
	private static final String CARDS_FILE_NAME    = "cards.csv";
	private static final String CELLS_FILE_NAME    = "cells.csv";
	private static final String MONSTERS_FILE_NAME = "monsters.csv";

	/**
	 * Opens a CSV file. Tries the working directory first (Eclipse default = project root),
	 * then one level up, then the class-loader resource stream.
	 * Java 8 compatible (no try-with-resources on non-AutoCloseable etc).
	 */
	private static BufferedReader openCsv(String filename) throws IOException {
		File f = new File(filename);
		if (f.exists())
			return new BufferedReader(new FileReader(f));

		File parent = new File(".." + File.separator + filename);
		if (parent.exists())
			return new BufferedReader(new FileReader(parent));

		InputStream is = DataLoader.class.getClassLoader().getResourceAsStream(filename);
		if (is != null)
			return new BufferedReader(new InputStreamReader(is, "UTF-8"));

		throw new IOException("Cannot find '" + filename + "'. "
				+ "Make sure the CSV files are in the project root folder "
				+ "(next to the src/ folder). In Eclipse: Run > Run Configurations > "
				+ "Arguments tab > Working directory > Other > ${project_loc}");
	}

	public static ArrayList<Card> readCards() throws IOException {
		ArrayList<Card> cards = new ArrayList<Card>();
		BufferedReader br = openCsv(CARDS_FILE_NAME);
		try {
			String nextLine;
			while ((nextLine = br.readLine()) != null) {
				nextLine = nextLine.trim();
				if (nextLine.isEmpty()) continue;

				String[] data = nextLine.split(",");
				if (data.length != 4 && data.length != 5)
					throw new InvalidCSVFormat(nextLine);

				String cardType = data[0].trim();
				Card card;
				switch (cardType) {
					case "SWAPPER":
						card = new SwapperCard(data[1].trim(), data[2].trim(), Integer.parseInt(data[3].trim())); break;
					case "ENERGYSTEAL":
						card = new EnergyStealCard(data[1].trim(), data[2].trim(), Integer.parseInt(data[3].trim()), Integer.parseInt(data[4].trim())); break;
					case "STARTOVER":
						card = new StartOverCard(data[1].trim(), data[2].trim(), Integer.parseInt(data[3].trim()), Boolean.parseBoolean(data[4].trim())); break;
					case "SHIELD":
						card = new ShieldCard(data[1].trim(), data[2].trim(), Integer.parseInt(data[3].trim())); break;
					case "CONFUSION":
						card = new ConfusionCard(data[1].trim(), data[2].trim(), Integer.parseInt(data[3].trim()), Integer.parseInt(data[4].trim())); break;
					default:
						throw new InvalidCSVFormat("Unknown card type: " + cardType);
				}
				cards.add(card);
			}
		} finally {
			br.close();
		}
		return cards;
	}

	public static ArrayList<Cell> readCells() throws IOException {
		ArrayList<Cell> cells = new ArrayList<Cell>();
		BufferedReader br = openCsv(CELLS_FILE_NAME);
		try {
			String nextLine;
			while ((nextLine = br.readLine()) != null) {
				nextLine = nextLine.trim();
				if (nextLine.isEmpty()) continue;

				String[] data = nextLine.split(",");
				if (data.length != 2 && data.length != 3)
					throw new InvalidCSVFormat(nextLine);

				Cell cell;
				if (data.length == 2) {
					int effect = Integer.parseInt(data[1].trim());
					cell = effect > 0
							? new ConveyorBelt(data[0].trim(), effect)
							: new ContaminationSock(data[0].trim(), effect);
				} else {
					cell = new DoorCell(data[0].trim(), Role.valueOf(data[1].trim()), Integer.parseInt(data[2].trim()));
				}
				cells.add(cell);
			}
		} finally {
			br.close();
		}
		return cells;
	}

	public static ArrayList<Monster> readMonsters() throws IOException {
		ArrayList<Monster> monsters = new ArrayList<Monster>();
		BufferedReader br = openCsv(MONSTERS_FILE_NAME);
		try {
			String nextLine;
			while ((nextLine = br.readLine()) != null) {
				nextLine = nextLine.trim();
				if (nextLine.isEmpty()) continue;

				String[] data = nextLine.split(",");
				if (data.length != 5)
					throw new InvalidCSVFormat(nextLine);

				String monsterType = data[0].trim();
				Monster monster;
				switch (monsterType) {
					case "DYNAMO":
						monster = new Dynamo(data[1].trim(), data[2].trim(), Role.valueOf(data[3].trim()), Integer.parseInt(data[4].trim())); break;
					case "DASHER":
						monster = new Dasher(data[1].trim(), data[2].trim(), Role.valueOf(data[3].trim()), Integer.parseInt(data[4].trim())); break;
					case "MULTITASKER":
						monster = new MultiTasker(data[1].trim(), data[2].trim(), Role.valueOf(data[3].trim()), Integer.parseInt(data[4].trim())); break;
					case "SCHEMER":
						monster = new Schemer(data[1].trim(), data[2].trim(), Role.valueOf(data[3].trim()), Integer.parseInt(data[4].trim())); break;
					default:
						throw new InvalidCSVFormat("Unknown monster type: " + monsterType);
				}
				monsters.add(monster);
			}
		} finally {
			br.close();
		}
		return monsters;
	}
}
