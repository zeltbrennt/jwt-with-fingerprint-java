package main

import (
	"encoding/json"
	"log"
	"net/http"
)

type Data struct {
	Value string `json:"value"`
}

func main() {

	http.HandleFunc("/collect", func(w http.ResponseWriter, r *http.Request) {

		w.Header().Set("Content-Type", "application/json")
		w.Header().Set("Access-Control-Allow-Origin", "*")
		w.Header().Set("Access-Control-Allow-Methods", "POST, OPTIONS")
		w.Header().Set("Access-Control-Allow-Headers", "Content-Type")

		if r.Method == http.MethodPost {
			var data Data
			err := json.NewDecoder(r.Body).Decode(&data)
			if err != nil {
				http.Error(w, "Error", http.StatusBadRequest)
				return
			}
			log.Printf("Joink: %+v\n", data.Value)
		}
	})

	log.Println("Starting very bad server >:D")
	http.ListenAndServe(":9090", nil)

}
