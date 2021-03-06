
(ns shallow-diff-test.patch
  (:require [cljs.test :refer-macros [deftest is run-tests]]
            [shallow-diff.patch :refer [patch]]))

(deftest map-add-test (is (= {:a 1} (patch {} [[[] [:add :a 1]]]))))

(deftest
  map-rm-test
  (is (= {:a 1} (patch {:b 2, :a 1} [[[] [:drop :b]]]))))

(deftest
  include-test
  (is (= (hash-set 1 2 4) (patch (hash-set 1 2) [[[] [:include 4]]]))))

(deftest
  exclude-test
  (is (= (hash-set 1 2) (patch (hash-set 1 2 4) [[[] [:exclude 4]]]))))

(deftest insert-test (is (= [1 2] (patch [2] [[[] [:insert 0 1]]]))))

(deftest remove-test (is (= [1] (patch [1 2] [[[] [:remove 1]]]))))

(deftest append-test (is (= [1 2 1] (patch [1 2] [[[] [:append 1]]]))))

(deftest
  deep-add-test
  (is
    (=
      {:a {:b {:c 1, :d 2}}}
      (patch {:a {:b {:c 1}}} [[[:a :b] [:add :d 2]]]))))

(deftest
  deep-insert-test
  (is (= [[[1 4 2]]] (patch [[[1 2]]] [[[0 0] [:insert 1 4]]]))))

(run-tests)
