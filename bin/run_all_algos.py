import argparse
import functools
import subprocess


parser = argparse.ArgumentParser()
parser.add_argument('command',
                    nargs='?',
                    choices=['clf', 'reg', 'clu'],
                    default=['clf', 'reg', 'clu'],
                    help='create data for classification, regression and/or clustering')
parser.add_argument("--format",
                    default='png',
                    choices=['pdf', 'png'],
                    help='output format for figures')
parser.add_argument("--output",
                    default='plots',
                    help='output directory for figures')
args = parser.parse_args()

algoDict = {'clf': {'DecisionTree': [{'dataType': 'nonlinear', 'hyper': '', 'suffix': 'nonlinear_bad'},
                                     {'dataType': 'nonlinear', 'hyper': 'depth=4', 'suffix': 'nonlinear'},
                                    ],
                    'BoostedDecisionTree': [{'dataType': 'nonlinear', 'hyper': 'depth=4, n_estimators=100', 'suffix': 'nonlinear'},
                                           ],
                    'RandomForest': [{'dataType': 'nonlinear', 'hyper': 'depth=3, n_estimators=100, depth=3', 'suffix': 'nonlinear'},
                                    ],
                    'kNN': [{'dataType': 'nonlinear', 'hyper': '', 'suffix': 'nonlinear'},
                           ],
                    'LogisticRegression': [{'dataType': 'linear', 'hyper': '', 'suffix': 'linear'},
                                           {'dataType': 'nonlinear', 'hyper': 'degree=2', 'suffix': 'nonlinear'},
                                          ],
                    'NaiveBayes': [{'dataType': 'linear', 'hyper': '', 'suffix': 'linear'},
                                   {'dataType': 'nonlinear', 'hyper': 'degree=2', 'suffix': 'nonlinear'},
                                  ],
                    'NeuralNetwork': [{'dataType': 'nonlinear', 'hyper': '', 'suffix': 'nonlinear_fail'},
                                      {'dataType': 'nonlinear', 'hyper': 'layers=List(2, 16, 2)', 'suffix': 'nonlinear'},
                                     ],
                    'Perceptron': [{'dataType': 'linear', 'hyper': '', 'suffix': 'linear'},
                                   {'dataType': 'nonlinear', 'hyper': 'degree=2', 'suffix': 'nonlinear'},
                                  ],
                    'Random': [{'dataType': 'linear', 'hyper': '', 'suffix': 'linear'},
                              ],
                    'SVM': [],
                   },
            'reg': {'Bayes': [{'dataType': 'linear', 'hyper': '', 'suffix': 'linear'},
                              {'dataType': 'nonlinear', 'hyper': 'degree=3', 'suffix': 'nonlinear'},
                             ],
                    'DecisionTree': [{'dataType': 'nonlinear', 'hyper': 'depth=3', 'suffix': 'nonlinear_bad'},
                                     {'dataType': 'nonlinear', 'hyper': 'depth=6', 'suffix': 'nonlinear'},
                                    ],
                    'kNN': [{'dataType': 'nonlinear', 'hyper': 'k=40', 'suffix': 'nonlinear'},
                           ],
                    'Linear': [{'dataType': 'linear', 'hyper': '', 'suffix': 'linear'},
                               {'dataType': 'nonlinear', 'hyper': 'degree=3', 'suffix': 'nonlinear'},
                              ],
                    'NeuralNetwork': [{'dataType': 'linear', 'hyper': '', 'suffix': 'linear'},
                                      {'dataType': 'nonlinear', 'hyper': '', 'suffix': 'nonlinear'},
                                     ],
                    'Random': [{'dataType': 'linear', 'hyper': '', 'suffix': 'linear'},
                              ],
                   },
            'clu': {'kMeans': [{'dataType': 'linear', 'hyper': 'k=3', 'suffix': 'three'},
                               {'dataType': 'nonlinear', 'hyper': 'k=5', 'suffix': 'five'},
                              ],
                    'Random': [{'dataType': 'linear', 'hyper': '', 'suffix': 'linear'},
                              ],
                   }
           }

dataDict = {'linear': {'input': 'src/test/resources'},
            'nonlinear': {'input': 'data'}}

for task, algorithms in algoDict.items():
    if task not in args.command:
        continue
    for algo, runList in algorithms.items():
        for runSettings in runList:
            command = ['java', '-jar', 'target/scala-2.11/mllab-assembly-0.1.0-SNAPSHOT.jar',
                       '--' + task, algo,
                       '--input', dataDict[runSettings['dataType']]['input'],
                       '--suffix', runSettings['suffix'],
                       '--output', args.output,
                       '--format', args.format]
            if runSettings['hyper']:
                command.extend(['--hyper', runSettings['hyper']])
            print(functools.reduce(lambda s, t: ' '.join([s, t]), command))
            subprocess.call(command)
