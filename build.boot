(set-env!
  :source-paths #{"src"}
  :resource-paths #{"resources"}
  :dependencies '[[perun "0.4.2-SNAPSHOT"]
                  [hiccup "1.0.5" :exclusions [org.clojure/clojure]]
                  [org.clojure/clojurescript "1.10.439"]
                  [pandeiro/boot-http "0.6.3-SNAPSHOT"]
                  [deraen/boot-livereload "0.2.1"]
                  [deraen/boot-sass "0.3.1"]
                  [adzerk/boot-cljs "2.1.5" :scope "test"]])

(require '[boot.core :as boot]
         '[clojure.string :as str]
         '[clojure.java.io :as io]
         '[io.perun :as perun]
         '[io.perun.core :refer [report-info]]
         '[pandeiro.boot-http :refer [serve]]
         '[deraen.boot-livereload :refer [livereload]]
         '[deraen.boot-sass :refer [sass]]
         '[adzerk.boot-cljs :refer [cljs]])

(defn match-any
  [patterns s]
  (some #(re-find % s) patterns))

(deftask clean!
  [d dir      DIR     str       "Directory to clean defaults to target/public"
   e excludes RE-LIST #{regex}  "Regexps to exclude from cleaning"
   i included RE-LIST #{regex}  "Regexps to include for cleaning"]
  (clojure.pprint/pprint *opts*)
  (let [excluder (if (some? excludes)
                   #(match-any excludes (str %))
                   (constantly false))
        includer (if (some? included)
                   #(match-any included (str %))
                   (constantly true))
        dir (or dir "target/public")]

    (doseq [file (->> (file-seq (io/file dir))
                      (remove excluder)
                      (filter includer))]
      (.delete file))
    (report-info "clean!" "Cleaned %s" dir)))

(deftask build
  "Build test blog. This task is just for testing different plugins together."
  [e build-env BUILD-ENV kw    "Environment keyword like :dev or :production"]
  (comp
        (perun/global-metadata)
        (perun/markdown :md-exts {:all true})
        (perun/draft)
        (perun/print-meta)
        (perun/slug)
        (perun/ttr)
        (perun/word-count)
        (perun/build-date)
        (perun/gravatar :source-key :author-email :target-key :author-gravatar)
        (perun/render :renderer 'eccentric-j.site.post/render)
        (perun/collection :renderer 'eccentric-j.site.index/render :page "index.html")
        (perun/tags :renderer 'eccentric-j.site.tags/render)
        (perun/paginate :renderer 'eccentric-j.site.paginate/render)
        ; (perun/assortment :renderer 'eccentric-j.site.assortment/render
        ;                   :grouper (fn [entries]
        ;                              (->> entries
        ;                                   (mapcat (fn [entry]
        ;                                             (if-let [kws (:keywords entry)]
        ;                                               (map #(-> [% entry]) (str/split kws #"\s*,\s*"))
        ;                                               [])))
        ;                                   (reduce (fn [result [kw entry]]
        ;                                             (let [path (str kw ".html")]
        ;                                               (-> result
        ;                                                   (update-in [path :entries] conj entry)
        ;                                                   (assoc-in [path :entry :keyword] kw))))
        ;                                           {}))))
        (perun/static :renderer 'eccentric-j.site.about/render :page "about.html")
        (perun/inject-scripts :scripts #{"start.js"})
        (perun/sitemap)
        (perun/rss :description "Hashobject blog")
        (perun/atom-feed)
        (sass)
        (cljs :optimizations (if (= build-env :production) :advanced :none))
        (target :no-clean true)
        (notify)))

(deftask dev
  []
  (comp (watch)
        (build)
        (livereload :snippet true)
        (serve :resource-root "public" :port 9000)))
