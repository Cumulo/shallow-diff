
(ns shallow-diff.diff )

(declare diff-map)

(declare diff-vector)

(declare diff)

(declare reduce-vector)

(defn shallow-contains? [xs x]
  (if (= (count xs) 0) false (if (identical? (first xs) x) true (recur (rest xs) x))))

(defn diff-set [xs ys coord]
  (let [all-keys (into (hash-set) (concat xs ys))]
    (->> all-keys
         (map
          (fn [cursor]
            (let [exists? (contains? ys cursor), existed? (contains? xs cursor)]
              (cond
                (and exists? existed?) []
                (and exists? (not existed?)) [[coord [:include cursor]]]
                (and (not exists?) existed?) [[coord [:exclude cursor]]]
                :else []))))
         (apply concat)
         (into []))))

(defn shallow-index-of
  ([xs x] (shallow-index-of 0 xs x))
  ([counter xs x]
   (cond
     (= (count xs) 0) nil
     (= (first xs) x) counter
     :else (recur (inc counter) (rest xs) x))))

(defn reduce-vector [acc counter xs ys coord]
  (cond
    (and (= (count xs) 0) (= (count ys) 0)) acc
    (and (= (count xs) 0) (> (count ys) 0))
      (let [cursor (first ys), next-acc (conj acc [coord [:append cursor]])]
        (recur next-acc (inc counter) xs (subvec ys 1) coord))
    (and (> (count xs) 0) (= (count ys) 0))
      (let [next-acc (conj acc [coord [:remove counter]])]
        (recur next-acc counter (subvec xs 1) ys coord))
    :else
      (let [x1 (first xs)
            y1 (first ys)
            x1-remains? (shallow-contains? (subvec ys 1) x1)
            y1-existed? (shallow-contains? (subvec xs 1) y1)]
        (cond
          (= x1 y1) (recur acc (inc counter) (subvec xs 1) (subvec ys 1) coord)
          (and x1-remains? (not y1-existed?))
            (recur
             (conj acc [coord [:insert counter y1]])
             (inc counter)
             xs
             (subvec ys 1)
             coord)
          (and (not x1-remains?) y1-existed?)
            (recur (conj acc [coord [:remove counter]]) counter (subvec xs 1) ys coord)
          (and (not x1-remains?) (not y1-existed?))
            (let [this-diff (diff x1 y1 (conj coord counter))
                  next-acc (into [] (concat acc this-diff))]
              (recur next-acc (inc counter) (subvec xs 1) (subvec ys 1) coord))
          :else
            (let [xi (shallow-index-of (subvec ys 1) x1)
                  yi (shallow-index-of (subvec xs 1) y1)]
              (if (<= xi yi)
                (recur
                 (conj acc [coord [:insert counter y1]])
                 (inc counter)
                 xs
                 (subvec ys 1)
                 coord)
                (recur (conj acc [coord [:remove counter]]) counter (subvec xs 1) ys coord)))))))

(defn diff
  ([x y] (diff x y []))
  ([x y coord]
   (cond
     (identical? x y) []
     (and (= (type x) (type y)) (set? x)) (diff-set x y coord)
     (and (= (type x) (type y)) (vector? x)) (diff-vector x y coord)
     (and (= (type x) (type y)) (map? x)) (diff-map x y coord)
     :else [[coord [:set! y]]])))

(defn diff-vector [xs ys coord] (reduce-vector [] 0 xs ys coord))

(defn diff-map [xs ys coord]
  (let [all-keys (into (hash-set) (concat (keys xs) (keys ys)))]
    (->> all-keys
         (map
          (fn [cursor]
            (let [x (get xs cursor), y (get ys cursor)]
              (cond
                (identical? x y) []
                (and (some? x) (not (some? y))) [[coord [:drop cursor]]]
                (and (not (some? x)) (some? y)) [[coord [:add cursor y]]]
                :else (diff x y (conj coord cursor))))))
         (apply concat)
         (into []))))

(declare diff)