(ns clojure-aes.decrypt.inverse-mix-columns
  (:require [clojure-aes.constants :as c]
            [clojure-aes.shared :as shared]
            [clojure-aes.utils :as utils]))

(defn inv-mixColumns
  "This transformation is the inverse of mixColumns. (see Section 5.3.3) of FIPS 197"
  [state round-num]
  (let [state-transposed (utils/matrix-transposition state)
        matrix-rows (vals (-> c/inv-mix-column-matrix :row-order))
        multiply-column-fn (fn [index matrix-index]
                             (let [matrix-row (nth matrix-rows matrix-index)
                                   col (nth state-transposed index)
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
                (generate-column 3)]]
    (utils/debug-aes round-num "after inv-mixColumns:  " result )
    (utils/matrix-transposition result)))
