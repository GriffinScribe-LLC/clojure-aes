# Clojure-aes 
[![Clojars Project](https://img.shields.io/clojars/v/com.griffinscribe/clojure-aes.svg)](https://clojars.org/com.griffinscribe/clojure-aes) 

Pure Clojure implementation of the Advanced Encryption Standard (AES) for encryption and decryption using 128, 192, and 256 bit keys based on the [NIST specification](https://csrc.nist.gov/csrc/media/publications/fips/197/final/documents/fips-197.pdf).

## Installation

Leiningen/Boot

Add the following into `:dependencies` in your project.clj file: 

```clojure
[com.griffinscribe/clojure-aes "0.1.4"]
```


Clojure CLI/deps.edn

```clojure
com.griffinscribe/clojure-aes {:mvn/version "0.1.4"}
```

Gradle

```clojure
compile 'com.griffinscribe:clojure-aes:0.1.4'
```

Maven

 ```java
 <dependency>
  <groupId>com.griffinscribe</groupId>
  <artifactId>clojure-aes</artifactId>
  <version>0.1.4</version>
</dependency>
```

(Version "0.1.5" only available via compilation from source via `lein uberjar`)

Command-line access:

Clone the repo, and `cd` into the root directory of the project

If there is no jar file located at `./target/uberjar/clojure-aes-0.1.4-standalone.jar`,
dowload leiningen (https://github.com/technomancy/leiningen), run `lein uberjar`  in the root directory of the project (same directory that the `project.clj` file is located in) to create a jar file. Then proceed with the below-mentioned `java` command

## Usage

```clojure
(:require [clojure-aes.core :as aes])
```

Encryption:

```clojure
(let [message     "1a57bbfeeefc417d203494788f3ba2c8"
      secret-key  "629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80c38d25f8fc54c649"
      key-length  256
      cipher-text (aes/encrypt secret-key message key-length)]
  cipher-text)
```
          
          
Decryption:

```clojure
(let [cipher-text "2ace987331c0d3e57479dd7037103028"
      secret-key  "629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80c38d25f8fc54c649"
      key-length  256
      message     (aes/decrypt secret-key cipher-text key-length)]
  message)
```
        
              
Version 0.1.4 currently supports an input message of 16 bytes (hex-formatted) at a time.
Version 0.1.5 is unreleased to Clojars, but can be compiled from source, or downloaded as an uberjar from the target directory.

Command line usage:
`cd` into project root, and run the following.

    $ java -jar clojure-aes-0.1.4-standalone.jar [args]
where the args passed in are the secret-key, the message, the key-length and a flag for encryption or decryption.

Example encryption:

```bash
    $ java -jar target/uberjar/clojure-aes-0.1.4-standalone.jar 629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80 526b00c38662e0c58a49ce6ccc83fe9a 192 -e
```
    => a953e8cf02e51d6f42e30be9910c9cde

The first arg is the secret key. The second arg is the message to be encrypted or decrypted. Third arg is the key-length, the third arg indicates whether encryption or decryption will be used.
`-e` indicates that encryption will be used. Use `-d` for decryption.

A 4th arg may optionally be passed as "-v" to indicate if verbose logging should be enabled to display the internal representation of the message in its various transformations through the encryption and decryption functions. Output follows format described in fips-197.pdf.

Example decryption:

```bash
    $ java -jar target/uberjar/clojure-aes-0.1.4-standalone.jar 629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80 a953e8cf02e51d6f42e30be9910c9cde 192 -d
```
    => 526b00c38662e0c58a49ce6ccc83fe9a


## TESTS
This library passes all test cases in Appendix C of https://csrc.nist.gov/csrc/media/publications/fips/197/final/documents/fips-197.pdf,
including encryption for 128, 192, and 256 bits, as well as decryption for keys of 128, 192 and 256 bits.

Additional tests are accessible in https://github.com/GriffinScribe-LLC/clojure-aes/blob/main/test/clojure_aes/core_test.clj
All major functions/units are individually tested, and integration test exists at the end of that file.

To run the unit tests from the command-line, install leiningen (See installation section of readme). Then go to the root directory of the project, and run `lein test`.

To run the tests in the REPL, start up a Clojure REPL in Emacs, navigate to the test file, load it via `ctrl-c ctrl-k` and use the Emacs cider test functionality to run the tests. Use the keystrokes `ctrl-c ctrl-t n` for all tests in the namespace; `ctrl-c ctrl-t t` to run a specific test. Documentation for cider is found at https://docs.cider.mx/cider/testing/running_tests.html

## API Documentation
https://cljdoc.org/d/com.griffinscribe/clojure-aes/0.1.4/api/clojure-aes

## Resources used
https://csrc.nist.gov/csrc/media/publications/fips/197/final/documents/fips-197.pdf

Consulted `Introduction to Cryptography with Coding Theory` 2E, 
pages 151-161 by Wade Trappe and Lawrence C. Washington.


## License

Copyright © 2021-2023 GriffinScribe, LLC. All Rights Reserved.

Distributed under the Eclipse Public License, the same as Clojure.


