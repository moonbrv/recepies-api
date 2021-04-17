(ns recipes-api.recipe.handlers
  (:require
   [recipes-api.recipe.db :as recipe-db]
   [recipes-api.responses :as responses]
   [ring.util.response :as rr])
  (:import (java.util UUID)))

(defn list-all-recipes [db]
  (fn [req]
    (let [uid "auth0|5ef440986e8fbb001355fd9c"
          recipes (recipe-db/find-all-recipes db uid)]
      (rr/response recipes))))

(defn retrieve-recipe [db]
  (fn [req]
    (let [recipe-id "a1995316-80ea-4a98-939d-7c6295e4bb46"
          recipe (recipe-db/find-recipe-by-id db recipe-id)]
      (if (some? recipe)
        (rr/response recipe)
        (rr/not-found {:type "recipe-not-found"
                       :message "Recipe not found"
                       :data (str "recipe-id " recipe-id)})))))

(defn create-recipe! [db]
  (fn [req]
    (let [recipe-id (str (UUID/randomUUID))
          uid "auth0|5ef440986e8fbb001355fd9c"
          recipe (get-in req [:parameters :body])]
      (recipe-db/insert-recipe! db (assoc recipe
                                          :recipe-id recipe-id
                                          :uid uid))
      (rr/created (str responses/base-url "/recipes/" recipe-id)
                  {:recipe-id recipe-id}))))

(defn update-recipe! [db]
  (fn [req]
    (let [recipe-id "a1995316-80ea-4a98-939d-7c6295e4bb46" ;; (get-in req [:parameters :path :recipe-id])
          recipe (get-in req [:parameters :body])
          updated? (recipe-db/update-recipe! db (recipe-db/insert-recipe! db
                                                                          (assoc recipe :recipe-id recipe-id)))]
      (if updated?
        (rr/status 204)
        (rr/not-found {:recipe-id recipe-id})))))


(defn delete-recipe! [db]
  (fn [req]
    (let [recipe-id "a1995316-80ea-4a98-939d-7c6295e4bb46"
          deleted? (recipe-db/delete-recipe! db recipe-id)]
      (if deleted?
        (rr/status 204)
        (rr/not-found {:recipe-id recipe-id})))))
