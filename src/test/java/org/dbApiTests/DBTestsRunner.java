package org.dbApiTests;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features",
    glue = "org.dbApiTests",
    plugin = {"pretty", "html:target/cucumber-reports.html"}
)
public class DBTestsRunner extends AbstractTestNGCucumberTests {
}
