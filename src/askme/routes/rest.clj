(ns askme.routes.rest
  (:use compojure.core)
  (:require [ring.util.response :refer [content-type response]]
            [taoensso.timbre :as timbre]
            [noir.session :as session]
            [askme.models.questions-db :as questions]
            [askme.logic.user :as user]
            [environ.core :refer [env]]))

(def MAX-PAGE-SIZE ((env :rest) :max-page-size))

(defn- ask-with-user [question user]
  (timbre/info "User " user " asking question " question)
  (let [all-questions (questions/get-user-questions user 1 MAX-PAGE-SIZE)
        existing (questions/search-user-questions user question 1 MAX-PAGE-SIZE)]
    (if (nil? existing)
      (cons (questions/insert-question question user) existing)
      existing)))

(defn ask-anonymous [question]
  (timbre/info "Anonymous asking question " question)
  (questions/search-all-questions question 1 MAX-PAGE-SIZE))

(defn askme-question [question]
 (let [res (if-not (user/is-anonymous)
              (ask-with-user question (user/get-user))
              (ask-anonymous question))]
  (content-type (response {:response "ok" :result res}) "application/json")))

(defn get-recent []
  (content-type (response {:response "ok" :result (questions/search-all-questions "" 1 MAX-PAGE-SIZE)}) "application/json"))

(defn search-question [query user ]
  (timbre/info "Call to search question with query " query " and user " user)
  (let [res (questions/search-questions query 1 MAX-PAGE-SIZE user)]
     (if-not (empty? res)
        (timbre/info "Found matches " res)
        (timbre/info "No matches found"))
    (content-type (response {:response "ok" :results res}) "application/json")))

(defroutes askme-rest-routes
      (POST "/questions/ask"  {:keys [body-params]}
            (askme-question (get body-params "question")))
      ;(GET  "/questions/recents" [] (get-recent))
      (GET  "/user/question" {:keys [params]}
            (search-question  (get params :q) (session/get! :user))))
