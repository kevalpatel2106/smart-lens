#!/usr/bin/env bash

set -e

if [ "$COMPONENT" == "instrumentation" ]; then
    bash <(curl -s https://codecov.io/bash)
fi