(ns recipes-api.account.handlers
  (:require
   [ring.util.response :as rr]
   [recipes-api.account.db :as account-db]
   [recipes-api.utils :refer [get-uid
                              get-recipe-id]]))

(defn create-account! [db]
  (fn [request]
    (let [{:keys [sub picture]
           uname :name} (:claims request)]
      (account-db/insert-account! db {:uid sub
                                      :name uname
                                      :picture picture})
      (rr/status 204))))
