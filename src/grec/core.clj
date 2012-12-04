(ns grec.core
  (:require 
    [clojure.java.io :as io]
    [clojure.xml :as xml]
    [colorize.core :as clojurecolor]
    )
  (:use 
    [clojure.tools.cli :only (cli)]
;    [clojure.contrib.command-line]
    )
  (:import 
    [java.net URLEncoder]
    )
  (:gen-class :main true)
  )

(defn pad-regex [regex] (re-pattern (str "(.*)(" regex ")(.*)")))

(defn add-color-by-regex
  [text regex colr]
  regex colr
  (let [x  (re-seq (pad-regex regex) text)]
   (if (nil? x) text
           (let [a (add-color-by-regex ((first x) 1) regex colr)
                 b (clojurecolor/color (keyword colr) ((first x) 2))
                 c (add-color-by-regex ((first x) 3) regex colr)
                 ]
                   (str (apply str a) (apply str b) (apply str c))))))

(defn file-colorize
  [filename regex-red regex-green]
 (with-open [rd (io/reader filename)]
    (doseq [line (line-seq rd)] 
      (println
        (add-color-by-regex (add-color-by-regex line regex-green "green") regex-red "red")
        ))))


(defn -main [& args]
  (dorun (println "hey shouln't this show up?"))
  (let [[opts args banner]
        (cli args
             ["-h" "--help" "Show usage" :flag true :default false]
             ["-r" "--red" "color this red"]
             ["-g" "--green" "color this green"])
        {:keys [red green]} 
        opts] 
    (when (:help opts)
      (println banner)
      (System/exit 0))
    (doseq [file args] (file-colorize file (opts :red) (opts :green)))
))



