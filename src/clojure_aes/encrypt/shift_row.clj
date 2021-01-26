(ns clojure-aes.encrypt.shift-row
  (:require [clojure-aes.utils :as utils]))

(defn rotWord
  "Transforms a word (4 bytes) by rotating/wrapping each byte cyclically to the left based on the number of rotations."
  [word amount]
  (let [extract-bytes (fn [word mask shift]
                        (bit-shift-right
                         (bit-and mask word) shift))
        byte1 (extract-bytes word 0xff000000 24)
        byte2 (extract-bytes word 0x00ff0000 16)
        byte3 (extract-bytes word 0x0000ff00 8)
        byte4 (extract-bytes word 0x000000ff 0)
        new-byte-val (fn [byte offset]
                       (bit-shift-left byte offset))
        new-word-val (fn [a b c d]
                       (+ (new-byte-val a 24)
                          (new-byte-val b 16)
                          (new-byte-val c 8)
                          (new-byte-val d 0)))

        rotations (mod amount 4)]
    (cond
      (= 0 rotations) word
      (= 1 rotations) (new-word-val byte2 byte3 byte4 byte1)
      (= 2 rotations) (new-word-val byte3 byte4 byte1 byte2)
      (= 3 rotations) (new-word-val byte4 byte1 byte2 byte3))))

(defn shift-rows
  "Cyclically shifts the last three rows of the 4x4 state matrix by different offsets to the left."
  [[row1 row2 row3 row4] round-num]
  (let [[row2-a row2-b row2-c row2-d] row2
        [row3-a row3-b row3-c row3-d] row3
        [row4-a row4-b row4-c row4-d] row4
        transformed [row1
                     [row2-b row2-c row2-d row2-a]
                     [row3-c row3-d row3-a row3-b]
                     [row4-d row4-a row4-b row4-c]]]
                                        ;(utils/print-array transformed "after shift-rows" round-num)
    (utils/debug-aes round-num "s_row " (utils/matrix-transposition transformed))
    transformed))





