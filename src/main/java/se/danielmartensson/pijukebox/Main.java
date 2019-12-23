package se.danielmartensson.pijukebox;

import java.util.logging.Logger;

/**
 * Main class. Just run this project as a JAR file
 * @author hp
 *
 */
public class Main {

	private static final Logger logger = Logger.getLogger(Main.class.getName());
	
	public static void main(String[] args) {
		
		// Create objects
		ADC adc = new ADC();
		LCD lcd = new LCD(0x20); // 0x20 = address for the IO-expander IC = A0, A1, A2 = GND
		lcd.LCD1602_noBlink();
		lcd.LCD1602_noCursor();
		MP3 mp3 = new MP3();
		GPIO gpio = new GPIO();
		
		logger.info("Starting Pi JukeBox");

		for(;;) {
			
			//First read ADC
			int songADC = adc.read((short) 0);
			int volumeADC = adc.read((short) 1);
			
			// Read the button position
			int buttonPosition = gpio.buttonPosition();
			
			// Then show it on the LCD
			lcd.LCD1602_setCursor((byte) 1, (byte) 1);
			lcd.LCD1602_print("Song:" + String.valueOf(songADC) + " Pos:" + String.valueOf(buttonPosition));
			lcd.LCD1602_setCursor((byte) 2, (byte) 1);
			lcd.LCD1602_print("Volume:" + String.valueOf(volumeADC) + "    ");
			
			// And now change the song
			mp3.changeSong(songADC, buttonPosition);
			mp3.changeVolume(volumeADC);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.info("Cannot sleep in the main thread");
			}
		}
	}

}
