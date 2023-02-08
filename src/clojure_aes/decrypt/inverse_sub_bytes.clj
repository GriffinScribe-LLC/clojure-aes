(ns clojure-aes.decrypt.inverse-sub-bytes
  (:require [clojure-aes.constants :as c]
            [clojure-aes.utils :as utils]))

(defn inv-subWord
  "Takes a four-byte input word and substitutes each byte in that word with its appropriate value from the inverse S-Box."
  [word]
  (let [extract-bytes (fn [word mask shift]
                        (bit-shift-right
                         (bit-and mask word) shift))
        byte1 (extract-bytes word 0xff000000 24)
        byte2 (extract-bytes word 0x00ff0000 16)
        byte3 (extract-bytes word 0x0000ff00 8)
        byte4 (extract-bytes word 0x000000ff 0)
        top-bits (fn [byte]
                   (bit-shift-right byte 4))
        bottom-bits (fn [byte]
                      (bit-and byte 0xf))
        s-byte (fn [row col]
                 (-> c/inv-sbox
                     (nth row)
                     (nth col)))
        transform-byte (fn [byte word-loc]
                         (-> (s-byte (top-bits byte)
                                     (bottom-bits byte))
                             (bit-shift-left (* 4 word-loc))))
        new-word (+ (transform-byte byte1 6)
                    (transform-byte byte2 4)
                    (transform-byte byte3 2)
                    (transform-byte byte4 0))]
    new-word))

(defn inv-sub-bytes
  "This transformation extracts each word from the State, and calculates the lookup for each byte within each word from inv-sbox"
  [[row1 row2 row3 row4] round-num]
  (let [calculate-row (fn -row-calcuate [row]
                        (into []
                              (for [word row]
                                (bit-and 0xff
                                         (inv-subWord word))))) 

        new-row1 (calculate-row row1)
        new-row2 (calculate-row row2)
        new-row3 (calculate-row row3)
        new-row4 (calculate-row row4)
        new-state [new-row1 new-row2 new-row3 new-row4]]
    (utils/print-array new-state "after subBytes" round-num)
    (utils/debug-aes round-num "s_box after subBytes:  " new-state)
    new-state))
