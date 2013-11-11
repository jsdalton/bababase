(ns bababase.tasks
  (:require [leiningen.core.main :as lein]
            [bababase.utils :as utils]
            [clojure.java.io :as io]
            [caribou.model :as model]
            [caribou.logger :as log]
            [caribou.config :as config]
            [caribou.core :as core]
            [bababase.boot :as boot]
            [caribou.app.config :as app-config]
            [clojure.string :as string]))

(def fields [:region :gender :year :name :count])
(def us-states 
  {:AL "Alabama"
   :AK "Alaska"
   :AZ "Arizona"
   :AR "Arkansas"
   :CA "California"
   :CO "Colorado"
   :CT "Connecticut"
   :DE "Delaware"
   :DC "District of Columbia"
   :FL "Florida"
   :GA "Georgia"
   :HI "Hawaii"
   :ID "Idaho"
   :IL "Illinois"
   :IN "Indiana"
   :IA "Iowa"
   :KS "Kansas"
   :KY "Kentucky"
   :LA "Louisiana"
   :ME "Maine"
   :MD "Maryland"
   :MA "Massachusetts"
   :MI "Michigan"
   :MN "Minnesota"
   :MS "Mississippi"
   :MO "Missouri"
   :MT "Montana"
   :NE "Nebraska"
   :NV "Nevada"
   :NH "New Hampshire"
   :NJ "New Jersey"
   :NM "New Mexico"
   :NY "New York"
   :NC "North Carolina"
   :ND "North Dakota"
   :OH "Ohio"
   :OK "Oklahoma"
   :OR "Oregon"
   :PA "Pennsylvania"
   :RI "Rhode Island"
   :SC "South Carolina"
   :SD "South Dakota"
   :TN "Tennessee"
   :TX "Texas"
   :UT "Utah"
   :VT "Vermont"
   :VA "Virginia"
   :WA "Washington"
   :WV "West Virginia"
   :WI "Wisconsin"
   :WY "Wyoming" })

(defn line-to-map
  "Maps line from SSA file to map of fields"
  [line]
  (zipmap fields (string/split line #"\,")))

(def provide-memoized (memoize utils/provide))

(defn load-ssa-data-names-by-state
  "Load raw SSA names-by-state data found in given directory."
  ([dir]
   (load-ssa-data-names-by-state dir provide-memoized))
  ([dir provider]
   (doseq [file (utils/list-directory-contents dir "txt")]
     (log/info (str "Processing " (.getName file)))
     (with-open [rdr (clojure.java.io/reader file)]
       (doseq [line (line-seq rdr)]
         (let [row (line-to-map line)
               year (provider :year {:year (read-string (:year row))})
               givenname (provider :givenname {:name (:name row) :gender (:gender row)})
               region (provider :region {:name ((keyword (:region row)) us-states) :code (:region row)})]
           (provider :yearregionnamecount {:count (read-string (:count row))
                                           :region (:region row)
                                           :year (read-string (:year row))
                                           :name (:name row)
                                           :gender (:gender row)})))))))

(defn load-ssa-data-names-by-state-task
  "Leiningen task wrapper around load-ssa-data"
  [config-file dir exit?]
  (let [default (app-config/default-config)
        local (config/merge-config default boot/local-config)
        cfg (config/read-config config-file)
        cfg (config/merge-config local cfg)
        cfg (config/process-config cfg)
        cfg (core/init cfg)]
    (core/with-caribou cfg
      (load-ssa-data-names-by-state dir)))
  (if exit? (lein/exit)))

