package se.danielmartensson.pijukebox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

/**
 * This class creates a song matrix with the columns
 * minADC, maxADC, buttonposition, FileName.mp3
 * 
 * Button position is a button where you can select different song bands.
 * If you don't have a button position like this. Just set MAX_BUTTON_POSITIONS = 1.
 * @author hp
 *
 */
public class CSV {
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	
	private String[][] songMatrix;
	private int rowCount;
	private static final int COLUMN_COUNT = 4;
	private static final int MIN_ADC = 320; // Minimum potentiometer position - Change this depending on which ADC you are using
	private static final int MAX_ADC = 980; // Maximum potentiometer position - Same here as well

	private static final int MAX_BUTTON_POSITIONS = 3;
	
	// For automatic playing
	private int currentRow;
	private int nextRow;


	/**
	 * Constructor who loads the songMatrix with values.
	 */
	public CSV() {
		try {
			// Get the row count and create song matrix
			InputStream in = getClass().getResourceAsStream("Songs.csv");
			
			// Check how many rows
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			rowCount = 0;
			while (reader.readLine() != null) rowCount++;
			reader.close();
			
			// Create matrix and compute the ADC step between 
			songMatrix = new String[rowCount][COLUMN_COUNT];
			float adcStep = (MAX_ADC - MIN_ADC)/(rowCount/MAX_BUTTON_POSITIONS); // Divide by 3 because I have a 3-way rotary button
			logger.info("Total rows in CSV is: " + rowCount + " and ADC step between songs is: " + adcStep);
			
			// Set all the values to the song matrix
			in = getClass().getResourceAsStream("Songs.csv");
			BufferedReader csvReader = new BufferedReader(new InputStreamReader(in));
			String line;
			int row = 0;
			int buttonPosition = 0;
			float initialADC = MIN_ADC;
			logger.info("Reading songs");
			while ((line = csvReader.readLine()) != null) {
				// MinADC, MaxADC, ButtonPosition, fileName.mp3
				System.out.println(line);
				songMatrix[row][0] = String.valueOf(initialADC);
				initialADC = initialADC + adcStep;
				songMatrix[row][1] = String.valueOf(initialADC);
				songMatrix[row][2] = String.valueOf(buttonPosition);
				songMatrix[row][3] = line;
				
				System.out.println("MinADC: " + songMatrix[row][0] + " MaxADC: " + songMatrix[row][1] + " ButtonPosition: " + buttonPosition + " Song: " + line);
				// Check if we need to change button position
				if(initialADC >= MAX_ADC) {
					initialADC = MIN_ADC;
					buttonPosition++; // 0, 1, 2
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
			float minADC = Float.parseFloat(songMatrix[row][0]);
			float maxADC = Float.parseFloat(songMatrix[row][1]);
			int position = Integer.parseInt(songMatrix[row][2]);
			if(adcValue >= minADC && adcValue <= maxADC && position == buttonPosition) {
				// Save these selections because we will use this for automatic playing later
				currentRow = row;
				song = songMatrix[row][3]; // The name of the song
				return song;
			}
		}
		return song;
	}
	
	/**
	 * When we select a new song by turning the knob, then we need to remember its index row.
	 */
	public void initNextRow() {
		nextRow = currentRow;
	}
	
	/**
	 * Now we can play songs by automatic. 
	 * @return The next song
	 */
	public String nextSong() {
		String song = null;
		nextRow++;
		if(nextRow >= rowCount)
			nextRow = 0; // reset
		song = songMatrix[nextRow][3]; // The name of the song
		return song;
		
	}

}
