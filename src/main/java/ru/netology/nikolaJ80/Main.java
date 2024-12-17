package ru.netology.nikolaJ80;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=zbSIwDOdspQEjDaoEnSI37bu77hboAUcEhEc978M";
    public static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        RequestConfig config = RequestConfig.custom()
                .setConnectTimeout(5000)
                .setSocketTimeout(30000)
                .setRedirectsEnabled(false)
                .build();

        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("My Test Service")
                .setDefaultRequestConfig(config)
                .build()) {

            HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

            try (CloseableHttpResponse response = httpClient.execute(request)) {

                String bodyBytes = EntityUtils.toString(response.getEntity());
                NASAResponse nasaResponse = mapper.readValue(bodyBytes, NASAResponse.class);
                String url = nasaResponse.getUrl();

                SaveImageByURL(config, url);
            }
        }
    }

    private static void SaveImageByURL(RequestConfig config, String url) throws IOException {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setUserAgent("My Test Service")
                .setDefaultRequestConfig(config)
                .build()) {
            HttpGet imageRequest = new HttpGet(url);
            CloseableHttpResponse imageResponse = httpClient.execute(imageRequest);

            String filename = url.substring(url.lastIndexOf("/") + 1);
            File dir = new File("Images");
            //Создание папки
            if (dir.mkdir()) {
                System.out.println("Folder created");
            } else {
                System.out.println("Folder not created");
            }

            File file = new File(dir, filename);

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(EntityUtils.toByteArray(imageResponse.getEntity()));
            }
        }
    }


}