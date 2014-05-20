(defn weather_text_url [ahour] (str"http://forecast.weather.gov/MapClick.php?w0=t&w1=td&w2=wc&w3u=1&w6=rh&w8=rain&w9=snow&w10=fzg&w11=sleet&AheadHour="ahour"&Submit=Submit&FcstType=digital&textField1=43.08544&textField2=-77.67506&site=all&unit=0&dd=0&bw=0"))
(defn parse_weather_text [ahour]
    (let [
        t (slurp (weather_text_url ahour))
        t (subs t (. t indexOf "<table width=\"800\" border=\"0\" align=\"center\">"))
        t (subs t 0 (. t indexOf "</table>"))
        t (map second (re-seq #"<b>([\w\s/-]*)</b>" t))
        t (rest t)
        day (first t) t (rest t)
        t (if (re-find #"\d+/\d+" (first t)) (rest t) t)
        t1 (partition 24 (take (* 24 9) t)) t (drop (* 24 9) t)
        t (drop 2 t)
        t2 (partition 24 t)
        t (map #(apply concat %) (map vector t1 t2))
        t (merge {"day" day
                  "hour" (Integer/parseInt (first (first t)))}
                 (zipmap ["temperature" "dewpoint" "wind_chill" "humidity" "rain" "snow" "freezing_rain" "sleet"] (rest t)))
        ] t))
(defn to_yaml [o] (. (org.yaml.snakeyaml.Yaml.) dump o))
(defn simple_date [s] (. (java.text.SimpleDateFormat. s) format (java.util.Date.)))

(def now (simple_date "yyyy-MM-dd-HH-mm"))
(doseq [i [0 48 96]] (spit (str"dump/weather "now" "i".yaml") (to_yaml (parse_weather_text i))))