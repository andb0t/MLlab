import argparse
import functools
import math
import os
import random


parser = argparse.ArgumentParser()
parser.add_argument('command', nargs='*', choices=['train', 'test'])
args = parser.parse_args()

TARGET_DIR = os.path.dirname(__file__)

sign = functools.partial(math.copysign, 1)

random.seed(1337)

for command in args.command:

    if command == 'train':
        nInstances = 1000
    elif command == 'test':
        nInstances = 1000
    else:
        nInstances = 1000

    filename = os.path.join(TARGET_DIR, command + '.csv')
    print('Creating data in', filename)

    with open(filename, 'w') as myfile:
        print('Index X Y Type', file=myfile)

        for i in range(nInstances):
            label = random.randint(0, 1)
            x = 0
            y = 0
            strategy = 'diagonal'
            if strategy == 'halfs':
                x = -random.random() if label is 0 else random.random()
                y = 2 * random.random() - 1
            elif strategy == 'quarters':
                x = 2 * random.random() - 1
                y = (1 if label == 1 else -1) * sign(x) * random.random()
            elif strategy == 'diagonal':
                x = 2 * random.random() - 1
                if label == 1:
                    y = x + random.random() * (1 - x)
                else:
                    y = random.random() * (1 + x) - 1
            elif strategy == 'circle':
                x = 2 * random.random() - 1
                y = 2 * random.random() - 1
                label = 1 if math.sqrt(x**2 + y**2) < 0.5 else 0
            x = round(x, 2)
            y = round(y, 2)
            print('{} {} {} {}'.format(i, x, y, label), file=myfile)
