(ns bababase.migrations.givennames
  (:require [caribou.model :as model]
            [caribou.config :as config]))

(defn migrate
  []
  (model/create
    :model
    {:name "GivenName"
     :fields [{:name "name" :type "string"}
              {:name "gender" :type "enum"
               :enumerations [{:entry "M"}
                              {:entry "F"}]}]}))

(defn rollback
  []
  (model/destroy
    :givenname
    (caribou.config/draw :models :givenname :id)))
