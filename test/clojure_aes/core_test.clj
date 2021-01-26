(ns clojure-aes.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [clojure-aes.core :as core]
            [clojure-aes.constants :as c]
            [clojure-aes.encrypt.shift-row :as sr]
            [clojure-aes.add-round-key :as ark]
            [clojure-aes.shared :as shared]
            [clojure-aes.encrypt.mix-columns :as mc]
            [clojure-aes.encrypt.sub-bytes :as sb]
            [clojure-aes.encrypt.cipher-core :as cipher-core]
            [clojure-aes.utils :as utils]
            [clojure-aes.decrypt.inverse-shift-rows :as isr]
            [clojure-aes.decrypt.inverse-mix-columns :as imc]
            [clojure-aes.decrypt.inverse-sub-bytes :as isb]))

(deftest combine-four-bytes
  (testing "Test that a vector of four bytes is transformed correctly into a word"
    (let [vec-to-combine [ 0x28 0xae 0xd2 0xa6]
          desired  0x28aed2a6]
      (is (= desired (shared/combine-four-bytes vec-to-combine) )
          "Failed to combine the bytes accurately"))))

(deftest separate-four-bytes
  (testing "Test that a word is transformed correctly to a vector of four bytes"
    (let [word-to-separate  0x28aed2a6
          desired  [ 0x28 0xae 0xd2 0xa6]]
      (is (= desired (shared/separate-four-bytes word-to-separate))
          "Failed to separate the bytes accurately"))))

(deftest matrix-transposition-test
  (testing "Matrix transposition works"
    (let [state c/shift-state
          expected-state 
          [[212 191 93 48]
           [224 180 82 174]
           [184 65 17 241]
           [30 39 152 229]]]
      (is (= expected-state (utils/matrix-transposition state) )
          "Matrix failed to transpose correctly"))))

(deftest ffadd-test
  (testing "finite field addition"
    (let [byte1 0x57
          byte2 0x83
          expected 0xd4]
      (is (= expected (shared/ffAdd byte1 byte2))
          "Finite field addition failed to xor correctly"))))

(deftest xtime-test
  (testing "Computes the doubling of a byte value mod an irreducible polynomial"
    (let [initial-byte 0x57
          second-byte 0xae
          third-byte 0x47
          fourth-byte 0x8e
          fifth-byte 0x07]
      (is (= second-byte (shared/xtime initial-byte))
          "Xtime failed for first byte, with msb not set")
      (is (= third-byte (shared/xtime second-byte))
          "Xtime failed for second byte, with msb set")
      (is (= fourth-byte (shared/xtime third-byte))
          "Xtime failed for third byte, with msb not set")
      (is (= fifth-byte (shared/xtime fourth-byte))
          "Xtime failed for fourth byte, with msb set"))))

(deftest ffmultiply-test
  (testing "finite field multiplication"
    (let [byte1 0x57
          byte2 0x13
          expected 0xfe]
      (is (= expected (shared/ffmultiply byte1 byte2))
          "Finite field multiplication failed to xor correctly"))))

(deftest rotword-test
  (testing "Tests that the output word is formed correctly by rotating bytes in the input word "
    (let [initial-word1 0x09cf4f3c
          initial-word2 0x2a6c7605
          expected-rotated-1-word1 0xcf4f3c09
          expected-rotated-1-word2 0x6c76052a
          expected-rotated-2-word1 0x4f3c09cf
          expected-rotated-2-word2 0x76052a6c
          expected-rotated-3-word1 0x3c09cf4f
          expected-rotated-3-word2 0x052a6c76]
      (is (= initial-word1 (sr/rotWord initial-word1 0))
          "Expected word1 shouldn't have any rotations")
      (is (= initial-word2 (sr/rotWord initial-word2 0))
          "Expected word2 shouldn't have any rotations")
      (is (= initial-word1 (sr/rotWord initial-word1 -4))
          "Expected word1 shouldn't have any rotations")
      (is (= initial-word2 (sr/rotWord initial-word2 -4))
          "Expected word2 shouldn't have any rotations")
      (is (= expected-rotated-1-word1 (sr/rotWord initial-word1 1))
          "Expected word1 is not rotated correctly with 1 rotation")
      (is (= expected-rotated-1-word2 (sr/rotWord initial-word2 1))
          "Expected word2 is not rotated correctly with 1 rotation")
      (is (= expected-rotated-2-word1 (sr/rotWord initial-word1 2))
          "Expected word2 is not rotated correctly with 2 rotations")
      (is (= expected-rotated-2-word2 (sr/rotWord initial-word2 2))
          "Expected word2 is not rotated correctly with 2 rotations")
      (is (= expected-rotated-3-word1 (sr/rotWord initial-word1 3))
          "Expected word2 is not rotated correctly with 3 rotations")
      (is (= expected-rotated-3-word2 (sr/rotWord initial-word2 3))
          "Expected word2 is not rotated correctly with 3 rotations"))))

