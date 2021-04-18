(ns recipes-api.router
  (:require
   [reitit.coercion.spec :as coercion-spec]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring :as ring]
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.spec :as rspec]
   [reitit.dev.pretty :as pretty]
   [muuntaja.core :as m]
   [recipes-api.recipe.routes :as recipe]))

(def router-config {:exception pretty/exception
                    :validate rspec/validate
                    :data {:muuntaja m/instance
                           :coercion coercion-spec/coercion
                           :middleware [swagger/swagger-feature
                                        muuntaja/format-middleware
                                        exception/exception-middleware
                                        coercion/coerce-request-middleware
                                        coercion/coerce-response-middleware]}})

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
