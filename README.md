# Clojure-aes 
[![Clojars Project](https://img.shields.io/clojars/v/com.griffinscribe/clojure-aes.svg)](https://clojars.org/com.griffinscribe/clojure-aes)

Pure Clojure implementation of AES for 128, 192, and 256 bit keys based on FIPS Publication 197. Encryption and decryption methods currently support an input message of 16 bytes at a time.

## Installation

Leiningen/Boot
Add the following into :dependencies vector in your project.clj file: 

`[com.griffinscribe/clojure-aes "0.1.4"]`


Clojure CLI/deps.edn

`com.griffinscribe/clojure-aes {:mvn/version "0.1.4"}`

Gradle

`compile 'com.griffinscribe:clojure-aes:0.1.4'`

Maven

 `<dependency>
  <groupId>com.griffinscribe</groupId>
  <artifactId>clojure-aes</artifactId>
  <version>0.1.4</version>
</dependency>`

Command-line access:

Clone the repo, and `cd` into the root directory of the project

If there is no jar file located at ./target/uberjar/clojure-aes-0.1.4-standalone.jar,
dowload leiningen (https://github.com/technomancy/leiningen), run `lein uberjar`  in the root directory of the project (same directory that the `project.clj` file is located in) to create a jar file. Then proceed with the below-mentioned `java` command

## Usage
` (:require [com.griffinscribe/clojure-aes.core :as gsce])`

Encryption:

`(let [message "1a57bbfeeefc417d203494788f3ba2c8"
       key-256 "629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80c38d25f8fc54c649"
       key-length 256
       result (gsce/encrypt key-256 message key-length)]
       result)`
          
          
Decryption:

 `(let [message "2ace987331c0d3e57479dd7037103028"
        key-256 "629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80c38d25f8fc54c649"
        key-length 256
        result "(gsce/decrypt key-256 message 256)]
        result)`
        

Command line usage:
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

Additional tests are accessible in https://github.com/GriffinScribe-LLC/clojure-aes/blob/main/test/clojure_aes/core_test.clj
All major functions/units are individually tested, and integration test exists at the end of that file.
To run the unit tests, install leiningen (See installation section of readme). Then go to the root directory of the project, and run `lein test`.

## API Documentation
https://cljdoc.org/d/com.griffinscribe/clojure-aes/0.1.4/api/clojure-aes

## Resources used
https://csrc.nist.gov/csrc/media/publications/fips/197/final/documents/fips-197.pdf

Consulted `Introduction to Cryptography with Coding Theory` 2E, 
pages 151-161 by Wade Trappe and Lawrence C. Washington.


## License

Copyright © 2021 GriffinScribe, LLC

Distributed under the Eclipse Public License, the same as Clojure.


