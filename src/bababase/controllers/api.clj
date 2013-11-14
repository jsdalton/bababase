(ns bababase.controllers.api
  (:require [caribou.model :as model]
            [clojure.string :as string]
            [caribou.util :as caribou-util]
            [caribou.query :as caribou-query]
            [bababase.utils :as bababase-util]
            [caribou.app.controller :as controller]))

(defn wrap-response
  [response]
  {:meta {:status 200 :msg "OK"}
   :response response})

(defn index
  [request]
  (controller/render 
    :json
    {"status" :ok}))

(defn names-index
  [request]
  (let [params (select-keys (:params request) [:gender :limit :offset :include :q])

        limit-parsed (and (:limit params) (caribou-util/convert-int (:limit params)))
        limit (or (and limit-parsed (bababase-util/fence limit-parsed 1 100)) 20)

        offset-parsed (and (:offset params) (caribou-util/convert-int (:offset params)))
        offset (and offset-parsed (bababase-util/fence offset-parsed 1 100))

        gender-parsed (and (:gender params) (string/upper-case (:gender params)))
        gender (and gender-parsed (if (contains? #{"M" "F"} gender-parsed) gender-parsed))

        q (:q params)

        where {:year 2012 :region "US"}
        where (if q (conj where {:name {:ILIKE (str q "%")}}) where)
        where (if gender (conj where {:gender gender}) where)
        ]
    (let [all (model/gather
                :yearregionnamecount
                {:where where
                 :limit limit
                 :offset offset
                 :order {:count :desc}})
          response (map #(select-keys % [:name :gender :count]) all) ]
      (controller/render 
        :json
        (wrap-response response)))))
