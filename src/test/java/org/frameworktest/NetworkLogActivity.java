package org.frameworktest;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v142.network.Network;
import org.openqa.selenium.devtools.v142.network.model.Request;
import org.openqa.selenium.devtools.v142.network.model.Response;


import java.util.Optional;

public class NetworkLogActivity {
    public static void main(String[] args){
        ChromeDriver driver = new ChromeDriver();
        DevTools devTools = driver.getDevTools();
        devTools.createSession();
        devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty()));

        devTools.addListener(Network.requestWillBeSent(), request ->
        {
            Request req = request.getRequest();
            System.out.println("Request URL: " + req.getUrl());
        });

        devTools.addListener(Network.responseReceived(), response->
        {
            Response res= response.getResponse();
            System.out.println("Response status: "+ res.getStatus());
        });

        driver.get("https://rahulshettyacademy.com/angularAppdemo/");
        driver.findElement(By.cssSelector("button[routerlink*='library']")).click();

        driver.quit();
    }
}
