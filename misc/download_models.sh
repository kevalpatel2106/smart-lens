#!/usr/bin/env bash
#curl to download the inception model for TF.
#extract the models in inference/assets
cd app/src/main/assets
mkdir tensorflow
cd assets
curl -L -o ./inception5h.zip https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip
unzip inception5h.zip -d .
rm -rf inception5h.zip
cd ..
cd ..
cd ..
cd ..
cd ..
