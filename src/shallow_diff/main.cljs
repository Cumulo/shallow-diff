
(ns shallow-diff.main
  (:require [respo.core :refer [render! clear-cache! gc-states!]]
            [shallow-diff.comp.container :refer [comp-container]]))

(defonce store-ref (atom 0))

(defn dispatch [op op-data] )

(defonce states-ref (atom {}))

(defn render-app! []
  (let [target (.querySelector js/document "#app")]
    (render! (comp-container @store-ref) target dispatch states-ref)))

(defn -main []
  (enable-console-print!)
  (render-app!)
  (add-watch store-ref :gc (fn [] (gc-states! states-ref)))
  (add-watch store-ref :changes render-app!)
  (add-watch states-ref :changes render-app!)
  (println "app started!"))

(defn on-jsload! [] (clear-cache!) (render-app!) (println "code updated."))

(set! js/window.onload -main)
