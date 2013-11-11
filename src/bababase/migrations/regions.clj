(ns bababase.migrations.regions
  (:require [caribou.model :as model]
            [caribou.config :as config]))

(defn migrate
  []
  (model/create
    :model
    {:name "Region"
     :fields [{:name "name" :type "string"}
              {:name "code" :type "string"}]}))

(defn rollback
  []
  (model/destroy
    :region
    (caribou.config/draw :models :presentation :id)))
