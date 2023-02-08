(ns clojure-aes.encrypt.mix-columns
  (:require [clojure-aes.shared :as shared]
            [clojure-aes.utils :as utils]
            [clojure-aes.constants :as c]))

(defn mixColumns
  "This transformation treats each column in state as a four-term polynomial. This polynomial is multiplied (modulo another polynomial) by a fixed polynomial with coefficients (see Sections 4.3 and 5.1.3) of FIPS 197."
  [state round-num]
  (let [transposed (utils/matrix-transposition state)
        matrix-rows (vals (-> c/mix-column-matrix :row-order))
        multiply-column-fn (fn [index matrix-index]
                             (let [matrix-row (nth matrix-rows matrix-index)
                                   col (nth transposed index)
                                   [m0 m1 m2 m3 ] matrix-row
                                   [c0 c1 c2 c3] col
                                   new-byte (bit-xor
                                             (shared/ffmultiply c0 m0)
                                             (shared/ffmultiply c1 m1)
                                             (shared/ffmultiply c2 m2)
                                             (shared/ffmultiply c3 m3))]
                               new-byte))
        generate-column (fn [col-num]
                          [(multiply-column-fn col-num 0)
                           (multiply-column-fn col-num 1)
                           (multiply-column-fn col-num 2)
                           (multiply-column-fn col-num 3)])
        result [(generate-column 0)
                (generate-column 1)
                (generate-column 2)
                (generate-column 3)]
        transposed-state (utils/matrix-transposition result)]
    (utils/print-array transposed-state "after mixColumns:" round-num)
    (utils/debug-aes round-num "m_col " (utils/matrix-transposition transposed-state))
    transposed-state))
