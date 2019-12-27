#!/usr/bin/env bash

echo 'Decrypting encryption key'
openssl aes-256-cbc -K ${ENCRYPTED_CODESIGNING_KEY} -iv ${ENCRYPTED_CODESIGNING_IV} \
-in .travis/gpg/codesigning.asc.enc -out .travis/gpg/codesigning.asc -d
echo 'Decrypted encryption key'

echo 'Importing encryption key'
gpg --fast-import .travis/gpg/codesigning.asc
echo 'Imported encryption key'