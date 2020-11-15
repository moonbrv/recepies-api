(ns recipes-api.router
  (:require
   [reitit.ring :as ring]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [muuntaja.core :as m]
   [recipes-api.recipe.routes :as recipe]))

(def router-config {:data {:muuntaja m/instance
                           :middleware [swagger/swagger-feature
                                        muuntaja/format-middleware]}})

(def swagger-docs
  ["/swagger.json" {:get {:no-doc true
                          :swagger {:basePath "/"
                                    :info {:title "Recipes API reference"
                                           :description "The Recipes API is organized around REST"
                                           :version "1.0.0"}}
                          :handler (swagger/create-swagger-handler)}}])

(defn routes [env]
  (ring/ring-handler
   (ring/router [swagger-docs
                 ["/v1" (recipe/routes env)]]
                router-config)
   (ring/routes (swagger-ui/create-swagger-ui-handler {:path "/"}))))
