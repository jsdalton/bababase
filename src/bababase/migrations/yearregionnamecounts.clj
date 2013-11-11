(ns bababase.migrations.yearregionnamecounts
  (:require [caribou.model :as model]
            [caribou.config :as config]))

(defn migrate
  []
  (model/create
    :model
    {:name "YearRegionNameCount"
     :fields [{:name "count" :type "integer"}
              {:name "year" :type "integer"}
              {:name "region" :type "string"}
              {:name "gender" :type "string"}
              {:name "name" :type "string"}]}))

(defn rollback
  []
  (model/destroy
    :yearregionnamecount
    (config/draw :models :yearregionnamecount :id)))
