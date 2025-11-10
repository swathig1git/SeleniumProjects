package org.utils;

import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.JavascriptExecutor;

import java.util.*;

public class SessionSyncUtil {

    // ✅ Capture full Selenium session (cookies + localStorage + sessionStorage)
    public static SessionState captureSessionFromSelenium(WebDriver driver) {
        Cookies cookies = exportCookiesFromSelenium(driver);
        Map<String, String> local = getAllLocalStorageItems(driver);
        Map<String, String> session = getAllSessionStorageItems(driver);
        return new SessionState(cookies, local, session);
    }

    // ✅ Apply captured session to API calls
    public static io.restassured.specification.RequestSpecification applySessionToApi(SessionState session) {
        io.restassured.specification.RequestSpecification spec = io.restassured.RestAssured.given();

        // Add cookies
        if (session.apiCookies != null) {
            spec.cookies(session.apiCookies);

        }

        // Optionally add Authorization headers if token exists in localStorage
        if (session.localStorage != null) {
            String token = session.localStorage.get("authToken"); // adjust key as needed
            if (token != null) {
                spec.header("Authorization", "Bearer " + token);
            }
        }

        return spec;
    }

    // ✅ Convert Selenium cookies to RestAssured cookies
    public static Cookies exportCookiesFromSelenium(WebDriver driver) {
        Set<org.openqa.selenium.Cookie> seleniumCookies = driver.manage().getCookies();
        Set<io.restassured.http.Cookie> restCookies = new HashSet<>();

        for (org.openqa.selenium.Cookie c : seleniumCookies) {
            io.restassured.http.Cookie.Builder builder =
                    new io.restassured.http.Cookie.Builder(c.getName(), c.getValue())
                            .setDomain(c.getDomain())
                            .setPath(c.getPath())
                            .setSecured(c.isSecure());

            // ✅ Only set expiry if it's not null
            if (c.getExpiry() != null) {
                builder.setExpiryDate(c.getExpiry());
            }

            restCookies.add(builder.build());
        }

        return new io.restassured.http.Cookies(
                restCookies.toArray(new io.restassured.http.Cookie[0])
        );
    }

    // ✅ Fetch all localStorage items
    @SuppressWarnings("unchecked")
    public static Map<String, String> getAllLocalStorageItems(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object result = js.executeScript(
                "let items = {}; " +
                        "for (let i = 0; i < localStorage.length; i++) { " +
                        "  let key = localStorage.key(i); " +
                        "  items[key] = localStorage.getItem(key); " +
                        "} " +
                        "return items;"
        );
        return (Map<String, String>) result;
    }

    // ✅ Fetch all sessionStorage items
    @SuppressWarnings("unchecked")
    public static Map<String, String> getAllSessionStorageItems(WebDriver driver) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        Object result = js.executeScript(
                "let items = {}; " +
                        "for (let i = 0; i < sessionStorage.length; i++) { " +
                        "  let key = sessionStorage.key(i); " +
                        "  items[key] = sessionStorage.getItem(key); " +
                        "} " +
                        "return items;"
        );
        return (Map<String, String>) result;
    }
}
