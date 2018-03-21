import argparse
import functools
import math
import os
import random


parser = argparse.ArgumentParser()
parser.add_argument("--clf",
                    default=None,
                    choices=['halfs', 'quarters', 'diagonal', 'circle', 'ellipse', 'circles'],
                    help='create classification data with specified shape')
parser.add_argument("--reg",
                    default=None,
                    choices=['linear', 'twodimlinear', 'multidimlinear', 'quadratic', 'twodimquadratic'],
                    help='create regression data with specified shape')

args = parser.parse_args()

TARGET_DIR = os.path.join(os.path.dirname(__file__), '../data')

sign = functools.partial(math.copysign, 1)


def generate_clf_point(strategy):
    while True:
        label = 0
        x = 0
        y = 0
        if strategy == 'halfs':
            label = random.randint(0, 1)
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
        else:
            raise NotImplementedError('this shape is not implemented for clf')

        x = round(x, 2)
        y = round(y, 2)

        yield (x, y, label)


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
        elif strategy == 'twodimquadratic':
            x = [2 * random.random() - 1, 2 * random.random() - 1]
            y = 2 * x[0]**2 - 3 * x[1]**2 + 4 * x[0] * x[1] + 1*x[0] - 2 * x[1] - 1
        else:
            raise NotImplementedError('this shape is not implemented for reg')

        y *= 1 + 0.1 * (2 * random.random() - 1)  # 10% fluctuation

        x = list(map(lambda xs: round(xs, 3), x))
        y = round(y, 3)

        yield (x, y)


if args.clf is not None:
    random.seed(1337)
    for command in ['train', 'test']:

        filename = os.path.join(TARGET_DIR, 'clf_' + command + '.csv')
        print('Creating data in', filename)
        print('Shape:', args.clf)
        print('Example:', next(generate_clf_point(args.clf)))

        with open(filename, 'w') as myfile:

            print('Index X Y Type', file=myfile)

            nInstances = 1000
            for i in range(nInstances):
                x, y, label = next(generate_clf_point(args.clf))
                print('{} {} {} {}'.format(i, x, y, label), file=myfile)

if args.reg is not None:
    random.seed(1337)
    for command in ['train', 'test']:

        filename = os.path.join(TARGET_DIR, 'reg_' + command + '.csv')
        print('Creating data in', filename)
        print('Shape:', args.reg)
        print('Example:', next(generate_reg_point(args.reg)))

        with open(filename, 'w') as myfile:

            print('Index X Y', file=myfile)

            nInstances = 1000
            for i in range(nInstances):
                x, y = next(generate_reg_point(args.reg))
                print('{} {} {}'.format(i, ' '.join(map(str, x)), y), file=myfile)

if args.clf is None and args.reg is None:
    print('Nothing to do.')
else:
    print('Done.')
