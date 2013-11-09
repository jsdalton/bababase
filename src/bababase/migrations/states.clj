(ns bababase.migrations.states
  (:require [caribou.model :as model]
            [caribou.config :as config]))

(defn migrate
  []
  (model/create
    :model
    {:name "State"
     :fields [{:name "name" :type "string"}
              {:name "code" :type "string"}]}))

(defn rollback
  []
  (model/destroy
    :state
    (caribou.config/draw :models :presentation :id)))
