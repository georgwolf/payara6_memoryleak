package demo.memleak;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

class FileUploadControllerTest{
    private WebDriver driver;

    @BeforeEach
    public void setUp(){
        this.driver = new FirefoxDriver();
    }

    @AfterEach
    public void tearDown(){
        this.driver.quit();
    }

    @Test
    public void submitForm(){
        for(int i = 0; i < 1000; i++){
            this.driver.get("http://localhost:8080/memleak/index.xhtml");
            this.driver.findElement(By.id("btn")).click();
            final long heapMb = Long.parseLong(this.driver.findElement(By.id("heapsize")).getText()) / 1024 / 1024;
            System.out.printf("%05d Heap: %d MB\n", i, heapMb);
        }
    }
}