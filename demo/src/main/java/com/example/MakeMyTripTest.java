package com.example;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;

public class MakeMyTripTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        String excelFilePath = "C:/Users/I528632/Desktop/Testing Assignment/demo/MakeMyTrip.xlsx";
        FileInputStream inputStream = new FileInputStream(excelFilePath);
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        int count = 0;
        for (Row row : sheet) {
            if (count == 0) {
                count++;
                continue;
            }

            String testCaseID = getCellValue(row.getCell(0));
            String testDesc = getCellValue(row.getCell(1));
            String inputField1 = getCellValue(row.getCell(2));
            String inputField2 = getCellValue(row.getCell(3));
            String expectedOutcome = getCellValue(row.getCell(4));

            driver.get("https://www.redbus.in/");
            Thread.sleep(5000);
            WebElement input1 = driver.findElement(By.xpath("//label[text()='From']//preceding-sibling::input"));
            WebElement input2 = driver.findElement(By.xpath("//label[text()='To']//preceding-sibling::input"));
            driver.findElement(By.xpath("//*[@id=\"onwardCal\"]/div/div")).click();

            String desired_month = "Nov 2024";
            String desired_date = "25";

            while (true) {

            String actual_month = driver.findElement(By.xpath("//*[@id=\"onwardCal\"]/div/div[2]/div/div/div[1]/div[2]")).getText();
            System.out.println(actual_month);
            if (actual_month.contains(desired_month)) {
                System.out.println(actual_month);
                break;

            } else {
                driver.findElement(By.xpath("//*[@id=\"onwardCal\"]/div/div[2]/div/div/div[1]/div[3]")).click(); // next month
            }
        }

        int column_size = 7; // as per the days (Mon - Sun)
        int flag = 0;
        int row_size = driver.findElements(By.xpath("//*[@id=\"onwardCal\"]/div/div[2]/div/div/div[3]/div")).size(); // 8
        for(int i = 2; i<=row_size; i++){ // row

            for(int j = 1; j<= column_size; j++){ // column
                String actual_date = driver.findElement(By.xpath("//*[@id=\"onwardCal\"]/div/div[2]/div/div/div[3]/div["+i+"]/span/div["+j+"]")).getText();
                if(actual_date.equals(desired_date)){
                    driver.findElement(By.xpath("//*[@id=\"onwardCal\"]/div/div[2]/div/div/div[3]/div["+i+"]/span/div["+j+"]")).click();
                    flag = 1; // set flag = 1
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break; // breaking out of inner loop
                }
                else{
                    continue;
                }
            }
            if(flag == 1){ // selection of the date is done
                break;
            }
        }

            input1.clear();
            input2.clear();

            if (!inputField1.isEmpty()) {
                input1.sendKeys(inputField1);
            }

            if (!inputField2.isEmpty()) {
                input2.sendKeys(inputField2);
            }

            WebElement submitButton = driver.findElement(By.cssSelector("button[type='submit']"));
            submitButton.click();
            Thread.sleep(4000);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            String actualOutcome = driver.getCurrentUrl();

            String status = actualOutcome.equals(expectedOutcome) ? "Pass" : "Fail";

            row.createCell(5).setCellValue(actualOutcome);
            row.createCell(6).setCellValue(status);
        }

        FileOutputStream outputStream = new FileOutputStream("C:/Users/I528632/Desktop/Testing Assignment/demo/MakeMyTrip.xlsx");
        workbook.write(outputStream);
        workbook.close();
        outputStream.close();
        inputStream.close();

        driver.quit();
    }

    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
