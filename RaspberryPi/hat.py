from sense_hat import SenseHat
from subprocess import call
import time
import os

sense = SenseHat()

start = time.time()
current = time.time()
i = 0
video = 0

#SET MAX LOG DURATION IN SECONDS
while (current-start) < 5:
	
	current = time.time()

	t = sense.get_temperature()
	p = sense.get_pressure()
	h = sense.get_humidity()
	pitch, roll, yaw = sense.get_orientation().values()
	xc, yc, zc = sense.get_compass_raw().values()
	xg, yg, zg = sense.get_gyroscope_raw().values()
	xa, ya, za = sense.get_accelerometer_raw().values()

	f = open('./hat-log/hat.csv', 'a', os.O_NONBLOCK)
	line = "%d, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f, %f\n" % (1000*time.time(),t,p,h,pitch,roll,yaw,xc,yc,zc,xg,yg,zg,xa,ya,za)
	f.write(line)
	f.flush
	f.close()

	#set za threshold to the number of g you would expect at launch
	if video == 0 and (za > 1.1 or za < -1.1):
		video = 1
		call(["./video", ""])

	#print i
	i = i+1