package se.danielmartensson.pijukebox;

import java.io.IOException;
import java.util.logging.Logger;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

/**
 * MP3 player class. Just leave this as it is, or if you got another ADC reader, just extend MAX_ADC
 * @author hp
 *
 */
public class MP3 {
	
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	
	private static final double MAX_ADC = 1023;
	private BasicPlayer player;
	private CSV csv = new CSV();
	private int volumeADCValue = 0;
	private String song = null;
	
	/**
	 * Constructor
	 */
	public MP3() {
		player = new BasicPlayer();
	}
	
	/**
	 * Change song when adcValue is between two values
	 * @param adcValue
	 */
	public void changeSong(int adcValue, int buttonPosition) {
		// Only change song if the return value changes and not being null
		String song = csv.search(adcValue, buttonPosition);
		if(song == null) {
			logger.info("No song selected. adcValue: " + adcValue + " buttonPosition: " + buttonPosition);
			return;
		}
		
		// Check if it's the same song we have selected
		if(this.song == song) {
			// Wait until this song has been played out
			if(BasicPlayer.STOPPED == player.getStatus()) {
				song = csv.nextSong(); // Get next song and play it
			}else {
				return; // No action
			}
		}else {
			csv.initNextRow(); // Important to call this method for count indexing
			this.song = song; // Save the new song so we don't reinit the nextRow again
		}
		
		// Change song now
		String pathToMp3 = getClass().getResource(song).toString();
		//InputStream in = getClass().getResourceAsStream(song);
		try {
			player.open(getClass().getResource(song));
			player.play();
			logger.info("Playing the song: " + song);
		} catch (BasicPlayerException e) {
			e.printStackTrace();
			logger.info("Could not turn on MP3 music from: " + pathToMp3);
		}
	    
	}
	
	/**
	 * Change the volume by changing the adcValue
	 * @param adcValue
	 */
	public void changeVolume(int adcValue) {
		// Check if it's the same adc as before
		if(volumeADCValue == adcValue)
			return;
		
		// Save
		volumeADCValue = adcValue;
		
		// Adjust volume now with amixer - This percent formula makes dB tuning smoother
		double procent = 21.67*Math.log(1 + 1/MAX_ADC * (adcValue)*100);
		try {
			Runtime.getRuntime().exec("amixer  sset PCM,0 " + procent + "%");
		} catch (IOException e) {
			logger.info("Cannot adjust volume");
		}
		
		
	}

}
