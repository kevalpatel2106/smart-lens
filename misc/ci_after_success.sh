#!/usr/bin/env bash

set -e

if [ "$COMPONENT" == "text-max" ]; then
    bash <(curl -s https://codecov.io/bash)
fi