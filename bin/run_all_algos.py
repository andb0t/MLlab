import argparse
import subprocess


parser = argparse.ArgumentParser()
parser.add_argument('command',
                    nargs='?',
                    choices=['clf', 'reg', 'clu'],
                    default=['clf', 'reg', 'clu'],
                    help='create data for classification, regression and/or clustering')
parser.add_argument("--format",
                    default='pdf',
                    choices=['pdf', 'png'],
                    help='output format for figures')
args = parser.parse_args()

algoDict = {'clf': ['DecisionTree', 'kNN', 'LogisticRegression', 'NaiveBayes',
                    'NeuralNetwork', 'Perceptron', 'Random', 'SVM'],
            'reg': ['Bayes', 'DecisionTree', 'kNN', 'Linear', 'NeuralNetwork', 'Random'],
            'clu': ['kMeans', 'Random']}

datasets = {'linear': {'input': 'src/test/resources'},
            'nonlinear': {'input': 'data'}}

for task, algorithms in algoDict.items():
    if task not in args.command:
        continue
    for algo in algorithms:
        for suffix, setting in datasets.items():
            print('Now running', task, algo, setting)
            subprocess.call(['java',
                             '-jar',
                             'target/scala-2.11/mllab-assembly-0.1.0-SNAPSHOT.jar',
                             '--' + task, algo,
                             '--input', setting['input'],
                             '--suffix', suffix,
                             '--output', 'plots',  # replace by docs once its ready
                             '--format', args.format])
