# Clojure-aes

Pure Clojure implementation of AES for 128, 192, and 256 bit keys based on FIPS Publication 197. Encryption and decryption methods currently support an input message of 16 bytes at a time.

## Installation
Clone the repo, and `cd` into the root directory of the project

If there is no jar file located at ./target/uberjar/clojure-aes-0.1.0-SNAPSHOT-standalone.jar,
dowload leiningen (https://github.com/technomancy/leiningen), run `lein uberjar`  in the root directory of the project (same directory that the `project.clj` file is located in) to create a jar file. Then proceed with the below-mentioned `java` command

## Usage
`cd` into project root, and run the following.

    $ java -jar clojure-aes-0.1.0-standalone.jar [args]
where the args passed in are the secret-key, the message, the key-length and a flag for encryption or decryption.
Example:

    $ java -jar target/uberjar/clojure-aes-0.1.0-standalone.jar 629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80 526b00c38662e0c58a49ce6ccc83fe9a 192 -e

The first arg is the secret key. The second arg is the message to be encrypted or decrypted. Third arg is the key-length, and the final arg indicates whether encryption or decryption will be used.
`-e` indicates that encryption will be used. Use `-d` for decryption.

## TESTS
This library passes all test cases in Appendix C of https://csrc.nist.gov/csrc/media/publications/fips/197/final/documents/fips-197.pdf,
including encryption for 128, 192, and 256 bits, as well as decryption for keys of 128, 192 and 256 bits.

Additional tests are accessible in `/clojure-aes/test/clojure_aes/core_test.clj`
All major functions/units are individually tested, and integration test exists at the end of that file.
To run the unit tests, install leiningen (See installation section of readme). Then go to the root directory of the project, and run `lein test`.

## Resources used
https://csrc.nist.gov/csrc/media/publications/fips/197/final/documents/fips-197.pdf
Consulted `Introduction to Cryptography with Coding Theory` 2E, pages 151-161 by Wade Trappe and Lawrence C. Washington.


## License

Copyright © 2021 Scott Griffin

Distributed under the Eclipse Public License, the same as Clojure.


