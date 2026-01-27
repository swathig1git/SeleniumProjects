package org.SAKSTests;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features = "src/test/resources/features/SAKSProductFilterCommonCucumber.feature",
    glue = "org.SAKSTests",
    plugin = {"pretty", "html:target/cucumber-reports-saks.html"}
)
public class SAKSProductFilterRunner extends AbstractTestNGCucumberTests {
}
