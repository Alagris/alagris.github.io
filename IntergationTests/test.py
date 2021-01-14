import unittest
from time import sleep

from selenium import webdriver
from selenium.common.exceptions import TimeoutException, WebDriverException
from selenium.webdriver.common.by import By
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.firefox.options import Options
from selenium.webdriver.remote.webelement import WebElement
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait

use_Firefox = True


class text_to_change(object):
    def __init__(self, locator, text):
        self.locator = locator
        self.text = text

    def __call__(self, driver):
        actual_text = EC._find_element(
            driver, self.locator).get_attribute("value")
        return actual_text != self.text



class Test(unittest.TestCase):

    def setUp(self):
        if use_Firefox == True:
            self.driver = webdriver.Firefox()
        else:
            self.driver = webdriver.Chrome()


    def test_1(self):
        driver = self.driver
        driver.get("http://localhost/compiler")
        title = driver.title
        self.assertIn("Solomonoff", driver.title)
        

        skip: WebElement = WebDriverWait(driver, 3).until(
            EC.visibility_of_element_located((By.CSS_SELECTOR, '.enjoyhint_skip_btn')))
        webdriver.ActionChains(driver).move_to_element(skip).click().perform()
        editor = driver.find_element_by_css_selector('#editor > div.ace_scroller > div')  
        webdriver.ActionChains(driver).move_to_element(editor).click().send_keys("x = 'tre':'00'").perform()
        compile = driver.find_element_by_css_selector('#btnn')
        compile.click()
        replOutput = driver.find_element_by_id('outputField')
        replOutputText1 = replOutput.get_attribute("value")
        replInput = driver.find_element_by_css_selector('#inputField > textarea')
        replInput.send_keys(':eval x \'tre\'')
        replInput.send_keys(Keys.RETURN)
        WebDriverWait(driver, 10).until(text_to_change((By.ID, "outputField"), replOutputText1))
        replOutputText2 = replOutput.get_attribute("value")
        # print(replOutputText2.strip())
        self.assertEqual(replOutputText2.strip(), "> :load\n> :eval x 'tre'\n'00'")
    

    def test_2(self):
        driver = self.driver
        driver.get("http://localhost/compiler")
        skip: WebElement = WebDriverWait(driver, 3).until(
            EC.visibility_of_element_located((By.CSS_SELECTOR, '.enjoyhint_skip_btn')))
        webdriver.ActionChains(driver).move_to_element(skip).click().perform()
        editor = driver.find_element_by_css_selector('#editor > div.ace_scroller > div')  
        webdriver.ActionChains(driver).move_to_element(editor).click().send_keys("x = 'tre':'00'").perform()
        compile = driver.find_element_by_css_selector('#btnn')
        compile.click()
        tips = driver.find_element_by_id('automataHtmlList').text
        self.assertIn("x", tips)
        

    def test_3(self):
        driver = self.driver
        driver.get("http://localhost/compiler")
        skip: WebElement = WebDriverWait(driver, 3).until(
            EC.visibility_of_element_located((By.CSS_SELECTOR, '.enjoyhint_skip_btn')))
        webdriver.ActionChains(driver).move_to_element(skip).click().perform()
        clear = driver.find_element_by_css_selector('body > main > div > div > div.first > span > button')
        clear.click()
        replOutput = driver.find_element_by_id('outputField').get_attribute("value")
        self.assertEqual(replOutput, "> :clear\n")
       

    

    def tearDown(self):
        self.driver.close()

if __name__ == "__main__":
    unittest.main()