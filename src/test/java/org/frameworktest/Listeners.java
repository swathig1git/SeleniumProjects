package org.frameworktest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.WebDriver;
import org.resources.ExtentReporterNG;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.IOException;

public class Listeners extends BaseTest implements ITestListener  {

    ExtentReports extent =  ExtentReporterNG.getReportObject();
    ExtentTest test;
    @Override
    public void onTestStart(ITestResult result){

       test = extent.createTest(result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result){
        test.log(Status.PASS, "Test PAssed");

    }

    @Override
    public void onTestFailure(ITestResult result){
        test.fail(result.getThrowable());
        try {
            driver = (WebDriver) result.getTestClass().getRealClass().getField("driver").
                    get(result.getInstance());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String filePath = null;
        try {
            filePath = getScreenShot(result.getMethod().getMethodName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        test.addScreenCaptureFromPath(filePath, result.getMethod().getMethodName());

    }

    @Override
    public void onFinish(ITestContext context){
        extent.flush();

    }
}
