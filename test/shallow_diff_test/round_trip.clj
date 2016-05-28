
(ns shallow-diff-test.round-trip
  (:require [clojure.test :refer :all]
            [shallow-diff.diff :refer [diff]]
            [shallow-diff.patch :refer [patch]]))

(deftest
  round-map-test
  (let [x1 {:c 2, :a 1}
        x2 {:b 2, :a 1}
        changes [[[] [:drop :c]] [[] [:add :b 2]]]]
    (is (= changes (diff x1 x2)))
    (is (= x2 (patch x1 changes)))))

(deftest
  round-set-test
  (let [x1 (hash-set 1 2)
        x2 (hash-set 1 3)
        changes [[[] [:include 3]] [[] [:exclude 2]]]]
    (is (= changes (diff x1 x2)))
    (is (= x2 (patch x1 changes)))))

(deftest
  round-append-test
  (let [x1 [1 2] x2 [1 2 3] changes [[[] [:append 3]]]]
    (is (= changes (diff x1 x2)))
    (is (= x2 (patch x1 changes)))))

(deftest
  round-insert-test
  (let [x1 [1 3] x2 [1 2 3] changes [[[] [:insert 1 2]]]]
    (is (= changes (diff x1 x2)))
    (is (= x2 (patch x1 changes)))))

(deftest
  round-remove-test
  (let [x1 [1 2 3] x2 [1 2] changes [[[] [:remove 2]]]]
    (is (= changes (diff x1 x2)))
    (is (= x2 (patch x1 changes)))))