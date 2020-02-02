package com.test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.*;
import org.testng.annotations.Test;

import com.test.utils.ConfigUtils;
import com.test.utils.TestPage;

public class TestUndostres {
	WebDriver driver;
	WebDriverWait wait;
	TestPage testPage;
	Properties configProp;

	/**
	 * Browser Initialization
	 * 
	 * @throws Exception
	 */
	@BeforeSuite
	public void initialize() throws Exception {
		System.out.println("Initializing Driver");
		configProp = ConfigUtils.readPropertyFile("config/config.properties");
		System.setProperty("webdriver.chrome.driver", configProp.getProperty("BROWSER_PATH"));
		String browser = configProp.getProperty("BROWSER");
		if (browser.equalsIgnoreCase("Chrome")) {
			driver = new ChromeDriver();
		} else if (browser.equalsIgnoreCase("Firefox")) {
			driver = new FirefoxDriver();
		} else if (browser.equalsIgnoreCase("IE")) {
			driver = new InternetExplorerDriver();
		}
		System.out.println("Initialized " + browser + " Driver");
		driver.manage().window().maximize();
		wait = new WebDriverWait(driver, 15);
		testPage = new TestPage(driver, configProp);
	}

	/**
	 * Removes existing screenshots and csv data
	 */
	@BeforeTest
	public void clear() {
		System.out.println("clear() : method start");
		System.out.println(configProp.getProperty("SCREENSHOT_PATH"));
		testPage.removeFiles(configProp.getProperty("SCREENSHOT_PATH")); // clearing
																			// screenshots
		System.out.println("clear() : method end");
	}

	/**
	 * Screenshot on Failure
	 * 
	 * @throws IOException
	 */
	@AfterMethod
	public void takeScreenShotOnFailure(ITestResult testResult) throws IOException {
		if (testResult.getStatus() == ITestResult.FAILURE) {
			System.out.println(testResult.getStatus() + " Screenshot Status [2: FAILURE]");
			File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			FileUtils.copyFile(scrFile,
					new File(configProp.getProperty("SCREENSHOT_PATH") + testResult.getName() + ".jpg"));
		} else {
			System.out.println(testResult.getStatus() + " Screenshot Status [1: SUCCESS]");
		}
	}

	/**
	 * Screenshot on Failure
	 * 
	 * @throws Exception
	 */
	@AfterSuite
	public void tearDown() throws Exception {
		driver.quit();
	}

	/**
	 * Verify Undestores URL is successfully opened
	 * 
	 */
	@Test
	public void launchUndestores() {
		boolean verifyLaunch = testPage.openUndostres();
		Assert.assertTrue(verifyLaunch);
	}

	/**
	 * Selection of Operator
	 * 
	 */
	@Test(dependsOnMethods = "launchUndestores")
	public void rechargeCell() {
		String operatorVerify = testPage.operatorSelect();
		Assert.assertEquals(operatorVerify, configProp.getProperty("OPERATOR").toLowerCase());
		System.out.println("Operator has been Successfully Selected.......");
	}

	/**
	 * Entering the cell and Amount Details
	 * 
	 */
	@Test(dependsOnMethods = "rechargeCell")
	public void cellDetails() {
		Boolean cellDetailsVerfigy = testPage.numberAndAmount();
		Assert.assertTrue(cellDetailsVerfigy);
	}

	/**
	 * Proceed for Payment
	 * 
	 * @throws Exception
	 * 
	 */
	@Test(dependsOnMethods = "cellDetails")
	public void signInForPayment() throws Exception {
		boolean verifySignIn = testPage.signIn();
		Assert.assertTrue(verifySignIn);
	}

	/**
	 * Card Details
	 * 
	 * @throws Exception
	 * 
	 */
	@Test(dependsOnMethods = "signInForPayment")
	public void cardPayment() throws Exception {
		testPage.accountDetails();
		boolean paymentVerify = testPage.paymentProceed();
		Assert.assertTrue(paymentVerify);
	}

	/**
	 * Final Details
	 * 
	 * 
	 */
	@Test(dependsOnMethods = "cardPayment")
	public void finalDetails() {
		boolean finalDetailsVerify = testPage.finalDetailsAndCaptcha();
		Assert.assertTrue(finalDetailsVerify);
	}

	/**
	 * successful Payment
	 * 
	 * @throws Exception
	 * 
	 * 
	 */
	@Test(dependsOnMethods = "finalDetails")
	public void successfulPayment() throws Exception {
		String actualMessage = testPage.paymentSuccessful();
		Assert.assertEquals(actualMessage, "Correo/Movil o contraseña no válido!");
		System.out.println("User Entered Incorrect Username or Password.....");
	}
}
