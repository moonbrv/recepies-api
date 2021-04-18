(ns recipes-api.utils)

(defn get-uid [req]
  (get-in req [:claims :sub]))

(defn get-recipe-id [req]
  (get-in req [:parameters :path :recipe-id]))
