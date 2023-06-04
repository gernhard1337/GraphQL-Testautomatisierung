(defproject graphql-tester "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [http-kit "2.3.0"]
                 [javax.xml.bind/jaxb-api "2.3.0"]
                 [org.clojure/data.json "0.2.6"]
                 [clj-http "3.10.1"]
                 [org.clojure/test.check "1.1.0"]
                 [com.paren/serene "0.0.2"]
                 [metosin/malli "0.11.0"]
                 [com.walmartlabs/lacinia "1.2.1"]]
  :main ^:skip-aot graphql-tester.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all
                       :jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
