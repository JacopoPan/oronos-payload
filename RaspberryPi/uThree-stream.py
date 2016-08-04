import time
import u3
import os

file = [0,0,0,0,0]
file[0] = open('./u3-log/u3-stream-0.csv', 'a', os.O_NONBLOCK)
file[1] = open('./u3-log/u3-stream-1.csv', 'a', os.O_NONBLOCK)
file[2] = open('./u3-log/u3-stream-2.csv', 'a', os.O_NONBLOCK)
file[3] = open('./u3-log/u3-stream-3.csv', 'a', os.O_NONBLOCK)
file[4] = open('./u3-log/u3-stream-4.csv', 'a', os.O_NONBLOCK)

input_channels = [0,1,2,3,4]
num_channels = len(input_channels)

d = u3.U3()
d.configU3()
d.getCalibrationData()
d.configIO(FIOAnalog = 31) 				#11111
d.streamConfig(NumChannels = num_channels,
		PChannels = input_channels,
		NChannels = [31]*num_channels, 		#[32]*num_channels, ch options: 0-7 for FIO0-FIO7, 8-15 for EIO0-EIO15, 30 for Vref, 31/199 for single-ended
		Resolution = 0,				#0-3, 0 highest, limits ScanFreq to 10k
		ScanFrequency = 200 			#(10000 // num_channels)
		)

d.streamStart()

start = time.time()
current = time.time()

count = 1

for r in d.streamData():
	
	current = time.time()

	#SET MAX LOG DURATION IN SECONDS
	if current-start > 5:
		break

	else:
		if r is not None:
            		if r['errors'] != 0:
                		print("STREAM ERROR : {}".format(r['errors']))

           		if r['numPackets'] != d.packetsPerRequest:
                		print("---- UNDERFLOW : {}".format(r['numPackets']))
                    
            		if r['missed'] != 0:
                		print("++++ MISSED : {}".format(r['missed']))

			for ch in input_channels:
				#print ch 			#MIGHT BE REVERSED?
				#print(r['AIN{}'.format(ch)])
				#print(len(r['AIN{}'.format(ch)]))

				for val in r['AIN{}'.format(ch)]:
					file[ch].write("%.16f, " % val)
				file[ch].flush

				#count = count + 1

        	else:
           		 # no data from read
            		print("No data")


		count = count + 1
	

d.streamStop()

#print count

#print d.getAIN(0)
#print d.getAIN(1)
#print d.getAIN(2)
#print d.getAIN(3)
#print d.getAIN(4)

d.close()

file[0].close()
file[1].close()
file[2].close()
file[3].close()
file[4].close()