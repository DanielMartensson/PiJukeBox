package se.danielmartensson.pijukebox;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

public class CSV {
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	
	private final String pathToCSV = "Songs.csv";
	private String[][] songMatrix;
	private int rowCount;
	private final int columnCount = 4;

	/**
	 * Constructor who loads the songMatrix with values
	 */
	public CSV() {
		try {
			// Get the row count and create song matrix
			Path path = Paths.get(pathToCSV);
			rowCount = (int) Files.lines(path).count();
			songMatrix = new String[rowCount][columnCount];
			
			// Set all the values to the song matrix
			BufferedReader csvReader = new BufferedReader(new FileReader(pathToCSV));
			String line;
			int row = 0;
			while ((line = csvReader.readLine()) != null) {
				// MaxADC, MinADC, ButtonPosition, fileName.mp3
				String[] cells = line.split(",");
				for(int column = 0; column < columnCount; column++) {
					songMatrix[row][column] = cells[column];
				}
				row++;
			}
			csvReader.close();
		} catch (IOException e) {
			logger.info("Could not read CSV file");
		}
	}
	
	/**
	 * Scan the song matrix and return the name, or null
	 * @param adcValue
	 * @param buttonPosition
	 * @return Name of the song, or null
	 */
	public String search(int adcValue, int buttonPosition) {
		String song = null;
		for(int row = 0; row < rowCount; row++) {
			int maxADC = Integer.parseInt(songMatrix[row][0]);
			int minADC = Integer.parseInt(songMatrix[row][1]);
			int pos = Integer.parseInt(songMatrix[row][2]);
			if(adcValue >= minADC && adcValue <= maxADC && pos == buttonPosition) {
				song = songMatrix[row][3]; // The name of the song
			}
		}
		return song;
	}

}
