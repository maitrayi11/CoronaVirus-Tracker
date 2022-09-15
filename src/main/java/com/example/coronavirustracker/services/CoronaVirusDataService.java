package com.example.coronavirustracker.services;

import com.example.coronavirustracker.models.Location;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class CoronaVirusDataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";


    private List<Location> allStats = new ArrayList<>();

    public List<Location> getAllStats() {
        return allStats;
    }
    public int getTotalReportedCases() {
        int sum = 0;
        for(Location location : allStats){
            sum += location.getLatestTotalCases();
        }
        return sum;
    }
    @PostConstruct
    @Scheduled(cron = "* * 1 * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<Location> newStats = new ArrayList<>();

        //To make request and get response from the git repo(open source) of data change
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request,HttpResponse.BodyHandlers.ofString());
        //System.out.println(httpResponse.body());

        //use to read the csv body
        StringReader csvBodyReader = new StringReader((httpResponse.body()));
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
        for (CSVRecord record : records) {
            Location location = new Location();
            location.setState(record.get("Province/State"));
            location.setCountry(record.get("Country/Region"));
            int latestDayCases = Integer.parseInt(record.get(record.size()-1));
            int prevDayCases = Integer.parseInt(record.get(record.size()-2));
            location.setLatestTotalCases(latestDayCases);
            location.setDiffFromPrevDay(latestDayCases - prevDayCases);
            //System.out.println(location);
            newStats.add(location);
        }
        this.allStats = newStats;
    }
}
