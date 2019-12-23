package se.danielmartensson.pijukebox;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Constructor for the GPIO inputs. It's for the button position.
 *   29 
 *     \
 *      \
 *       \
 * 28 ----O------3.3v where O = rotary button switch
 *       /
 *      /
 *     /
 *    /
 *  27    
 * @author hp
 *
 */
public class GPIO {
	
	private GpioPinDigitalInput position0;
	private GpioPinDigitalInput position1;
	private GpioPinDigitalInput position2;
	
	public GPIO() {
		
		// create gpio controller
        final GpioController gpio = GpioFactory.getInstance();

        // Declare the position for a 3-way rotary button
        position0 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29, PinPullResistance.PULL_DOWN);
        position1 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_28, PinPullResistance.PULL_DOWN);
        position2 = gpio.provisionDigitalInputPin(RaspiPin.GPIO_27, PinPullResistance.PULL_DOWN);
	}
	
	/**
	 * Get the position of the 3-way rotary button
	 * @return
	 */
	public int buttonPosition() {
		int position = 0;
		if(position0.getState().isHigh() == true)
			position = 0;
		if(position1.getState().isHigh() == true)
			position = 1;
		if(position2.getState().isHigh() == true)
			position = 2;
		
		return position;
	}

}
