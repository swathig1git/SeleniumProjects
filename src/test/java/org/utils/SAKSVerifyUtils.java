package org.utils;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class SAKSVerifyUtils {
    public static boolean verifyBrowseByButtons(WebDriver driver, List<WebElement> actualElements, List<String> expectedButtonNames) {
        // Extract text
        List<String> actualButtonNames = actualElements.stream()
                .map(element -> {
                    SAKSBrowserUtilities.scrollIntoView(driver, element);  // scroll before reading
                    return element.getText().trim();
                })
                .toList();

        // Find missing buttons
        List<String> missing = expectedButtonNames.stream()
                .filter(e -> !actualButtonNames.contains(e))
                .toList();

        if (!missing.isEmpty()) {
            System.out.println("Missing buttons: " + missing);
            return false;
        }

        return true;
    }

}
