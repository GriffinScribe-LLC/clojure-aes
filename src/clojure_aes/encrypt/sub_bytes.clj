(ns clojure-aes.encrypt.sub-bytes
  (:require [clojure-aes.constants :as c]
            [clojure-aes.utils :as utils]))

(defn subWord
  "Takes a four-byte input word and substitutes each byte in that word with its appropriate value from the S-Box."
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
                 (-> c/sbox
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

(defn subBytes
  "Substitutes each byte in the 4x4 state array with its corresponding value from the S-Box."
  [[row1 row2 row3 row4] round-num]
  (let [calculate-row (fn [row]
                        (into []
                              (for [word row]
                                (bit-and 0xff
                                         (subWord word)))))
        new-row1 (calculate-row row1)
        new-row2 (calculate-row row2)
        new-row3 (calculate-row row3)
        new-row4 (calculate-row row4)
        new-state [new-row1 new-row2 new-row3 new-row4]]
                                        ;(utils/print-array new-state "after subBytes" round-num)
    (utils/debug-aes round-num "s_box " (utils/matrix-transposition new-state))
    new-state))
