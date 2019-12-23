# PiJukeBox

This is a simple project where I use Java and Pi4J to turn an old Centrum U68 from 1940 into a MP3 player. 
The reason is because short wave, middle wave and long wave is today obsolete and not being used or sended today
in Sweden. Also the electronics inside was a mess and very dangerous because it runs on both AC/DC current and all the
wires is begun to lose its isolators. 

So I installed a Raspberry Pi B+ and OpenJDK 8 and Pi4J inside this radio and then it become a juke box.


![a](https://raw.githubusercontent.com/DanielMartensson/PiJukeBox/master/Radio.jpg)

## Electical compontents if you want to build your own
```
* Raspberry
* MPC3008 10 bit ADC
* MCP23017 IO-expander for the LCD
* 1602 LCD
* 10K potentiometer
* Turn button 3-way
* PC speakers with Aux cable
* 5 Volt Micro USB charger
```

## How to use
Step 1: Download the project

Step 2: Go to the PiJukeBox folder and run

```
./graldew fatJar // Linux
gradlew fatJar // Windows
```
Inside build/libs folder there is a file named PiJukeBox-all-1.0.jar

Step 3: Transfer it to your Raspberry Pi
```
scp PiJukeBox-all-1.0.jar pi@IpAddressOfPi:/home/pi/your/folder
```

Step 4: Open rc.local in ```/etc/rc.local```

```
sudo nano /etc/rc.local
// Paste this above exit 0
cd /home/pi/your/folder/PiJukeBox-all-1.0.jar
java -jar PiJukeBox-all-1.0.jar & // importat with &, else it will stop here
```

Step 5: Change OpenJDK 8 like this and save
```
sudo nano /etc/java-8-openjdk/sound.properties

// Place a square before the icedtea classpath configs like this

#javax.sound.sampled.Clip=org.classpath.icedtea.pulseaudio.PulseAudioMixerProvider
#javax.sound.sampled.Port=org.classpath.icedtea.pulseaudio.PulseAudioMixerProvider
#javax.sound.sampled.SourceDataLine=org.classpath.icedtea.pulseaudio.PulseAudioMixerProvider
#javax.sound.sampled.TargetDataLine=org.classpath.icedtea.pulseaudio.PulseAudioMixerProvider

// Remove the square (#) from the sun classpath configs like this

javax.sound.sampled.Clip=com.sun.media.sound.DirectAudioDeviceProvider
javax.sound.sampled.Port=com.sun.media.sound.PortMixerProvider
javax.sound.sampled.SourceDataLine=com.sun.media.sound.DirectAudioDeviceProvider
javax.sound.sampled.TargetDataLine=com.sun.media.sound.DirectAudioDeviceProvider
```

Now you can play MP3 songs via the terminal.

Step 6: Reboot

Done! Every time you start your raspberry, the musik will start up and roll on

