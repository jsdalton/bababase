(ns bababase.controllers.api
  (:require [caribou.model :as model]
            [caribou.app.controller :as controller]))

(defn index
  [request]
  (controller/render 
    :json
    {"status" :ok}))
