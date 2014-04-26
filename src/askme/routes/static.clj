(ns askme.routes.static
  (:use compojure.core)
  (:require [askme.views.layout :as layout]
            [askme.util :as util]))

(defroutes static-routes
  (GET "/" [] (layout/render-html "home.html"))
  (GET "/about" [] (layout/render-html "about.html"))
  (GET "/login.html" [] (layout/render-html "/login.html"))
  (GET "/nav-menu.html" [] (layout/render-html "/nav-menu.html"))
  (GET "/ask-form.html" [] (layout/render-html "/ask-form.html"))
  (GET "/partials/recents.html" [] (layout/render-html "/partials/recents.html"))
  (GET "/partials/search.html" [] (layout/render-html "/partials/search.html")))
