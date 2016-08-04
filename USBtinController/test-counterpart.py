import time
import os

for num in range(1,50):
	f = open('./hat-log/hat.csv', 'a', os.O_NONBLOCK)
	line = "%d, %d, %f, %f, %f, %f, %f, %f, %f, %f\n" % (1000*time.time(), num, 12.3456789, 12.3456789, 12.3456789, 12.3456789, 12.3456789, 12.3456789, 12.3456789, 99.999)
	f.write(line)
	f.flush
	f.close()
	time.sleep(0.1)
