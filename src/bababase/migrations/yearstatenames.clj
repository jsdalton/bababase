(ns bababase.migrations.yearstatenames
  (:require [caribou.model :as model]
            [caribou.config :as config]))

(defn migrate
  []
  (model/create
    :model
    {:name "YearStateName"
     :fields [{:name "count" :type "integer"}
              {:name "year" :type "link"
               :target-id (:id (model/pick :model {:where {:name "Year"}}))}
              {:name "state" :type "link"
               :target-id (:id (model/pick :model {:where {:name "State"}}))}
              {:name "givenname" :type "link"
               :target-id (:id (model/pick :model {:where {:name "GivenName"}}))}]}))

(defn rollback
  []
  (model/destroy
    :yearstatename
    (config/draw :models :yearstatename :id)))
