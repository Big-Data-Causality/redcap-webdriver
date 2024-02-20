(ns redcap.webdriver
  (:require [std.string :as str]
            [std.lib :as h]
            [etaoin.api :as e]
            [redcap.webdriver-base :as base]
            [redcap.webdriver-signup :as signup]))

(h/intern-in 
  base/end-driver
  base/get-driver
  base/get-env-site
  base/reset-driver)

(defn get-current
  []
  {:site  base/*current-project*
   :token base/*current-token*})

(defn get-token
  []
  base/*current-token*)

(defn login
  "logs in to the site"
  {:added "0.1"}
  [& [{:keys [site]
       :as opts}
      driver]]
  (let [{:keys [site]
         :as opts}  (or opts   (base/get-env-site))
        driver (or driver (base/get-driver))
        _       (base/goto-url driver site)]
    (cond (base/is-logged-out? driver)
          (base/site-login-action driver opts)

          :else
          {:sessionId (:session driver)
           :status 0, :value nil})))

(defn logout
  "logs out of the site"
  {:added "0.1"}
  [& [{:keys [site]
       :as opts}
      driver]]
  (base/site-logout-action (or driver (base/get-driver))
                           (or opts   (base/get-env-site))))

(defn project-create
  "creates a project given environment"
  {:added "0.1"}
  [{:keys [title
           notes]}
   & [{:keys [site]
       :as opts}
      driver]]
  (let [{:keys [site]
         :as opts}   (or opts   (base/get-env-site))
        driver (or driver (base/get-driver))
        _ (base/site-project-create-action
           driver
           {:site site})
        _ (base/site-project-create-fill
           driver
           {:title title :notes notes})
        _ (base/site-project-create-submit
           driver)
        url (e/get-url driver)
        last {:url url
              :pid (second (re-find #"pid=(\d+)" url))}
        _   (alter-var-root #'base/*current-project*
                            (fn [_] last))]
    last))

(defn project-api-enabled?
  "checks that the project api is enabled"
  {:added "0.1"}
  [pid & [{:keys [site
                  username]
           :as opts}
          driver]]
  (let [{:keys [site
                username]
         :as opts}   (or opts   (base/get-env-site))
        driver (or driver (base/get-driver))
        _ (base/goto-url driver site)
        version (base/site-version driver)
        main-url  (str site "/redcap_v" version "/index.php?pid=" pid)
        _         (base/goto-url driver main-url)]
    (e/exists? driver "//a[text()='API Playground']")))

(defn project-api-toggle
  "toggles the project api"
  {:added "0.1"}
  [pid & [{:keys [site
                  username]
           :as opts}
          driver]]
  (let [{:keys [site
                username]
         :as opts}   (or opts   (base/get-env-site))
        driver (or driver (base/get-driver))
        _ (base/goto-url driver site)
        version (base/site-version driver)
        api-url  (str site "/redcap_v" version "/UserRights/index.php?pid=" pid)
        _ (base/goto-url driver api-url)
        _ (e/click driver
                   {:xpath (str "//a[@userId='" username "']") })
        _ (e/click driver
                   {:xpath "//div[@id='tooltipBtnSetCustom']/button"})
        _ (e/wait-exists driver
                         {:tag :span :id "ui-id-9"
                          #_#_:xpath "//[@aria-describedby='editUserPopup']"})
        _ (e/click driver
                   {:tag :input
                    :name "api_import"})
        _ (e/click driver
                   {:tag :input
                    :name "api_export"})
        _ (e/click driver
                   {:xpath "//button[text()='Save Changes']"})
        _ (Thread/sleep 1000)]
    (project-api-enabled? pid opts driver)))

(defn project-api-token
  "gets the project token"
  {:added "0.1"}
  [pid & [{:keys [site
                  username]
           :as opts}
          driver]]
  (let [{:keys [site
                username]
         :as opts}   (or opts   (base/get-env-site))
        driver (or driver (base/get-driver))
        _       (base/goto-url driver site)
        version (base/site-version driver)
        api-url  (str site "/redcap_v" version "/API/project_api.php?pid=" pid)
        _ (base/goto-url driver api-url)
        token  (e/get-element-attr (base/get-driver)
                                   {:tag :input :id "apiTokenId"}
                                   "value")
        token  (cond (empty? token)
                     (do (e/click driver
                                  {:xpath "//div[@id='apiReqBoxId']//div[@class='chklistbtn']/button"})
                         (Thread/sleep 500)
                         (e/get-element-attr (base/get-driver)
                                             {:tag :input :id "apiTokenId"}
                                             "value"))

                     :else token)
        _ (alter-var-root #'base/*current-token* (fn [_] token))
        _ (base/goto-url driver (str site "/redcap_v" version "/index.php?pid=" pid))]
    token))

(defn project-list
  "lists all projects in the given environment"
  {:added "0.1"}
  [& [{:keys [site]
       :as opts}
      driver]]
  (let [{:keys [site]
         :as opts}   (or opts   (base/get-env-site))
        driver (or driver (base/get-driver))
        _ (base/site-project-list-action
           driver
           {:site site})]
    (base/site-project-list-parse
     driver)))

(defn project-delete
  "deletes a newly created project"
  {:added "0.1"}
  [pid & [{:keys [site]
           :as opts}
          driver]]
  (let [{:keys [site]
         :as opts}   (or opts   (base/get-env-site))
        driver (or driver (base/get-driver))
        _ (base/goto-url driver site)
        version (base/site-version driver)
        delete-url  (str site "/redcap_v" version "/ProjectSetup/other_functionality.php?pid=" pid)
        _ (base/goto-url driver delete-url)
        _ (e/click driver
                   {:xpath "//tr[@id='row_delete_project']//button"})
        _ (e/wait-exists driver
                         {:tag :input :id "delete_project_confirm"})
        _ (e/fill driver
                  {:tag :input :id "delete_project_confirm"}
                  "DELETE")
        _ (e/click (base/get-driver)
                   {:xpath "//div[@class='ui-dialog-buttonset']//button[position()=2]"})
        _ (e/click-el (base/get-driver)
                      (second
                       (e/query-all (base/get-driver)
                                    {:xpath "//div[@class='ui-dialog-buttonset']//button[position()=2]"})))
        _ (e/wait-exists driver
                         {:tag :span :id "ui-id-3"})
        _ (e/click-el (base/get-driver)
                      (second
                       (e/query-all (base/get-driver)
                                    {:xpath "//div[@class='ui-dialog-buttonset']//button[position()=1]"})))]
    (base/site-project-list-parse driver)))

(defn project-delete-all
  "deletes all projects in the environment"
  {:added "0.1"}
  [& [{:keys [site]
       :as opts}
      driver]]
  (let [opts   (or opts   (base/get-env-site))
        driver (or driver (base/get-driver))
        projects (project-list opts driver)]
    (doseq [p projects]
      (project-delete (:pid p) opts driver))
    projects))

(defn project-new
  "creates a brand new project"
  {:added "0.1"}
  [{:keys [title
           notes]}
   & [{:keys [site]
       :as opts}
      driver]]
  (let [{:keys [site]
         :as opts}   (or opts   (base/get-env-site))
        driver (or driver (base/get-driver))
        {:keys [url pid]} (project-create {:title title :notes notes} opts driver)
        enabled   (project-api-toggle pid opts driver)
        token     (project-api-token  pid opts driver)]
    {:token token
     :url   url
     :pid   pid}))
