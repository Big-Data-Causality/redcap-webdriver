(ns redcap.webdriver-signup
  (:require [std.string :as str]
            [std.lib :as h]
            [etaoin.api :as e]
            [redcap.webdriver-base :as base]))

(def ^:dynamic *demo-site*
  "https://redcapdemo.vanderbilt.edu/trial/")

(def +organisation_types+
  #{:non_profit_academic
    :non_profit_private
    :for_profit
    :government
    :military
    :other})

(def +reason-types+
  #{:evaluate_software
    :learn_about_specfic_feature
    :check_out_latest_features
    :other})

(defn signup-fill-form
  "prefills the trial form"
  {:added "0.1"}
  [driver {:keys [email
                  first-name
                  last-name
                  organisation-name
                  organisation-type
                  country
                  reason]}]
  (doto driver
    (e/fill  {:tag :input :name "email"} email)
    (e/fill  {:tag :input :name "first_name"} first-name)
    (e/fill  {:tag :input :name "last_name"} last-name)
    (e/fill  {:tag :input :name "organization_name"} organisation-name)
    (e/click [{:tag :select :name "organization_type"} {:value (str/snake-case (h/strn organisation-type))}])
    (e/click [{:tag :select :name "country"} {:value country}])
    (e/click [{:tag :select :name "reason_for_demo"} {:value (str/snake-case (h/strn reason))}])))

(defn signup-trial
  "creates a trial account"
  {:added "0.1"}
  [account & [driver]]
  (let [driver (or driver (base/get-driver))]
    (e/go driver *demo-site*)
    (signup-fill-form driver account)
    (e/click driver {:xpath "//form[@id='trial_form']//button"})
    (cond (e/has-text? (base/get-driver)
                       "Account already created")
          [false :already-created]

          (e/has-text? (base/get-driver)
                       "Account successfully created")
          [true]

          (e/has-text? (base/get-driver)
                       "Some required fields not entered")
          [false :missing-fields]

          :else
          [true])))
