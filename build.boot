
(set-env!
 :dependencies '[[org.clojure/clojurescript "1.9.76"      :scope "test"]
                 [org.clojure/clojure       "1.8.0"       :scope "test"]
                 [adzerk/boot-cljs          "1.7.170-3"   :scope "test"]
                 [adzerk/boot-reload        "0.4.6"       :scope "test"]
                 [cirru/boot-cirru-sepal    "0.1.8"       :scope "test"]
                 [binaryage/devtools        "0.5.2"       :scope "test"]
                 [adzerk/boot-test          "1.1.1"       :scope "test"]
                 [mvc-works/hsl             "0.1.2"       :scope "test"]
                 [mvc-works/respo           "0.2.2"       :scope "test"]]

  :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"}]))

(require '[adzerk.boot-cljs   :refer [cljs]]
         '[adzerk.boot-reload :refer [reload]]
         '[cirru-sepal.core   :refer [transform-cirru]]
         '[respo.alias        :refer [html head title script style meta' div link body]]
         '[respo.render.static-html :refer [make-html]]
         '[adzerk.boot-test   :refer :all]
         '[clojure.java.io    :as    io])

(def +version+ "0.1.1")

(task-options!
  pom {:project     'cumulo/shallow-diff
       :version     +version+
       :description "Workflow"
       :url         "https://github.com/Cumulo/shallow-diff"
       :scm         {:url "https://github.com/Cumulo/shallow-diff"}
       :license     {"MIT" "http://opensource.org/licenses/mit-license.php"}}

  test {:namespaces '#{shallow-diff-test.diff shallow-diff-test.patch
      shallow-diff-test.round-trip}})

(deftask compile-cirru []
  (set-env!
    :source-paths #{"cirru/"})
  (comp
    (transform-cirru)
    (target :dir #{"compiled/"})))

(defn use-text [x] {:attrs {:innerHTML x}})
(defn html-dsl [data fileset]
  (make-html
    (html {}
    (head {}
      (title (use-text "Shallow Diff"))
      (link {:attrs {:rel "icon" :type "image/png" :href "http://logo.cirru.org/cirru-400x400.png"}})
      (style (use-text "body {margin: 0;}"))
      (style (use-text "body * {box-sizing: border-box;}"))
      (script {:attrs {:id "config" :type "text/edn" :innerHTML (pr-str data)}}))
    (body {}
      (div {:attrs {:id "app"}})
      (script {:attrs {:src "main.js"}})))))

(deftask html-file
  "task to generate HTML file"
  [d data VAL edn "data piece for rendering"]
  (with-pre-wrap fileset
    (let [tmp (tmp-dir!)
          out (io/file tmp "index.html")]
      (empty-dir! tmp)
      (spit out (html-dsl data fileset))
      (-> fileset
        (add-resource tmp)
        (commit!)))))

(deftask dev []
  (set-env!
    :source-paths #{"cirru/src" "cirru/app"})
  (comp
    (html-file :data {:build? false})
    (watch)
    (transform-cirru)
    (reload :on-jsload 'shallow-diff.core/on-jsload)
    (cljs)
    (target)))

(deftask build-simple []
  (set-env!
    :source-paths #{"cirru/src"})
  (comp
    (transform-cirru)
    (cljs :optimizations :simple)
    (html-file :data {:build? false})
    (target)))

(deftask build-advanced []
  (set-env!
    :source-paths #{"cirru/src"})
  (comp
    (transform-cirru)
    (cljs :optimizations :advanced)
    (html-file :data {:build? true})
    (target)))

(deftask rsync []
  (fn [next-task]
    (fn [fileset]
      (sh "rsync" "-r" "target/" "tiye:repo/Cumulo/shallow-diff" "--exclude" "main.out" "--delete")
      (next-task fileset))))

(deftask send-tiye []
  (comp
    (build-simple)
    (rsync)))

(deftask build []
  (set-env!
    :source-paths #{"cirru/src"})
  (comp
    (transform-cirru)
    (pom)
    (jar)
    (install)
    (target)))

(deftask deploy []
  (comp
    (build)
    (push :repo "clojars" :gpg-sign (not (.endsWith +version+ "-SNAPSHOT")))))

(deftask watch-test []
  (set-env!
    :source-paths #{"cirru/src" "cirru/test"})
  (comp
    (watch)
    (transform-cirru)
    (target)
    (test)))
