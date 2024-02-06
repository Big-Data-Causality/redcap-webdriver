(ns redcap.webdriver-base-test
  (:use code.test)
  (:require [redcap.webdriver-base :as base]
            [etaoin.api :as e]
            [std.lib :as h]))

^{:refer redcap.webdriver-base/set-current-project :added "0.1"}
(fact "sets the current project id"
  ^:hidden

  (base/set-current-project "4567")
  => string?)

^{:refer redcap.webdriver-base/set-current-token :added "0.1"}
(fact "sets the current api token"
  ^:hidden
  
  (base/set-current-token "31BED902796A12603C2401AAD75E4275")
  => string?)

^{:refer redcap.webdriver-base/get-env-site :added "0.1"}
(fact "gets the environment site variables"
  ^:hidden

  (base/get-env-site ".env-site.sample")
  => map?)

^{:refer redcap.webdriver-base/end-driver :added "0.1"}
(fact "ends the current driver settings"
  ^:hidden

  (base/end-driver)
  => nil)

^{:refer redcap.webdriver-base/get-driver :added "0.1"}
(fact "gets the current etaoin driver"
  ^:hidden
  
  (base/get-driver {:headless true})
  => map?)

^{:refer redcap.webdriver-base/reset-driver :added "0.1"}
(fact "resets the existing driver")

^{:refer redcap.webdriver-base/goto-url :added "0.1"}
(fact "goto url, or stay on site if the same url"
  ^:hidden

  (e/go (base/get-driver)
        "https://redcapdemo.vanderbilt.edu/")
  =>
  (base/goto-url (base/get-driver)
                 "https://redcapdemo.vanderbilt.edu/"))

^{:refer redcap.webdriver-base/is-logged-out? :added "0.1"}
(fact "checks if the session has been logged out"
  ^:hidden

  (do (base/site-logout-action (base/get-driver)  {:site "https://redcapdemo.vanderbilt.edu/"})
      (base/is-logged-out? (base/get-driver)))
  => true)

^{:refer redcap.webdriver-base/site-version :added "0.1"}
(fact "gets the current redcap version from site"
  ^:hidden
  
  (do (e/go (base/get-driver)
            "https://redcapdemo.vanderbilt.edu/")
      (base/site-version
       (base/get-driver)))
  ;; "14.0.10"
  => string?)

^{:refer redcap.webdriver-base/site-login-action :added "0.1"}
(fact "completes the site login action"
  ^:hidden
  
  (do (e/go (base/get-driver) "https://redcapdemo.vanderbilt.edu/")
      
      (base/site-logout-action
       (base/get-driver)
       (base/get-env-site ".env-site.sample"))
      
      (base/site-login-action
       (base/get-driver)
       (base/get-env-site ".env-site.sample")))
  => map?)

^{:refer redcap.webdriver-base/site-logout-action :added "0.1"}
(fact "logs out of the site")

^{:refer redcap.webdriver-base/site-project-create-action :added "0.1"}
(fact "goes to the create project form"
  ^:hidden
  
  (do (e/go (base/get-driver) "https://redcapdemo.vanderbilt.edu/")
      
      (when (base/is-logged-out? (base/get-driver))
        (base/site-login-action
         (base/get-driver)
         (base/get-env-site ".env-site.sample")))
      
      (base/site-project-create-action
       (base/get-driver)
       (base/get-env-site ".env-site.sample"))
      
      (base/site-project-create-fill
       (base/get-driver)
       {:title (str "Project " (std.lib/uuid))
        :notes "Sample Project B"})
      
      (base/site-project-create-submit
       (base/get-driver)))
  => map?)

^{:refer redcap.webdriver-base/site-project-create-fill :added "0.1"}
(fact "fills out a create project form")

^{:refer redcap.webdriver-base/site-project-create-submit :added "0.1"}
(fact "submits the project create form")

^{:refer redcap.webdriver-base/site-project-list-action :added "0.1"}
(fact "goes to the site project list"
  ^:hidden
  
  (base/site-project-list-action
   (base/get-driver)
   (base/get-env-site ".env-site.sample"))
  => map?)

^{:refer redcap.webdriver-base/site-project-list-parse :added "0.1"}
(fact "parses the project list page"
  ^:hidden
  
  (do (base/site-project-list-action
       (base/get-driver)
       (base/get-env-site ".env-site.sample"))
      (base/site-project-list-parse
       (base/get-driver)))
  => vector?)

(comment
  "https://redcapdemo.vanderbilt.edu/redcap_v14.0.10/UserRights/index.php?pid=55818"
  "https://redcapdemo.vanderbilt.edu/index.php?action=myprojects")
