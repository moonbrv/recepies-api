(ns recipes-api.middleware
  (:require
   [ring.middleware.jwt :as jwt]
   [ring.util.response :as rr]
   [recipes-api.recipe.db :as recipe-db]
   [recipes-api.utils :refer [get-uid
                              get-recipe-id]]))

(defn wrap-auth [jwk-endpoint]
  {:name ::auth0
   :description "Middleware for auth0 authentication and authorization"
   :wrap (fn [handler]
           (jwt/wrap-jwt handler {:alg :RS256
                                  :jwk-endpoint jwk-endpoint}))})

(def wrap-check-recipe-owner
  {:name ::check-recipe-owner
   :description "Middleware to check if requestor is recipe owner"
   :wrap (fn [handler db]
           (fn [request]
             (let [uid (get-uid request)
                   recipe-id (get-recipe-id request)
                   recipe (recipe-db/find-recipe-by-id db recipe-id)]
               (if (= uid (:recipe/uid recipe))
                 (handler request)
                 (-> (rr/response {:message "You need to be recipe owner"
                                   :data (str "recipe-id " recipe-id)
                                   :type :authorization-required})
                     (rr/status 401))))))})
