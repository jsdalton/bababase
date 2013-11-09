(ns bababase.migrations.years
  (:require [caribou.model :as model]
            [caribou.config :as config]))

(defn migrate
  []
  (model/create
    :model
    {:name "Year"
     :fields [{:name "year" :type "integer"}]}))

(defn rollback
  []
  (model/destroy
    :year
    (caribou.config/draw :models :year :id)))
