import argparse
import functools
import math
import os
import random


parser = argparse.ArgumentParser()
parser.add_argument('command', nargs='*', choices=['train', 'test'])
parser.add_argument("--clf", action="store_true", default=False, help='only create classification data')
parser.add_argument("--reg", action="store_true", default=False, help='only create regression data')
args = parser.parse_args()

TARGET_DIR = os.path.join(os.path.dirname(__file__), '../src/test/resources')

sign = functools.partial(math.copysign, 1)

random.seed(1337)

suffixList = ['clf', 'reg']
if args.clf:
    suffixList = ['clf']
if args.reg:
    suffixList = ['reg']

for command in args.command:
    for suff in suffixList:

        if command == 'train':
            nInstances = 1000
        elif command == 'test':
            nInstances = 1000
        else:
            nInstances = 1000

        filename = os.path.join(TARGET_DIR, command + '_' + suff + '.csv')
        print('Creating data in', filename)

        with open(filename, 'w') as myfile:

            if suff == 'clf':
                print('Index X Y Type', file=myfile)

                for i in range(nInstances):
                    label = random.randint(0, 1)
                    x = 0
                    y = 0
                    strategy = 'circles'
                    if strategy == 'halfs':
                        x = -random.random() if label is 0 else random.random()
                        y = 2 * random.random() - 1
                    elif strategy == 'quarters':
                        x = 2 * random.random() - 1
                        y = 2 * random.random() - 1
                        label = 0 if x * y > 0 else 1
                    elif strategy == 'diagonal':
                        x = 2 * random.random() - 1
                        y = 2 * random.random() - 1
                        label = 0 if x - y > 0 else 1
                    elif strategy == 'circle':
                        x = 2 * random.random() - 1
                        y = 2 * random.random() - 1
                        label = 1 if math.sqrt(x**2 + y**2) < 0.5 else 0
                    elif strategy == 'ellipse':
                        x = 2 * random.random() - 1
                        y = 2 * random.random() - 1
                        xCenter = x - 0.2
                        yCenter = y + 0.3
                        rx = 0.75
                        ry = 0.5
                        label = 1 if math.sqrt((xCenter/rx)**2 + (yCenter/ry)**2) < 1 else 0
                    elif strategy == 'circles':
                        x = 2 * random.random() - 1
                        y = 2 * random.random() - 1
                        d = 0.5
                        r = 0.25
                        partOfCircle = \
                            math.sqrt((x-d)**2 + (y-d)**2) < r or \
                            math.sqrt((x+d)**2 + (y+d)**2) < r or \
                            math.sqrt((x-d)**2 + (y+d)**2) < r or \
                            math.sqrt((x+d)**2 + (y-d)**2) < r
                        label = 1 if partOfCircle else 0
                    x = round(x, 2)
                    y = round(y, 2)
                    print('{} {} {} {}'.format(i, x, y, label), file=myfile)

            elif suff == 'reg':
                print('Index X Y', file=myfile)

                for i in range(nInstances):

                    strategy = 'linear'
                    if strategy == 'linear':
                        x = [2 * random.random() - 1]
                        y = 2 * x[0] - 1
                    elif strategy == 'twodimlinear':
                        x = [2 * random.random() - 1, 2 * random.random() - 1]
                        y = 2 * x[0] + 1 * x[1] - 1
                    elif strategy == 'multidimlinear':
                        x = [2 * random.random() - 1,
                             2 * random.random() - 1,
                             2 * random.random() - 1,
                             2 * random.random() - 1]
                        y = 2 * x[0] + 1 * x[1] + 10 * x[2] - 5 * x[3] - 1
                    elif strategy == 'quadratic':
                        x = [2 * random.random() - 1]
                        y = 2 * x[0]**2 + 1*x[0] - 1
                    elif strategy == 'twodimquadratic':
                        x = [2 * random.random() - 1, 2 * random.random() - 1]
                        y = 2 * x[0]**2 - 3 * x[1]**2 + 4 * x[0] * x[1] + 1*x[0] - 2 * x[1] - 1

                    y *= 1 + 0.1 * (2 * random.random() - 1)  # 10% fluctuation

                    x = map(lambda xs: round(xs, 3), x)
                    y = round(y, 3)
                    print('{} {} {}'.format(i, ' '.join(map(str, x)), y), file=myfile)
