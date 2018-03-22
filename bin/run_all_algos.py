import argparse
import subprocess


parser = argparse.ArgumentParser()
parser.add_argument('command',
                    nargs='?',
                    choices=['clf', 'reg'],
                    default=['clf', 'reg'],
                    help='create data for classification and/or regression')
args = parser.parse_args()

algoDict = {'clf': ['Random',
                    'kNN',
                    'DecisionTree',
                    'Perceptron',
                    'NeuralNetwork',
                    'SVM',
                    'LogisticRegression'],
            'reg': ['Random',
                    'Linear']}

for task, algorithms in algoDict.items():
    if task not in args.command:
        continue
    for algo in algorithms:
        print('Now running', task, algo)
        subprocess.call(['java',
                         '-jar',
                         'target/scala-2.11/mllab-assembly-0.1.0-SNAPSHOT.jar',
                         task,
                         algo])
