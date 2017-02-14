
(ns shallow-diff-test.diff
  (:require [cljs.test :refer-macros [deftest is run-tests]]
            [shallow-diff.diff :refer [diff]]))

(deftest id-test (is (= [] (diff {} {}))))

(deftest vector-test (is (= [[[2] [:set! 4]]] (diff [1 2 3] [1 2 4]))))

(deftest
  map-test
  (is
    (=
      [[[:b] [:set! 0]] [[] [:add :c 2]]]
      (diff {:b 2, :a 1} {:c 2, :b 0, :a 1}))))

(deftest
  hash-set-test
  (is
    (=
      [[[] [:exclude 1]] [[] [:include 3]]]
      (diff (hash-set 1 2) (hash-set 3 2)))))

(deftest
  set!-vector-test
  (is (= [[[1] [:set! 3]]] (diff [1 2 4 5] [1 3 4 5]))))

(deftest
  set!-map-test
  (is (= [[[:a] [:set! 2]]] (diff {:b 2, :a 1} {:b 2, :a 2}))))

(deftest
  set!-deep-vector-test
  (is
    (=
      [[[2 2 1] [:set! 16]]]
      (diff [1 2 [3 4 [5 6] 7] 8] [1 2 [3 4 [5 16] 7] 8]))))

(deftest
  set!-deep-map-test
  (is
    (=
      [[[:a :b :c] [:set! 2]]]
      (diff {:a {:b {:c 1}}} {:a {:b {:c 2}}}))))

(deftest append-test (is (= [[[] [:append 3]]] (diff [1 2] [1 2 3]))))

(deftest
  prepend-test
  (is
    (=
      [[[] [:insert 0 11]] [[] [:insert 2 22]] [[] [:append 33]]]
      (diff [2 3] [11 2 22 3 33]))))

(deftest
  shift-test
  (is (= [[[] [:remove 0]] [[] [:remove 0]]] (diff [1 2 3 4] [3 4]))))

(deftest
  mixed-test
  (is
    (=
      [[[:vec 0] [:set! 0]]
       [[:vec 1] [:set! 4]]
       [[:vec] [:append 4]]
       [[:set] [:exclude 1]]
       [[:set] [:exclude 2]]
       [[:set] [:include 0]]
       [[:set] [:include 4]]
       [[:map] [:drop :b]]
       [[:map :a] [:set! 0]]
       [[:map] [:add :c 3]]]
      (diff
        {:vec [1 2 3], :set (hash-set 1 2 3), :map {:b 2, :a 1}}
        {:vec [0 4 3 4], :set (hash-set 0 3 4), :map {:c 3, :a 0}}))))

(run-tests)