(deftest shift-row-test
  (testing "verifies that the shift-row function works as specified in the specs of FIBS 197"
    (let [initial-state [[1 2 3 4]
                         [5 6 7 8]
                         [9 10 11 12]
                         [13 14 15 16]]
          expected-vector [[1 2 3 4]
                           [6 7 8 5]
                           [11 12 9 10]
                           [16 13 14 15]]
          result-state (sr/shift-rows initial-state 0)]
      (is (= expected-vector result-state )
          "Failed: bytes were not shifted accurately"))))

(deftest subWord-test
  (testing "Takes a four-byte input word and substitutes each byte in that word with its appropriate value from the S-Box."
    (let [word1 0x00102030
          expected-word1 0x63cab704
          word2 0x40506070
          expected-word2 0x0953d051
          word3 0x8090a0b0
          expected-word3 0xcd60e0e7
          word4 0xc0d0e0f0
          expected-word4 0xba70e18c]
      (is (= expected-word1 (sb/subWord word1))
          "Word1 was not transformed correctly by the sbox transformation.")
      (is (= expected-word2 (sb/subWord word2))
          "Word2 was not transformed correctly by the sbox transformation.")
      (is (= expected-word3 (sb/subWord word3))
          "Word3 was not transformed correctly by the sbox transformation.")
      (is (= expected-word4 (sb/subWord word4))
          "Word4 was not transformed correctly by the sbox transformation."))))

(deftest subBytes-test
  (testing "Test that the subBytes transformation accurately transforms the state"
    (is (= c/sub-state (sb/subBytes c/state 0))
        "SubBytes function failed to transform the state accurately.")))

(deftest mixColumns-test
  (testing "Test that the mixColumns transformation accurately transforms the state (shift-state)"
    (is (= c/mix-state (mc/mixColumns c/shift-state 0))
        "mixColumns function failed to transform the state accurately.")))

(deftest keyExpansion-test
  (testing "Ensures that the key expands correctly"
    (let [expected-key-128 c/initial-key-expanded-128
          num-bits-128 128
          expected-key-192 c/initial-key-expanded-192
          num-bits-192 192
          expected-key-256 c/initial-key-expanded-256
          num-bits-256 256]
      (is (= expected-key-128 (core/key-expansion c/initial-key-128 num-bits-128))
          "Expanded key with 128 bits is not accurate")
      (is (= expected-key-192 (core/key-expansion c/initial-key-192 num-bits-192))
          "Expanded key with 192 bits is not accurate")
      (is (= expected-key-256 (core/key-expansion c/initial-key-256 num-bits-256))
          "Expanded key with 256 bits is not accurate"))))

#_(deftest addRoundKey-test
  (testing "Test that the addRoundKey transformation accurately transforms the state (mix-state)"
    (is (= c/round-state
           (ark/addRoundKey
            (utils/matrix-transposition c/mix-state)
            c/initial-key-expanded-128
            4))
        "AddRoundKey function for 128 bits failed to transform the state accurately.")))

(deftest addRoundKey-round0-test
  (testing "Test that the addRoundKey transformation accurately transforms the state on start 0"
    (let [secret-key [0x2b 0x7e 0x15 0x16
                      0x28 0xae 0xd2 0xa6
                      0xab 0xf7 0x15 0x88
                      0x09 0xcf 0x4f 0x3c]
          num-bits 128
          key-expanded (core/key-expansion secret-key num-bits)
          plain-text [[0x32, 0x43, 0xf6, 0xa8]
                      [0x88, 0x5a, 0x30, 0x8d]
                      [0x31, 0x31, 0x98, 0xa2]
                      [0xe0, 0x37, 0x07, 0x34] ]
          expected [[0x19 0xa0 0x9a 0xe9]
                    [0x3d 0xf4 0xc6 0xf8]
                    [0xe3 0xe2 0x8d 0x48]
                    [0xbe 0x2b 0x2a 0x8]] ]
      (is (= expected
             (ark/addRoundKey
              (utils/matrix-transposition plain-text)
              key-expanded
              0))
          "AddRoundKey function for 128 bits failed to transform the state accurately on the zeroeth round."))))


