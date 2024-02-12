(ns redcap.webdriver-base
  (:require [std.string :as str]
            [etaoin.api :as e]))

(def ^:dynamic *current-project* nil)

(def ^:dynamic *current-token* nil)

(defonce +current+
  (atom nil))

(defn set-current-project
  "sets the current project id"
  {:added "0.1"}
  [pid]
  (alter-var-root #'*current-project* (fn [_] pid)))

(defn set-current-token
  "sets the current api token"
  {:added "0.1"}
  [token]
  (alter-var-root #'*current-token* (fn [_] token)))

(defn get-env-site
  "gets the environment site variables"
  {:added "0.1"}
  [& [filename]]
  (read-string (slurp (or filename
                          ".env-site"))))

(defn end-driver
  "ends the current driver settings"
  {:added "0.1"}
  [& [{:keys [headless] :as opts}]]
  (when @+current+
    (e/quit @+current+)
    (reset! +current+ nil)))

(defn get-driver
  "gets the current etaoin driver"
  {:added "0.1"}
  [& [{:keys [headless] :as opts}]]
  (or @+current+
      (reset! +current+
              (e/chrome (merge {:site [1200 1200]} opts)))))

(defn reset-driver
  "resets the existing driver"
  {:added "0.1"}
  [& [opts]]
  (do (end-driver)
      (get-driver opts)))

(defn goto-url
  "goto url, or stay on site if the same url"
  {:added "0.1"}
  [driver url]
  (let [current (e/get-url driver)]
    (when (not= current url)
      (e/go driver url))
    {:sessionId (:session driver)
     :status 0, :value nil}))

(defn is-logged-out?
  "checks if the session has been logged out"
  {:added "0.1"}
  [driver]
  (e/exists? driver {:id "rc-login-form"}))

(defn site-version
  "gets the current redcap version from site"
  {:added "0.1"}
  [driver]
  (second
   (str/split
    (e/get-element-text (get-driver)
                        {:xpath "//div[@id='footer']//a"})
    #" ")))

(defn site-login-action
  "completes the site login action"
  {:added "0.1"}
  [driver {:keys [username
                  password]
           :as opts}]
  (doto driver
    (e/fill  {:tag :input :id "username"} username)
    (e/fill  {:tag :input :id "password"} password)
    (e/click {:tag :button :id "login_btn"})))

(defn site-logout-action
  "logs out of the site"
  {:added "0.1"}
  [driver {:keys [site]}]
  (e/go driver (str/replace-all (str site "/index.php?logout=1")
                                "//"
                                "/"))
  (e/go driver site))

(defn site-project-create-action
  "goes to the create project form"
  {:added "0.1"}
  [driver {:keys [site]}]
  (e/go driver (str/replace-all (str site "/index.php?action=create")
                                "//"
                                "/")))

(defn site-project-create-fill
  "fills out a create project form"
  {:added "0.1"}
  [driver {:keys [title
                  notes]}]
  (doto driver
    (e/fill  {:tag :input :name "app_title"} title)
    (e/click [{:tag :select :name "purpose"} {:value "0"}])
    (e/fill  {:tag :textarea :name "project_note"} notes)
    #_(e/select [{:tag :input :id "project_template_radio0"} {:value "0"}])))

(defn site-project-create-submit
  "submits the project create form"
  {:added "0.1"}
  [driver]
  (e/click driver {:xpath "//form[@name='createdb']//button"}))

(defn site-project-list-action
  "goes to the site project list"
  {:added "0.1"}
  [driver  {:keys [site]}]
  (e/go driver (str/replace-all (str site "/index.php?action=myprojects")
                                "//"
                                "/")))

(defn site-project-list-parse
  "parses the project list page"
  {:added "0.1"}
  [driver]
  (let [text-fn    (fn [el]
                     (e/get-element-text-el
                      driver
                      el))
        name-elems (e/query-all driver
                                {:xpath "//table[@id='table-proj_table']//a[@class='aGrid']"})
        name-all (map text-fn name-elems)
        pid-elems (e/query-all driver
                                {:xpath "//table[@id='table-proj_table']/tbody/tr/td[position()=2]"})
        pid-all (map text-fn pid-elems)]
    (mapv (fn [name pid]
            {:name name :pid pid})
          name-all
          pid-all)))

