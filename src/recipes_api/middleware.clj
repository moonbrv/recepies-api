(ns recipes-api.middleware
  (:require
   [ring.middleware.jwt :as jwt]))

(defn wrap-auth [jwk-endpoint]
  {:name ::auth0
   :description "Middleware for auth0 authentication and authorization"
   :wrap (fn [handler]
           (jwt/wrap-jwt handler {:alg :RS256
                                  :jwk-endpoint jwk-endpoint}))})
