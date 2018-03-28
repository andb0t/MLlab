import argparse
import functools
import math
import os
import random

import numpy as np


parser = argparse.ArgumentParser()
parser.add_argument("--clf",
                    default=None,
                    choices=['halfs', 'quarters', 'diagonal', 'circle',
                             'ellipse', 'circles', 'shifteddiagonal',
                             'bernoulli', 'multinomial'],
                    help='create classification data with specified shape')
parser.add_argument("--reg",
                    default=None,
                    choices=['linear', 'twodimlinear', 'multidimlinear',
                             'quadratic', 'twodimquadratic', 'cubic',
                             'twodimcubic'],
                    help='create regression data with specified shape')

args = parser.parse_args()

TARGET_DIR = os.path.join(os.path.dirname(__file__), '../src/test/resources')
# TARGET_DIR = os.path.join(os.path.dirname(__file__), '../data')

sign = functools.partial(math.copysign, 1)


def generate_clf_point(strategy):
    while True:
        label = 0
        x = [0, 0]
        if strategy == 'halfs':
            label = random.randint(0, 1)
            x0 = -random.random() if label is 0 else random.random()
            x1 = 2 * random.random() - 1
            x = [x0, x1]
        elif strategy == 'quarters':
            x0 = 2 * random.random() - 1
            x1 = 2 * random.random() - 1
            x = [x0, x1]
            label = 0 if x0 * x1 > 0 else 1
        elif strategy == 'diagonal':
            x0 = 2 * random.random() - 1
            x1 = 2 * random.random() - 1
            x = [x0, x1]
            label = 0 if x0 - x1 > 0 else 1
        elif strategy == 'shifteddiagonal':
            x0 = 2 * random.random() - 1
            x1 = 2 * random.random() - 1
            x = [x0, x1]
            label = 0 if x0 - x1 + 0.5 > 0 else 1
        elif strategy == 'circle':
            x0 = 2 * random.random() - 1
            x1 = 2 * random.random() - 1
            x = [x0, x1]
            label = 1 if math.sqrt(x0**2 + x1**2) < 0.5 else 0
        elif strategy == 'ellipse':
            x0 = 2 * random.random() - 1
            x1 = 2 * random.random() - 1
            x = [x0, x1]
            x0Center = x0 - 0.2
            x1Center = x1 + 0.3
            rx0 = 0.75
            rx1 = 0.5
            label = 1 if math.sqrt((x0Center/rx0)**2 + (x1Center/rx1)**2) < 1 else 0
        elif strategy == 'circles':
            x0 = 2 * random.random() - 1
            x1 = 2 * random.random() - 1
            x = [x0, x1]
            d = 0.5
            r = 0.25
            part_of_circle = \
                math.sqrt((x0-d)**2 + (x1-d)**2) < r or \
                math.sqrt((x0+d)**2 + (x1+d)**2) < r or \
                math.sqrt((x0-d)**2 + (x1+d)**2) < r or \
                math.sqrt((x0+d)**2 + (x1-d)**2) < r
            label = 1 if part_of_circle else 0
        elif strategy == 'bernoulli':
            x = [random.randint(0, 1) for _ in range(5)]
            label = 1 if (sum(x) > 2) else 0
        elif strategy == 'multinomial':
            nFeatures = 10
            nTotalHistEntries = 10
            label = random.randint(0, 1)
            if label == 0:
                sample = np.random.normal(loc=2.0, scale=3.0, size=nTotalHistEntries)
            else:
                sample = np.random.normal(loc=7.0, scale=4.0, size=nTotalHistEntries)
            hist = np.histogram(sample, bins=nFeatures, range=[0, 10])
            x = hist[0]
        else:
            raise NotImplementedError('this shape is not implemented for clf')

        x = list(map(lambda xs: round(xs, 2), x))

        yield (x, label)


def generate_reg_point(strategy):
    while True:
        x = 0
        y = 0
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
        elif strategy == 'twodimcubic':
            x = [2 * random.random() - 1, 2 * random.random() - 1]
            y = -x[0]**3 + 2 * x[1]**3 + 2 * x[0]**2 - 3 * x[1]**2 + 1*x[0] - 2 * x[1] - 1
        elif strategy == 'twodimquadratic':
            x = [2 * random.random() - 1, 2 * random.random() - 1]
            y = 2 * x[0]**2 - 3 * x[1]**2 + 4 * x[0] * x[1] + 1*x[0] - 2 * x[1] - 1
        elif strategy == 'cubic':
            x = [2 * random.random() - 1]
            y = -2 * x[0]**3 + 2 * x[0]**2 + 1*x[0] - 1
        else:
            raise NotImplementedError('this shape is not implemented for reg')

        mu, sigma = 0, 0.3  # mean and standard deviation
        y += random.gauss(mu, sigma)  # gaussian noise

        x = list(map(lambda xs: round(xs, 3), x))
        y = round(y, 3)

        yield (x, y)


if args.clf is not None:
    random.seed(1337)
    nInstances = {'train': 1000, 'test': 500}
    for command in ['train', 'test']:

        filename = os.path.join(TARGET_DIR, 'clf_' + command + '.csv')
        print('Creating data in', filename)
        print('Shape:', args.clf)
        print('Example:', next(generate_clf_point(args.clf)))

        with open(filename, 'w') as myfile:

            print('Index X Y Type', file=myfile)

            for i in range(nInstances[command]):
                x, label = next(generate_clf_point(args.clf))
                print('{} {} {}'.format(i, ' '.join(map(str, x)), label), file=myfile)

if args.reg is not None:
    random.seed(1337)
    nInstances = {'train': 1000, 'test': 100}
    for command in ['train', 'test']:

        filename = os.path.join(TARGET_DIR, 'reg_' + command + '.csv')
        print('Creating data in', filename)
        print('Shape:', args.reg)
        print('Example:', next(generate_reg_point(args.reg)))

        with open(filename, 'w') as myfile:

            print('Index X Y', file=myfile)

            for i in range(nInstances[command]):
                x, y = next(generate_reg_point(args.reg))
                print('{} {} {}'.format(i, ' '.join(map(str, x)), y), file=myfile)

if args.clf is None and args.reg is None:
    print('Nothing to do.')
else:
    print('Done.')
