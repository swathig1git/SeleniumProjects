package org.utils;

import io.restassured.http.Cookies;
import java.util.Map;

/**
 * Represents a combined Selenium + API session state.
 * Includes cookies, localStorage, and sessionStorage.
 */
public class SessionState {
    public Cookies apiCookies;
    public Map<String, String> localStorage;
    public Map<String, String> sessionStorage;

    public SessionState(Cookies cookies,
                        Map<String, String> localStorage,
                        Map<String, String> sessionStorage) {
        this.apiCookies = cookies;
        this.localStorage = localStorage;
        this.sessionStorage = sessionStorage;
    }
}
