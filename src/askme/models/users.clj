(ns askme.models.users
  (:use [korma.core]
       [korma.db :only (defdb)]
       [black.water.korma :refer [decorate-korma!]])
  (:require [taoensso.timbre :as timbre]
            [noir.util.crypt :as sec] )
            )

(defentity users)

(defn create-user [email username password &
                   {:keys [is-admin first-name last-name is-active]
                    :or {:is-admin false :first-name nil :last-name nil :is-active true}}]

  (decorate-korma!)
  (let [ new-user (insert users
                      (values [{:email email
                                :username username
                                :password (sec/encrypt password)
                                :is_admin is-admin
                                :first_name first-name
                                :last_name last-name}]))]
  {:worked true
   :user new-user}))


(defn login [username password]
  (decorate-korma!)
  (let [res (select users
              (fields :password :id :username :email)
              (where (or {:username username}
                         {:email username})))]
    (timbre/info "Attempt login -> " res)
    (if (and (= 1 (count res))
             (->> (first res)
                  :password
                 (sec/compare password)))

      {:worked true
       :user (first res)}
      {:worked false})))


(defn count-by [field value]
  (decorate-korma!)
  (:cnt (first (select users
          (aggregate (count :*) :cnt)
            (where {field value})))))


(defmacro get-user [query]
  `(select users
          (where ~query)))

(comment (defn get-user [query]
  (decorate-korma!)
  (timbre/debug "INSTANCE OF QUERY " (fn? query))
  (cond
   (fn? query) (select users
                     (where (query)))
    :else (select users
            (where query))
    )))

