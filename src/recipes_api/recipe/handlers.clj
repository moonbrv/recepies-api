(ns recipes-api.recipe.handlers
  (:require
   [recipes-api.recipe.db :as recipe-db]
   [recipes-api.responses :as responses]
   [ring.util.response :as rr])
  (:import (java.util UUID)))

(defn get-uid [req]
  (get-in req [:claims :sub]))

(defn get-recipe-id [req]
  (get-in req [:parameters :path :recipe-id]))

(defn not-found-params [recipe-id]
  {:type "recipe-not-found"
   :message "Recipe not found"
   :data (str "recipe-id " recipe-id)})

(defn list-all-recipes [db]
  (fn [req]
    (let [uid (get-uid req)
          recipes (recipe-db/find-all-recipes db uid)]
      (rr/response recipes))))

(defn retrieve-recipe [db]
  (fn [req]
    (let [recipe-id (get-recipe-id req)
          recipe (recipe-db/find-recipe-by-id db recipe-id)]
      (if (some? recipe)
        (rr/response recipe)
        (rr/not-found (not-found-params recipe-id))))))

(defn create-recipe! [db]
  (fn [req]
    (let [recipe-id (str (UUID/randomUUID))
          uid (-> req :claims :sub)
          recipe (get-in req [:parameters :body])]
      (recipe-db/insert-recipe! db (assoc recipe
                                          :recipe-id recipe-id
                                          :uid uid))
      (rr/created (str responses/base-url "/recipes/" recipe-id)
                  {:recipe-id recipe-id}))))

(defn update-recipe! [db]
  (fn [req]
    (let [recipe-id (get-recipe-id req)
          recipe (get-in req [:parameters :body])
          updated? (recipe-db/update-recipe! db (assoc recipe :recipe-id recipe-id))]
      (if updated?
        (rr/status 204)
        (rr/not-found (not-found-params recipe-id))))))


(defn delete-recipe! [db]
  (fn [req]
    (let [recipe-id (get-recipe-id req)
          deleted? (recipe-db/delete-recipe! db recipe-id)]
      (if deleted?
        (rr/status 204)
        (rr/not-found (not-found-params recipe-id))))))
