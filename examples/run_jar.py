import subprocess


subprocess.call(['java',
                 '-jar',
                 'target/scala-2.11/mllab-assembly-0.1.0-SNAPSHOT.jar',
                 'reg',
                 'Linear'])
