#!/usr/bin/env python

from __future__ import print_function
import os
import subprocess
import re

pattern = re.compile('@loader_path/([^ ]+) ')

required = []
to_check = ['libglvideo.jnilib']

# add all modules
for fn in os.listdir('../../library/macosx/gstreamer-1.0'):
	if fn.endswith('.so'):
		to_check.append('gstreamer-1.0/' + fn)

while 0 < len(to_check):
	tested = to_check.pop()
	out = subprocess.check_output('otool -L ../../library/macosx/' + tested, shell=True)
	required.append(tested)
	deps = pattern.findall(out)
	for dep in deps:
		# we're in the module directory, remove any trailing ../
		if '/' in tested and dep[0:3] == '../':
			dep = dep[3:]
		if dep not in required and dep not in to_check:
			to_check.append(dep)
	#print(to_check)

required.sort()
#print(required)

# remove unneeded libs
for fn in os.listdir('../../library/macosx'):
	if fn not in required:
		try:
			print('Removing ' + fn + ' ... ', end='')
			os.remove('../../library/macosx/' + fn)
			print('Done')
		except:
			print('Fail')
