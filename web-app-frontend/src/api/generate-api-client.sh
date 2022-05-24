#!/bin/bash

spec_file="__api_spec__.json"

curl http://localhost:5000/v3/api-docs/ > $spec_file

npx openapi-generator-cli generate \
-g typescript-fetch \
-i $spec_file \
-o ./src/api \
-c ./src/api/openapi-generator-config.json \
--api-package apis \
--model-package models \
--type-mappings json="string\|number" \
--language-specific-primitives "string\|number" \
--additional-properties=supportsES6=true,typescriptFourPlus=true


rm $spec_file

# replace GlobalFetch with WindowOrWorkerGlobalScope to avoid ts errors
sed -i 's/GlobalFetch/WindowOrWorkerGlobalScope/' ./src/api/runtime.ts