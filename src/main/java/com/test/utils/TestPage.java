package com.test.utils;

import java.io.File;
import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class TestPage {
	WebDriver driver;
	WebDriverWait wait;
	JavascriptExecutor js;
	Actions action;
	Properties configProp;

	public TestPage(WebDriver webDriver, Properties configProp) {
		this.driver = webDriver;
		this.js = (JavascriptExecutor) webDriver;
		this.action = new Actions(webDriver);
		this.configProp = configProp;
	}

	public void removeFiles(String path) {
		File file = new File(path);
		File[] files = file.listFiles();
		if (null != files) {
			for (File f : files) {
				if (f.isFile() && f.exists()) {
					f.delete();
					System.out.println("successfully deleted");
				} else {
					System.out.println("cant delete a file due to open or error");
				}
			}
		}
	}

	public boolean openUndostres() {
		System.out.println("Launching Undostres...");
		driver.navigate().to(configProp.getProperty("LAUNCH_URL"));
		try {
			wait = new WebDriverWait(driver, 30);
			WebElement rechargeCell = wait
					.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("div[to-do='mobile']")));
			String loginVerify = rechargeCell.findElement(By.tagName("h1")).getText();
			if (loginVerify.equalsIgnoreCase("Recarga Celular")) {
				System.out.println("Welcome to Undostres.....");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String operatorSelect() {
		String operatorValue = configProp.getProperty("OPERATOR").toLowerCase();
		WebElement operatorClick = driver.findElement(By.cssSelector("input[suggest='mobile-operator']"));
		operatorClick.click();
		wait = new WebDriverWait(driver, 10);
		WebElement operator = driver
				.findElement(By.cssSelector(".suggestion>ul>li[data-name='" + operatorValue + "']"));
		operator.click();
		String selectedOperator = operatorClick.getAttribute("data-value").toLowerCase();
		return selectedOperator;
	}

	public boolean numberAndAmount() {
		wait = new WebDriverWait(driver, 10);
		WebElement number = wait.until(
				ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='mobile'][oninput^='checkMob']")));
		number.click();
		number.sendKeys(configProp.getProperty("NUMBER"));
		wait = new WebDriverWait(driver, 15);
		WebElement amount = wait.until(
				ExpectedConditions.elementToBeClickable(By.cssSelector("input[suggest='mobile-operator_amount']")));
		amount.click();
		wait = new WebDriverWait(driver, 5);
		WebElement operator = driver.findElement(
				By.cssSelector(".suggestion>ul>li[data-show^='$" + configProp.getProperty("AMOUNT") + "']"));
		operator.click();
		String selectedAmount = amount.getAttribute("data-value");
		if (selectedAmount.equals(configProp.getProperty("AMOUNT"))) {
			System.out.println("Cell Number is: " + configProp.getProperty("NUMBER") + "....");
			System.out.println("Amount to be recharged is: $" + configProp.getProperty("AMOUNT") + "....");
			return true;
		} else
			return false;
	}

	public boolean signIn() throws Exception {
		Thread.sleep(5000);
		WebElement signIn = driver.findElement(By.cssSelector("div[class^='box'][to-do='mobile'] button"));
		js.executeScript("arguments[0].click();", signIn);
		wait = new WebDriverWait(driver, 20);
		WebElement summary = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("span[class^='summary-message']")));
		boolean number = false, amount = false, mode = false;
		String summaryVerify = summary.getText();
		for (String summaryValue : summaryVerify.split("\\s+")) {
			if (summaryValue.equals(configProp.getProperty("NUMBER"))) {
				number = true;
			} else if (summaryValue.equals("$" + configProp.getProperty("AMOUNT"))) {
				amount = true;
			} else if (summaryValue.equalsIgnoreCase("Recarga")) {
				mode = true;
			}
		}
		if (number == true && amount == true && mode == true) {
			System.out.println("Sign In Done Successfully....Proceeding with the payment....");
			return true;
		} else
			return false;
	}

	public void accountDetails() throws Exception {
		String[] cardValues = { "CARDNAME", "CARDNO", "EXPMONTH", "EXPYEAR", "CVVNO" };
		WebElement paymentMode = driver.findElement(By.cssSelector("a[class*='select-card']"));
		paymentMode.click();
		for (String cardInfo : cardValues) {
			WebElement cardValue = wait.until(ExpectedConditions.elementToBeClickable(
					By.cssSelector("div.card-info-box input[name='" + cardInfo.toLowerCase() + "']")));
			cardValue.click();
			cardValue.sendKeys(configProp.getProperty(cardInfo));
			System.out.println(configProp.getProperty(cardInfo) + " : " + cardInfo);
		}
		System.out.println("Card Details Entered Successfully...");
		Thread.sleep(2000);
		WebElement emailBlock = driver.findElement(By.cssSelector("div#email_block"));
		WebElement email = emailBlock.findElement(By.cssSelector("input[name='txtEmail']"));
		email.click();
		email.sendKeys(configProp.getProperty("COREO_ELECTONICO"));
		Thread.sleep(2000);
	}

	public boolean paymentProceed() {
		WebElement payButton = driver.findElement(By.cssSelector("button[name=formsubmit]"));
		if (payButton.isEnabled()) {
			payButton.click();
			System.out.println("Payment button is clicked and Pop up is Appeared.....");
			return true;
		} else
			return false;
	}

	public boolean finalDetailsAndCaptcha() {
		WebElement email = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input#usrname")));
		email.click();
		email.sendKeys(configProp.getProperty("EMAIL"));
		WebElement password = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("input#psw")));
		password.click();
		password.sendKeys(configProp.getProperty("PASSWORD"));
		WebElement frame = driver.findElement(By.cssSelector("iframe[name^=a]"));
		driver.switchTo().frame(frame);
		WebElement captcha = driver.findElement(By.cssSelector("div.recaptcha-checkbox-border"));
		captcha.click();
		driver.switchTo().parentFrame();
		WebElement access = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button#loginBtn")));
		if (access.isEnabled()) {
			System.out.println("Captcha and Final Details are entered Successfully....");
			return true;
		} else
			return false;
	}

	public String paymentSuccessful() throws Exception {
		WebElement pay = driver.findElement(By.cssSelector("button#loginBtn"));
		pay.click();
		Thread.sleep(10000);
		WebElement messageRead = wait
				.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div#add_err")));
		String message = messageRead.getText();
		System.out.println(message);
		return message;
	}
}
