import RPi.GPIO as GPIO
import time

#Set GPIO pin numbering
GPIO.setmode(GPIO.BCM)
TRIG = 23
ECHO = 24

print "Measuring distance..."

#Set i/o mode for pins
GPIO.setup(TRIG,GPIO.OUT)
GPIO.setup(ECHO,GPIO.IN)

GPIO.output(TRIG, False)
print "Waiting for sensor to settle..."
time.sleep(2)

#Trigger
GPIO.output(TRIG, True)
time.sleep(0.00001)
GPIO.output(TRIG, False)

#Record time when ECHO goes from low to high
while GPIO.input(ECHO) == 0:
    pulse_start = time.time()

#Record end time of echo pulse
while GPIO.input(ECHO) == 1:
    pulse_end = time.time()

pulse_duration = pulse_end - pulse_start
distance = pulse_duration * 17150
distance = round(distance,2) #Round distance to 2 decimals

#Print distance if smaller or equals to 10 cm
if distance <= 10:
    print "Distance = ",distance,"cm"

GPIO.cleanup
