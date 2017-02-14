
(ns shallow-diff.diff )

(declare diff-values)

(declare diff-map)

(declare reduce-vector)

(declare diff-vector)

(defn diff-set [xs ys coord collect!]
  (let [all-keys (into (hash-set) (concat xs ys))]
    (doseq [cursor all-keys]
      (let [exists? (contains? ys cursor), existed? (contains? xs cursor)]
        (cond
          (and exists? existed?) nil
          (and exists? (not existed?)) (collect! [coord [:include cursor]])
          (and (not exists?) existed?) (collect! [coord [:exclude cursor]])
          :else nil)))
    nil))

(defn =type? [x y] (= (type x) (type y)))

(defn shallow-index-of
  ([xs x] (shallow-index-of 0 xs x))
  ([counter xs x]
   (cond
     (= (count xs) 0) nil
     (= (first xs) x) counter
     :else (recur (inc counter) (rest xs) x))))

(defn shallow-contains? [xs x]
  (if (= (count xs) 0) false (if (identical? (first xs) x) true (recur (rest xs) x))))

(defn diff-vector [xs ys coord collect!] (reduce-vector collect! 0 xs ys coord))

(defn reduce-vector [collect! counter xs ys coord]
  (cond
    (and (empty? xs) (empty? ys)) nil
    (and (empty? xs) (not (empty? ys)))
      (let [cursor (first ys)]
        (collect! [coord [:append cursor]])
        (recur collect! (inc counter) xs (subvec ys 1) coord))
    (and (not (empty? xs)) (empty? ys))
      (do
       (collect! [coord [:remove counter]])
       (recur collect! counter (subvec xs 1) ys coord))
    :else
      (let [x1 (first xs)
            y1 (first ys)
            x1-remains? (shallow-contains? (subvec ys 1) x1)
            y1-existed? (shallow-contains? (subvec xs 1) y1)]
        (cond
          (= x1 y1) (recur collect! (inc counter) (subvec xs 1) (subvec ys 1) coord)
          (and x1-remains? (not y1-existed?))
            (do
             (collect! [coord [:insert counter y1]])
             (recur collect! (inc counter) xs (subvec ys 1) coord))
          (and (not x1-remains?) y1-existed?)
            (do
             (collect! [coord [:remove counter]])
             (recur collect! counter (subvec xs 1) ys coord))
          (and (not x1-remains?) (not y1-existed?))
            (do
             (diff-values x1 y1 (conj coord counter) collect!)
             (recur collect! (inc counter) (subvec xs 1) (subvec ys 1) coord))
          :else
            (let [xi (shallow-index-of (subvec ys 1) x1)
                  yi (shallow-index-of (subvec xs 1) y1)]
              (if (<= xi yi)
                (do
                 (collect! [coord [:insert counter y1]])
                 (recur collect! (inc counter) xs (subvec ys 1) coord))
                (do
                 (collect! [coord [:remove counter]])
                 (recur collect! counter (subvec xs 1) ys coord))))))))

(defn diff-map [xs ys coord collect!]
  (let [all-keys (into (hash-set) (concat (keys xs) (keys ys)))]
    (doseq [cursor all-keys]
      (let [x (get xs cursor), y (get ys cursor)]
        (cond
          (identical? x y) nil
          (and (some? x) (nil? y)) (collect! [coord [:drop cursor]])
          (and (nil? x) (some? y)) (collect! [coord [:add cursor y]])
          :else (diff-values x y (conj coord cursor) collect!))))
    nil))

(defn diff-values [x y coord collect!]
  (cond
    (identical? x y) []
    (and (=type? x y) (set? x)) (diff-set x y coord collect!)
    (and (=type? x y) (vector? x)) (diff-vector x y coord collect!)
    (and (=type? x y) (map? x)) (diff-map x y coord collect!)
    :else (collect! [coord [:set! y]])))

(defn diff [x y]
  (let [ref-changes (atom []), collect! (fn [x] (swap! ref-changes conj x))]
    (diff-values x y [] collect!)
    @ref-changes))

(declare diff)