(deftest cipher-test
  (testing "Integration test of cipher function, which encrypts the input by various calls to subBytes, shiftRows, mixColumns, and addRoundKey"
    (let [ num-bits-128 128
          input-128-2 [[0x32, 0x43, 0xf6, 0xa8]
                     [0x88, 0x5a, 0x30, 0x8d]
                     [0x31, 0x31, 0x98, 0xa2]
                     [0xe0, 0x37, 0x07, 0x34] ]
          expected-128-2 [[0x39, 0x25, 0x84, 0x1d]
                        [0x02, 0xdc, 0x09, 0xfb]
                        [0xdc, 0x11, 0x85, 0x97]
                          [0x19, 0x6a, 0x0b, 0x32]]
          key-2 [0x2b, 0x7e, 0x15, 0x16, 0x28, 0xae, 0xd2, 0xa6,
                 0xab, 0xf7, 0x15, 0x88, 0x09, 0xcf, 0x4f, 0x3c]
          expanded-key-2 (core/key-expansion key-2 num-bits-128)
          
          plain-text [[0x00 0x11 0x22 0x33]
                     [0x44 0x55 0x66 0x77]
                     [0x88 0x99 0xaa 0xbb]
                     [0xcc 0xdd 0xee 0xff]]
          
          key [0x00 0x01 0x02 0x03
               0x04 0x05 0x06 0x07
               0x08 0x09 0x0a 0x0b
               0x0c 0x0d 0x0e 0x0f]
          expanded-key (core/key-expansion key num-bits-128)
          expected [[0x69 0xc4 0xe0 0xd8]
                    [0x6a 0x7b 0x04 0x30]
                    [0xd8 0xcd 0xb7 0x80]
                    [0x70 0xb4 0xc5 0x5a]]]
      (is (= expected
             (cipher-core/cipher plain-text
                          expanded-key
                          num-bits-128
                          key))
          "Input text was not encrypted per AES standard for 128 bits.")
      (is (= expected-128-2
             (cipher-core/cipher input-128-2
                          expanded-key-2
                          num-bits-128
                          key-2))
          "Input text was not encrypted per AES standard for 128 bits."))))

(deftest cipher-128-test
  (testing "Test that aes algorithm encrypts message with 128-bit key correctly"
    (let [message "2d2eac399c50fab7b0c86e4b94b61fac"
          key-128 "629cdd27509b3d2fe2adb7ec7ff0e6cf"
          expected "d8d1888cfced26f85ea422d4fd56e0ef"]
      (is (= expected (core/encrypt key-128 message 128))
          "Message with 128-bit key failed to encrypt correctly"))))

(deftest cipher-192-test
  (testing "Test that aes algorithm encrypts message with 192-bit key correctly"
    (let [message "526b00c38662e0c58a49ce6ccc83fe9a"
          key-192 "629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80"
          expected "a953e8cf02e51d6f42e30be9910c9cde"]
      (is (= expected (core/encrypt key-192 message 192))
          "Message with 192-bit key failed to encrypt correctly"))))

(deftest cipher-256-test
  (testing "Test that aes algorithm encrypts message with 256-bit key correctly"
    (let [message "1a57bbfeeefc417d203494788f3ba2c8"
          key-256 "629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80c38d25f8fc54c649"
          expected "2ace987331c0d3e57479dd7037103028"]
      (is (= expected (core/encrypt key-256 message 256))
          "Message with 256-bit key failed to encrypt correctly"))))

(deftest inv-shiftRows-test
  (testing "Test that the inv-shiftRows transformation accurately transforms the state (sub-state)"
    (let [initial-state [[212 224 184 30]
                         [39 191 180 65]
                         [17 152 93 82]
                         [174 241 229 48]]
          expected-state [[212 224 184 30]
                          [65 39 191 180]
                          [93 82 17 152]
                          [241 229 48 174]]]
      (is (= expected-state (isr/inv-shift-rows initial-state 0))
          "inv-shiftRows function failed to transform the state accurately."))))

(deftest inv-sub-word-test
  (testing "Takes a four-byte input word and substitutes each byte in that word with its appropriate value from the inv S-Box."
    (let [word1 0x63cab704
          expected-word1  0x00102030
          word2 0x0953d051
          expected-word2  0x40506070
          word3 0xcd60e0e7
          expected-word3  0x8090a0b0
          word4 0xba70e18c
          expected-word4  0xc0d0e0f0 ]
      (is (= expected-word1 (isb/inv-subWord word1))
          "Word1 was not transformed correctly by the inv-sbox transformation.")
      (is (= expected-word2 (isb/inv-subWord word2))
          "Word2 was not transformed correctly by the inv-sbox transformation.")
      (is (= expected-word3 (isb/inv-subWord word3))
          "Word3 was not transformed correctly by the inv-sbox transformation.")
      (is (= expected-word4 (isb/inv-subWord word4))
          "Word4 was not transformed correctly by the inv-sbox transformation."))))

