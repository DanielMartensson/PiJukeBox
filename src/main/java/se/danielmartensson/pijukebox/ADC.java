package se.danielmartensson.pijukebox;

import java.io.IOException;
import java.util.logging.Logger;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.SpiDevice;
import com.pi4j.io.spi.SpiFactory;

/**
 * This is code for MCP3008
 * @author hp
 *
 */
public class ADC {
	
	private static final Logger logger = Logger.getLogger(Main.class.getName());

	// SPI device
	public static SpiDevice spi = null;

	public ADC() {

		try {
			// create SPI object instance for SPI for communication
			// default spi mode 0
			spi = SpiFactory.getInstance(SpiChannel.CS0, SpiDevice.DEFAULT_SPI_SPEED, SpiDevice.DEFAULT_SPI_MODE);
		} catch (IOException e) {
			// TODO: LOGGER
		}
	}

	/**
	 * Read MPC3008
	 * @param channel
	 * @return
	 */
	public int read(short channel) {

		// create a data buffer and initialize a conversion request payload
		byte data[] = new byte[] { 
				(byte) 0b00000001, 								// first byte, start bit
				(byte) (0b10000000 | (((channel & 7) << 4))),   // second byte transmitted -> (SGL/DIF = 1, D2=D1=D0=0)
				(byte) 0b00000000 								// third byte transmitted....don't care
		};

		// send conversion request to ADC chip via SPI channel
		try {
			byte[] result = spi.write(data);
			// calculate and return conversion value from result bytes
			int value = (result[1] << 8) & 0b1100000000; // merge data[1] & data[2] to get 10-bit result
			value |= (result[2] & 0xff);
			return value;
		} catch (IOException e) {
			logger.info("Cannot read the ADC");
			return 0;
		}
	}
}
