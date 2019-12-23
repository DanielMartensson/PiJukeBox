package se.danielmartensson.pijukebox;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

/*
 * Connect on MCP23017 to the LCD 16x2 for 8-bit mode
 * GPB0 - RS
 * GPB1 - RW
 * GPB2 - E
 *
 * GPA7 - D7
 * GPA6 - D6
 * GPA5 - D5
 * GPA4 - D4
 * GPA3 - D3
 * GPA2 - D2
 * GPA1 - D1
 * GPA0 - D0
 */

public class LCD {
	
	private static final Logger logger = Logger.getLogger(Main.class.getName());


	// Parameters for the I2C
	private byte pData_A_side[] = new byte[] { 0, 0 };
	private byte pData_B_side[] = new byte[] { 0, 0 };
	private I2CDevice device;
	private byte FunctionSet = 0x38;
	private byte DisplayControl = 0x0F;
	private byte LCD_CLEARDISPLAY = 0x01;
	private byte LCD_DISPLAYCONTROL = 0x08;
	private byte LCD_FUNCTIONSET = 0x20;
	
	// Display control
	private byte LCD_DISPLAY_B = 0x01;
	private byte LCD_DISPLAY_C = 0x02;
	private byte LCD_DISPLAY_D = 0x04;

	// Function set control
	private byte LCD_FUNCTION_N	= 0x08;
	private byte LCD_FUNCTION_DL = 0x10;

	/**
	 * Get the device object
	 * 
	 * @param address
	 * @throws UnsupportedBusNumberException
	 * @throws IOException
	 */
	public LCD(int address) {
		
		try {
			I2CBus bus = I2CFactory.getInstance(I2CBus.BUS_1);
			device = bus.getDevice(address);
			
			
			// Set output for A-side and B-side
			pData_A_side[0] = 0x00;
			pData_A_side[1] = 0x00;
			device.write(pData_A_side);

			//  B-side - Output
			pData_B_side[0] = 0x01;
			device.write(pData_B_side);
			
			//Function set variable to 8 bits mode
			FunctionSet = 0x38;
			//Initialise LCD
			//1. Wait at least 15ms
			Thread.sleep(20);
			//2. Attentions sequence
			LCD1602_writeCommand((byte) 0x30);
			Thread.sleep(5);
			LCD1602_writeCommand((byte) 0x30);
			Thread.sleep(1);
			LCD1602_writeCommand((byte) 0x30);
			Thread.sleep(1);
			//3. Function set; Enable 2 lines, Data length to 8 bits
			LCD1602_writeCommand((byte) (LCD_FUNCTIONSET | LCD_FUNCTION_N | LCD_FUNCTION_DL));
			//4. Display control (Display ON, Cursor ON, blink cursor)
			LCD1602_writeCommand((byte) (LCD_DISPLAYCONTROL | LCD_DISPLAY_B | LCD_DISPLAY_C | LCD_DISPLAY_D));
			//5. Clear LCD and return home
			LCD1602_writeCommand(LCD_CLEARDISPLAY);
			Thread.sleep(2);
		} catch (UnsupportedBusNumberException | IOException | InterruptedException e) {
			logger.info("Cannot init the LCD");
		}
		
	}

	/**
	 * Enable EN pulse
	 */
	private void LCD1602_EnablePulse() {
		try {
			pData_B_side[0] = 0x13; // Calling B side
			pData_B_side[1] |= 0x04; // 1 0 0 = Only E will be enabled
			device.write(pData_B_side);
			Thread.sleep(1);
			pData_B_side[0] = 0x13; // Calling B side
			pData_B_side[1] &= ~0x04; // 1 0 0 -> 0 1 1 = Only E will be disabled
			device.write(pData_B_side);
			Thread.sleep(1);
		} catch (Exception e) {
			logger.info("Cannot enable a pulse on LCD");
		}
	}

	/**
	 * RS control
	 * 
	 * @param state
	 */
	private void LCD1602_RS(boolean state) {
		try {
			pData_B_side[0] = 0x13; // Calling B side
			if (state == true) {
				pData_B_side[1] |= 0x01; // 0 0 1 = Only RS will be enabled
				device.write(pData_B_side);
			} else {
				pData_B_side[1] &= ~0x01; // 0 0 1 -> 1 1 0 = Only RS will be disabled
				device.write(pData_B_side);
			}
		} catch (Exception e) {
			logger.info("Cannot RS on the LCD");
		}
	}

