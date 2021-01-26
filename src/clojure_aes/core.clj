(ns clojure-aes.core
  (:require [clojure-aes.constants :as c]
            [clojure-aes.encrypt.shift-row :as sr]
            [clojure-aes.utils :as utils]
            [clojure-aes.shared :as shared]
            [clojure-aes.encrypt.sub-bytes :as sb]
            [clojure-aes.encrypt.cipher-core :as cc]
            [clojure-aes.decrypt.inverse-cipher-core :as icc]))

(defn key-expansion
  "Takes an initial 4x4 byte-array `cipher-key` and expands it to have 44, 52, or 60 columns, based on the `num-bits` passed in. Allowed values for `num-bits` is 128, 192, or 256."
  [cipher-key num-bits]
  (let [expanded-key (atom [])
        Nb 4
        nk  (shared/number-of-32bit-words-in-key num-bits)
        num-rounds (shared/number-of-rounds num-bits)
        counter (atom nk)
        temp (atom [])
        add-at-index (fn [index val-vec]
                       (reset! expanded-key
                               (assoc @expanded-key index val-vec)))
        sub-vec (fn [offset]
                  (vec (take 4 (drop offset cipher-key))))
        initialize-expanded (fn []
                              (vec
                               (for [x (range nk)]
                                 (shared/combine-four-bytes
                                  (sub-vec (* x 4))))))]
    (reset! expanded-key (initialize-expanded))
    (while (< @counter (* Nb (inc num-rounds)))
      (reset! temp (nth @expanded-key (dec @counter)))
      (if (= 0 (mod @counter nk))
        (reset! temp (bit-xor
                      (sb/subWord
                       (sr/rotWord @temp 1))
                      (nth c/roundConstant  (/ @counter nk))))
        (when (and (> nk 6)
                   (= 4 (mod @counter nk)))
          (reset! temp (sb/subWord @temp))))
      
      (add-at-index @counter
                    (bit-xor
                     (nth @expanded-key (- @counter nk))
                     @temp))
      (swap! counter inc))
    @expanded-key))

(defn encrypt
  "Separates the incoming `bit-key` and `message` into byte arrays, and encrypts the `message` using the `bit-key`."
  [bit-key message bits]
  (let [secret-key (utils/break-message-to-bytes bit-key)
        plain-text (utils/break-message-to-bytes message)
        split-plain-text [(vec (take 4 plain-text))
                          (vec (take 4 (drop 4 plain-text)))
                          (vec (take 4 (drop 8 plain-text)))
                          (vec (take 4 (drop 12 plain-text)))]     
        expanded-key (key-expansion secret-key bits)]    
    (apply str (utils/byte-to-hex-string
                (flatten
                 (cc/cipher
                  split-plain-text
                  expanded-key
                  bits
                  secret-key))))))

(defn decrypt
  "Separates the incoming `bit-key` and `message` into byte arrays, and decrypts the `message` using the `bit-key`."
  [bit-key message bits]
  (let [secret-key (utils/break-message-to-bytes bit-key)
        cipher-text (utils/break-message-to-bytes message)
        split-cipher-text [(vec (take 4 cipher-text))
                           (vec (take 4 (drop 4 cipher-text)))
                           (vec (take 4 (drop 8 cipher-text)))
                           (vec (take 4 (drop 12 cipher-text)))]
        expanded-key (key-expansion secret-key bits)]
    (apply str (utils/byte-to-hex-string
                (flatten
                 (icc/inv-cipher split-cipher-text
                                 expanded-key
                                 bits
                                 secret-key))))))
