(deftest inv-sub-bytes-test
  (testing "Test that the inv-sub-bytes transformation accurately transforms the state"
    (is (= c/sub-state (sb/subBytes c/state 0))
        "inv-sub-bytes function failed to transform the state accurately.")))

(deftest inv-mixColumns-test
  (testing "Test that the inv-mixColumns transformation accurately transforms the state"
    (is (= c/shift-state (imc/inv-mixColumns c/mix-state 0))
        "inv-mixColumns function failed to transform the state accurately.")))

(deftest inv-cipher-128-test
  (testing "Test that aes algorithm decrypts message with 128-bit key correctly"
    (let [message "0f2d64db78cdaec2a710554fb436df40"
          key-128 "629cdd27509b3d2fe2adb7ec7ff0e6cf"
          expected "ce711b1dd332f008cea7445507ab0738"]
      (is (= expected (core/decrypt key-128 message 128))
          "Message with 128-bit key failed to decrypt correctly"))))

(deftest inv-cipher-192-test
  (testing "Test that aes algorithm encrypts message with 192-bit key correctly"
    (let [message "4b75c65e5e26f261afb18955c8cce7bf"
          key-192 "629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80"
          expected "c8a08456e9211688ea1a8d0cfb0656bb"]
      (is (= expected (core/decrypt key-192 message 192))
          "Message with 192-bit key failed to decrypt correctly"))))

(deftest inv-cipher-256-test
  (testing "Test that aes algorithm encrypts message with 256-bit key correctly"
    (let [message "2db70585ca50215c2a449261542462dd"
          key-256 "629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80c38d25f8fc54c649"
          expected "fce6c0a79b4787d0c10f7254fa55b045"]
      (is (= expected (core/decrypt key-256 message 256))
          "Message with 256-bit key failed to decrypt correctly"))))

;;; appendix c tests taken from https://cs465.byu.edu/static/pubs/fips-197.pdf
(deftest cipher-128-test-appendix-c
  (testing "Test that aes algorithm encrypts message with 128-bit key correctly from appendix c"
    (let [message "00112233445566778899aabbccddeeff"
          key-128 "000102030405060708090a0b0c0d0e0f"
          expected "69c4e0d86a7b0430d8cdb78070b4c55a"]
      (is (= expected (core/encrypt key-128 message 128))
          "Message with 128-bit key failed to encrypt correctly from appendix c"))))

(deftest inv-cipher-128-test-appendix-c
  (testing "Test that aes algorithm decrypts message with 128-bit key correctly from appendix c"
    (let [message "69c4e0d86a7b0430d8cdb78070b4c55a"
          key-128 "000102030405060708090a0b0c0d0e0f"
          expected "00112233445566778899aabbccddeeff"]
      (is (= expected (core/decrypt key-128 message 128))
          "Message with 128-bit key failed to decrypt correctly from appendix c"))))

(deftest cipher-192-test-appendix-c
  (testing "Test that aes algorithm encrypts message with 192-bit key correctly from appendix c"
    (let [message "526b00c38662e0c58a49ce6ccc83fe9a"
          key-192 "629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80"
          expected "a953e8cf02e51d6f42e30be9910c9cde"]
      (is (= expected (core/encrypt key-192 message 192))
          "Message with 192-bit key failed to encrypt correctly from appendix c"))))

(deftest inv-cipher-192-test-appendix-c
  (testing "Test that aes algorithm encrypts message with 192-bit key correctly from appendix c"
    (let [message "a953e8cf02e51d6f42e30be9910c9cde"
          key-192 "629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80"
          expected "526b00c38662e0c58a49ce6ccc83fe9a"]
      (is (= expected (core/decrypt key-192 message 192))
          "Message with 192-bit key failed to decrypt correctly from appendix c"))))

(deftest cipher-256-test-appendix-c
  (testing "Test that aes algorithm encrypts message with 256-bit key correctly from appendix c"
    (let [message "1a57bbfeeefc417d203494788f3ba2c8"
          key-256 "629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80c38d25f8fc54c649"
          expected "2ace987331c0d3e57479dd7037103028"]
      (is (= expected (core/encrypt key-256 message 256))
          "Message with 256-bit key failed to encrypt correctly from appendix c"))))

(deftest inv-cipher-256-test-appendix-c
  (testing "Test that aes algorithm encrypts message with 256-bit key correctly from appendix c"
    (let [message "2ace987331c0d3e57479dd7037103028"
          key-256 "629cdd27509b3d2fe2adb7ec7ff0e6cf4a6c24f4c5ebbf80c38d25f8fc54c649"
          expected "1a57bbfeeefc417d203494788f3ba2c8"]
      (is (= expected (core/decrypt key-256 message 256))
          "Message with 256-bit key failed to decrypt correctly from appendix c"))))


