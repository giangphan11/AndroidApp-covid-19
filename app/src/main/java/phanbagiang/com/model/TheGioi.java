package phanbagiang.com.model;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class TheGioi implements Serializable {
    private String country;
    private String cases;
    private String deaths;
    private String recovered;

    public TheGioi() {
    }

    public TheGioi(String country, String cases, String deaths, String recovered) {
        this.country = country;
        this.cases = cases;
        this.deaths = deaths;
        this.recovered = recovered;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCases() {
        return cases;
    }

    public void setCases(String cases) {
        this.cases = cases;
    }

    public String getDeaths() {
        return deaths;
    }

    public void setDeaths(String deaths) {
        this.deaths = deaths;
    }

    public String getRecovered() {
        return recovered;
    }

    public void setRecovered(String recovered) {
        this.recovered = recovered;
    }
}
