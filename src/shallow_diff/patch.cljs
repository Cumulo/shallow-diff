
(ns shallow-diff.patch )

(defn decide-op [acc operation]
  (let [coord (first operation)
        detail (last operation)
        op (first detail)
        args (rest detail)
        a1 (first args)
        a2 (first (rest args))]
    (let [method (fn [cursor]
                   (case op
                     :set! a1
                     :add (assoc cursor a1 a2)
                     :drop (dissoc cursor a1)
                     :insert
                       (into [] (concat (conj (subvec cursor 0 a1) a2) (subvec cursor a1)))
                     :remove
                       (into [] (concat (subvec cursor 0 a1) (subvec cursor (inc a1))))
                     :append (conj cursor a1)
                     :include (conj cursor a1)
                     :exclude (disj cursor a1)
                     acc))]
      (if (= coord []) (method acc) (update-in acc coord method)))))

(defn patch [x changes] (reduce decide-op x changes))
