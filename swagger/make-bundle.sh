#!/usr/bin/env bash
set -eux
swagger-ui-watcher pos-proxy.yaml --bundle ../server/src/main/resources/swagger-ui/pos-proxy.json
