package se.danielmartensson.pijukebox;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerException;

public class MP3 {
	
	private static final Logger logger = Logger.getLogger(Main.class.getName());
	
	private static final float MAX_ADC = 1023;
	private BasicPlayer player;
	private CSV csv = new CSV();
	private int adcValue = 0;
	private int buttonPostion = 0;
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
	public void changeSong(int adcValue, int buttonPostion) {
		// Only call this method if the adcValue or buttonPosition changes
		if(this.adcValue == adcValue && this.buttonPostion == buttonPostion)
			return;
		
		// Save
		this.adcValue = adcValue;
		this.buttonPostion = buttonPostion; 
		
		// Only change song if the return value changes and not being null
		String song = csv.search(adcValue, buttonPostion);
		if(song == null)
			return;
		
		// Change only if the song is not the same
		if(this.song == song)
			return; 
		
		// Save
		this.song = song;
		
		// Change song now
		String pathToMp3 = System.getProperty("user.dir") +"/"+ song;
		try {
			player.open(new URL("file:///" + pathToMp3));
			player.play();
		} catch (MalformedURLException | BasicPlayerException e) {
			logger.info("Could not turn on MP3 music");
		}
	    
	}
	
	/**
	 * Change the volume by chaning the adcValue
	 * @param adcValue
	 */
	public void changeVolume(int adcValue) {
		double fGain = 1/MAX_ADC * ((float)  adcValue);
		try {
			player.setGain(fGain);
		} catch (BasicPlayerException e) {
			logger.info("Could not adjust MP3 music");
		}
	}

}
