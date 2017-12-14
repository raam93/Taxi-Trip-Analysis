#!/usr/bin/env python
import sys 

import matplotlib.pyplot as plt 
import pylab as pl
import numpy as np

file_name = sys.argv[1]

plotFor = sys.argv[2]

if plotFor == 'Length Distribution':
		
	d = {}
	
	with open(file_name) as inputFile:
	
		for line in inputFile:
	
			line = line.strip().replace("(", "").replace(")", "")
		
			(binIndex, frequency) = line.split(",")
			d[int(float(binIndex))] = int(frequency)


	d = sorted(d.items())
	
	x, y = zip(*d)
	
	plt.xlabel('Trip Length')
	plt.ylabel('No. of Trips')
	plt.gcf().subplots_adjust(bottom=0.15)
	plt.gcf().subplots_adjust(left=0.15)
	
	plt.plot(x, y, label='naive')
	plt.savefig('graph.png')

	
elif plotFor == 'Airport Revenue':

	dates = []
	revenues = []
	
	with open(file_name) as inputFile:
	
		lines = inputFile.readlines()
		lines = lines[:-1]
		N = len(lines)	
		samples = [ '' ] * N
		
		x = np.arange(1,N+1)
		
		for line in lines:
			
			line = line.strip()
			date, revenue = line.split("	")
			
			dates.append(date)
			revenues.append(revenue)
			
		
	for i in range( 0, N, N/8 ):
		samples[i] = dates[i]
        	
	pl.xticks(x ,samples, rotation=45)	
	pl.xlabel('Date')
	pl.ylabel('Revenue in USD')
	
	pl.gcf().subplots_adjust(bottom=0.20)
	pl.gcf().subplots_adjust(left=0.15)

	pl.plot(x,revenues, label='naive')
	pl.savefig('graph1.png')		
			
			
				

	
	

