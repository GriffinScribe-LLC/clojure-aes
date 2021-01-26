(ns clojure-aes.decrypt.inverse-shift-rows
  (:require [clojure-aes.utils :as utils]))

(defn inv-shift-rows
  "Cyclically shifting the last three rows of the 4x4 state by different offsets to the right."
  [[row1 row2 row3 row4] round-num]
  (let [[row2-a row2-b row2-c row2-d] row2
        [row3-a row3-b row3-c row3-d] row3
        [row4-a row4-b row4-c row4-d] row4
        transformed [row1
                     [row2-d row2-a row2-b row2-c]
                     [row3-c row3-d row3-a row3-b]
                     [row4-b row4-c row4-d row4-a]]]
    (utils/print-array transformed "after inv-shift-rows" round-num)
    transformed))
