(ns recipes-api.recipe.handlers
  (:require
   [recipes-api.recipe.db :as recipe-db]
   [recipes-api.responses :as responses]
   [ring.util.response :as rr]
   [recipes-api.utils :refer [get-uid
                              get-recipe-id]])
  (:import (java.util UUID)))



(defn not-found-recipe [recipe-id]
  {:type "recipe-not-found"
   :message "Recipe not found"
   :data (str "recipe-id " recipe-id)})

(defn not-found-step [step-id]
  {:type "step-not-found"
   :message "Step not found"
   :data (str "step-id " step-id)})

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
        (rr/not-found (not-found-recipe recipe-id))))))

(defn create-recipe! [db]
  (fn [req]
    (let [recipe-id (str (UUID/randomUUID))
          uid (get-uid req)
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
        (rr/not-found (not-found-recipe recipe-id))))))


(defn delete-recipe! [db]
  (fn [req]
    (let [recipe-id (get-recipe-id req)
          deleted? (recipe-db/delete-recipe! db recipe-id)]
      (if deleted?
        (rr/status 204)
        (rr/not-found (not-found-recipe recipe-id))))))

(defn favorite-recipe! [db]
  (fn [req]
    (let [uid (get-uid req)
          recipe-id (get-recipe-id req)
          updated? (recipe-db/favorite-recipe! db recipe-id uid)]
      (if updated?
        (rr/status 204)
        (rr/not-found (not-found-recipe recipe-id))))))

(defn unfavorite-recipe! [db]
  (fn [req]
    (let [uid (get-uid req)
          recipe-id (get-recipe-id req)
          updated? (recipe-db/unfavorite-recipe! db recipe-id uid)]
      (if updated?
        (rr/status 204)
        (rr/not-found (not-found-recipe recipe-id))))))

(defn create-step! [db]
  (fn [req]
    (let [step-id (str (UUID/randomUUID))
          recipe-id (get-recipe-id req)
          step (get-in req [:parameters :body])]
      (recipe-db/insert-step! db (assoc step
                                        :step-id step-id
                                        :recipe-id recipe-id))
      (rr/created (str responses/base-url "/recipes/" recipe-id)
                  {:step-id step-id}))))

(defn update-step! [db]
  (fn [req]
    (let [step (get-in req [:parameters :body])
          updated? (recipe-db/update-step! db step)]
      (if updated?
        (rr/status 204)
        (rr/not-found (not-found-step (:step-id step)))))))

(defn delete-step! [db]
  (fn [req]
    (let [step-id (get-in req [:parameters :body :step-id])
          deleted? (recipe-db/delete-step! db step-id)]
      (if deleted?
        (rr/status 204)
        (rr/not-found (not-found-step step-id))))))
