(ns recipes-api.middleware
  (:require
   [ring.middleware.jwt :as jwt]
   [ring.util.response :as rr]
   [recipes-api.recipe.db :as recipe-db]
   [recipes-api.utils :refer [get-uid
                              get-recipe-id]]))

(def wrap-auth
  {:name ::auth0
   :description "Middleware for auth0 authentication and authorization"
   :wrap (fn [handler jwk-endpoint]
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

(def wrap-managed-recipes
  {:name ::manage-recipes
   :description "Middleware to check if user able to manage recipes"
   :wrap (fn [handler {:keys [auth0-roles-api-identifer
                              auth0-manage-role-name] :as env}]
           (fn [request]
             (let [roles-key (str auth0-roles-api-identifer "/roles")
                   roles (set (get-in request [:claims roles-key]))]
               (if (contains? roles auth0-manage-role-name)
                 (handler request)
                 (-> (rr/response {:message "You need a cook to manage recipes"
                                   :data (:uri request)
                                   :type :authorization-required})
                     (rr/status 401))))))})
