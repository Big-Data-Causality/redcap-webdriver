(ns redcap.webdriver-signup-test
  (:use code.test)
  (:require [redcap.webdriver-signup :as signup]
            [redcap.webdriver-base :as base]
            [etaoin.api :as e]
            [std.lib :as h]))

^{:refer redcap.webdriver-signup/signup-fill-form :added "0.1"}
(fact "prefills the trial form"
  ^:hidden
  
  (do (e/go (base/get-driver) signup/*demo-site*)
      (signup/signup-fill-form
       (base/get-driver)
       {:email "test0002@zcaudate.xyz"
        :first-name "John"
        :last-name   "Smith"
        :organisation-name "-"
        :organisation-type :for_profit
        :country "AU"
        :reason  :check_out_latest_features})
      (e/get-element-value
       (base/get-driver)
       {:tag :input :name "email"}))
  => "test0002@zcaudate.xyz")

^{:refer redcap.webdriver-signup/signup-trial :added "0.1"}
(fact "creates a trial account"
  ^:hidden
  
  (signup/signup-trial
   {:email "zcaudate@outlook.com"
    :first-name "John"
    :last-name   "Smith"
    :organisation-name "-"
    :organisation-type :for_profit
    :country "AU"
    :reason  :check_out_latest_features})
  => [false :already-created]
  
  (signup/signup-trial
   {:email (str (h/uuid) "@outlook.com")
    :first-name "Test"
    :last-name   "User"
    :organisation-name "-"
    :organisation-type :for_profit
    :country "AU"
    :reason  :check_out_latest_features})
  => [true])


(comment
 
  (signup-trial
   {:email "test0003@zcaudate.xyz"
    :first-name "John"
    :last-name   "Smith"
    :organisation-name "-"
    :organisation-type :for_profit
    :country "AU"
    :reason  :check_out_latest_features}))