	/**
	 * Write Parallel interface
	 * 
	 * @param command
	 */
	private void LCD1602_write(byte command) {
		try {
			pData_A_side[0] = 0x12; // Calling A side
			pData_A_side[1] = command;
			device.write(pData_A_side);
			LCD1602_EnablePulse();
		} catch (Exception e) {
			logger.info("Cannot wrote to the LCD");
		}

	}

	/**
	 * Write command
	 * 
	 * @param command
	 */
	private void LCD1602_writeCommand(byte command) {
		// Set RS to 0
		LCD1602_RS(false);
		// Call low level write parallel function
		LCD1602_write(command);
	}

	/**
	 * Write 8 bits data
	 * 
	 * @param data
	 */
	private void LCD1602_writeData(byte data) {
		// Set RS to 1
		LCD1602_RS(true);
		// Call low level write parallel function
		LCD1602_write(data);
	}

	/**
	 * LCD print string
	 * 
	 * @param string
	 */
	public void LCD1602_print(String string) {
		for (int i = 0; i < string.length(); i++) {
			LCD1602_writeData((byte) string.charAt(i));
		}
	}

	/**
	 * set cursor position
	 * 
	 * @param row
	 * @param col
	 */
	public void LCD1602_setCursor(byte row, byte col) {
		byte maskData;
		maskData = (byte) ((col - 1) & 0x0F);
		if (row == 1) {
			maskData |= (0x80);
			LCD1602_writeCommand(maskData);
		} else {
			maskData |= (0xc0);
			LCD1602_writeCommand(maskData);
		}
	}

	/**
	 * Write on first line
	 */
	public void LCD1602_1stLine() {
		LCD1602_setCursor((byte) 1, (byte) 1);
	}

	/**
	 * Write on second line
	 */
	public void LCD1602_2ndLine() {
		LCD1602_setCursor((byte) 2, (byte) 1);
	}

	/**
	 * Enable two lines
	 */
	public void LCD1602_TwoLines() {
		FunctionSet |= (0x08);
		LCD1602_writeCommand(FunctionSet);
	}

	/**
	 * Enable one line
	 */
	public void LCD1602_OneLine() {
		FunctionSet &= ~(0x08);
		LCD1602_writeCommand(FunctionSet);
	}

	/**
	 * Cursor OFF
	 */
	void LCD1602_noCursor() {
		DisplayControl &= ~(0x02);
		LCD1602_writeCommand(DisplayControl);
	}

	/**
	 * Display Cursor
	 */
	void LCD1602_cursor() {
		DisplayControl |= (0x02);
		LCD1602_writeCommand(DisplayControl);
	}

	/**
	 * Clear display
	 */
	void LCD1602_clear() {
		LCD1602_writeCommand(LCD_CLEARDISPLAY);
		try {
			Thread.sleep(3);
		} catch (InterruptedException e) {
			logger.info("Cannot sleep on LCD1602_clear() method");
		}
	}

	/**
	 * No Blinking cursor
	 */
	void LCD1602_noBlink() {
		DisplayControl &= ~(0x01);
		LCD1602_writeCommand(DisplayControl);
	}

	/**
	 * Blinking cursor
	 */
	void LCD1602_blink() {
		DisplayControl |= 0x01;
		LCD1602_writeCommand(DisplayControl);
	}

	/**
	 * Display OFF
	 */
	void LCD1602_noDisplay() {
		DisplayControl &= ~(0x04);
		LCD1602_writeCommand(DisplayControl);
	}

	/**
	 * LCD Display
	 */
	void LCD1602_display() {
		DisplayControl |= (0x04);
		LCD1602_writeCommand(DisplayControl);
	}

	/**
	 * LCD shift to right
	 * 
	 * @param num
	 */
	void LCD1602_shiftToRight(byte num) {
		for (byte i = 0; i < num; i++) {
			LCD1602_writeCommand((byte) 0x1c);
		}
	}

	/**
	 * LCD shift to left
	 * 
	 * @param num
	 */
	void LCD1602_shiftToLeft(byte num) {
		for (byte i = 0; i < num; i++) {
			LCD1602_writeCommand((byte) 0x18);
		}
	}

	/**
	 * Print integer
	 * 
	 * @param number
	 */
	void LCD1602_PrintInt(int number) {
		String numStr = String.valueOf(number);
		LCD1602_print(numStr);
	}

	/**
	 * Print float
	 * 
	 * @param number
	 * @param decimalPoints
	 */
	void LCD1602_PrintFloat(float number, int decimalPoints) {
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(decimalPoints);
		String numStr = df.format(number);
		LCD1602_print(numStr);
	}

}
