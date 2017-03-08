
(set-env!
  :asset-paths #{"assets/"}
  :resource-paths #{"src/"}
  :dependencies '[[org.clojure/clojure       "1.8.0"       :scope "provided"]
                  [org.clojure/clojurescript "1.9.473"     :scope "provided"]
                  [adzerk/boot-cljs          "1.7.228-1"   :scope "provided"]
                  [adzerk/boot-reload        "0.4.13"      :scope "provided"]
                  [andare                    "0.5.0"       :scope "provided"]
                  [cirru/boot-cirru-sepal    "0.1.8"       :scope "provided"]
                  [cirru/boot-stack-server   "0.1.30"      :scope "provided"]
                  [binaryage/devtools        "0.9.1"       :scope "provided"]
                  [fipp                      "0.6.9"       :scope "provided"]
                  [mvc-works/hsl             "0.1.2"       :scope "provided"]
                  [respo                     "0.3.38"      :scope "provided"]])

(require '[adzerk.boot-cljs   :refer [cljs]]
         '[adzerk.boot-reload :refer [reload]])

(def +version+ "0.1.3")

(task-options!
  pom {:project     'cumulo/shallow-diff
       :version     +version+
       :description "Simple data diffing"
       :url         "https://github.com/Cumulo/shallow-diff"
       :scm         {:url "https://github.com/Cumulo/shallow-diff"}
       :license     {"MIT" "http://opensource.org/licenses/mit-license.php"}})

(deftask dev []
  (comp
    (watch)
    (reload :on-jsload 'shallow-diff.main/on-jsload!
            :cljs-asset-path ".")
    (cljs :compiler-options {:language-in :ecmascript5})
    (target :no-clean true)))

(deftask build-advanced []
  (comp
    (cljs :optimizations :advanced
          :compiler-options {:language-in :ecmascript5
                             :pseudo-names true
                             :static-fns true
                             :parallel-build true
                             :optimize-constants true
                             :source-map true})
    (target)))

(deftask build []
  (comp
    (pom)
    (jar)
    (install)
    (target)))

(deftask deploy []
  (set-env!
    :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}]))
  (comp
    (build)
    (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))
