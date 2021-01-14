from time import sleep
import unittest
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.remote.webelement import WebElement
from selenium.webdriver.firefox.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.common.exceptions import TimeoutException, WebDriverException
import os


class text_to_change(object):
    def __init__(self, locator, text):
        self.locator = locator
        self.text = text

    def __call__(self, driver):
        actual_text = EC._find_element(driver, self.locator).get_attribute("value")
        return actual_text != self.text


class PythonOrgSearch(unittest.TestCase):

    def setUp(self):
        browser = os.environ.get('BROWSER')
        if browser == "Chrome":
            self.driver = webdriver.Chrome()
        elif browser == "Edge":
            self.driver = webdriver.Edge()
        elif browser == "Safari":
            self.driver = webdriver.Safari()
        else:
            self.driver = webdriver.Firefox()

    def test_compilation(self):
        driver = self.driver
        try:
            # open our web page
            driver.get("http://localhost/compiler")
            # Get website's title
            title = driver.title
            # test if the title is correct. It should contain "Solomonoff" string in the title
            self.assertIn("Solomonoff", driver.title)

            # at the beginning the tips open
            # but they usually show up after a tiny delay. Here we wait for
            # the tips to load and show up. In particular, we wait
            # until "Skip" button appears
            skip: WebElement = WebDriverWait(driver, 3).until(
                EC.visibility_of_element_located((By.CSS_SELECTOR, '.enjoyhint_skip_btn')))
            # We press the "Skip" button
            webdriver.ActionChains(driver).move_to_element(skip).click().perform()
            # The tips should disappear
            # Now we focus on the Ace editor. We query it with CSS selector
            editor = driver.find_element_by_css_selector('#editor > div.ace_scroller > div')

            # Now the request has been sent to server and it will take some time before its processed.
            # In the meantime, while we wait for the response from server, we focus on the REPL output console.
            # Now we query console output with CSS selector
            replOutput = driver.find_element_by_id('outputField')
            # We get the current REPL output. Most likely its just an empty string, unless there was a compilation error
            replOutputText1 = replOutput.get_attribute("value")

            # Now we type some code into the Ace editor
            webdriver.ActionChains(driver).move_to_element(editor).click().send_keys("x = 'tre':'00'").perform()

            # Now we focus on the "Compile" button. We query the button with CSS selector
            compile = driver.find_element_by_css_selector('#btnn')
            # We press "Compile" button
            compile.click()

            # We wait until the REPL output updates. The server needs to do some work and send it back.
            WebDriverWait(driver, 10).until(text_to_change((By.ID, "outputField"), replOutputText1))

            # Now we query the new text
            replOutputText2 = replOutput.get_attribute("value")
            # print(replOutputText2)
            self.assertEquals(replOutputText2.strip(), "> :load")

            # We get the REPL input field
            replInput = driver.find_element_by_id('inputField')

            webdriver.ActionChains(driver) \
                .move_to_element(replInput) \
                .click() \
                .send_keys(":eval x 'tre'") \
                .send_keys(Keys.RETURN).perform()

            WebDriverWait(driver, 10).until(text_to_change((By.ID, "outputField"), replOutputText2))

            replOutputText3 = replOutput.get_attribute("value")
            self.assertEquals(replOutputText3.strip(), "> :load\n> :eval x 'tre'\n'00'")

            webdriver.ActionChains(driver) \
                .move_to_element(replInput) \
                .click() \
                .send_keys("y = !!x !!x") \
                .send_keys(Keys.RETURN).perform()

            WebDriverWait(driver, 10).until(text_to_change((By.ID, "outputField"), replOutputText3))

            replOutputText4 = replOutput.get_attribute("value")
            self.assertEquals(replOutputText4.strip(), "> :load\n> :eval x 'tre'\n'00'\n> y = !!x !!x")

            webdriver.ActionChains(driver) \
                .move_to_element(replInput) \
                .click() \
                .send_keys("y = !!x !!x") \
                .send_keys(Keys.RETURN).perform()

            WebDriverWait(driver, 10).until(text_to_change((By.ID, "outputField"), replOutputText3))

        except Exception as e:
            driver.save_screenshot('test_compilation.png')
            raise e

    def test_tips(self):
        driver = self.driver
        try:
            driver.get("http://localhost/compiler")

            # First tip

            next: WebElement = WebDriverWait(driver, 3).until(
                EC.visibility_of_element_located((By.CSS_SELECTOR, '.enjoyhint_next_btn')))
            # We press the "Skip" button
            webdriver.ActionChains(driver).move_to_element(next).click().perform()

            # Second tip

            next: WebElement = WebDriverWait(driver, 3).until(
                EC.visibility_of_element_located((By.CSS_SELECTOR, '.enjoyhint_next_btn')))
            # We press the "Skip" button
            webdriver.ActionChains(driver).move_to_element(next).click().perform()

            # Third tip

            next: WebElement = WebDriverWait(driver, 3).until(
                EC.visibility_of_element_located((By.CSS_SELECTOR, '.enjoyhint_next_btn')))
            # We press the "Skip" button
            webdriver.ActionChains(driver).move_to_element(next).click().perform()

        except Exception as e:
            driver.save_screenshot('test_tips.png')
            raise e

    def test_nav_bar(self):
        driver = self.driver
        try:
            driver.get("http://localhost")
            nav_bar: WebElement = WebDriverWait(driver, 3).until(
                EC.visibility_of_element_located((By.CSS_SELECTOR, 'nav.navbar')))
            nav_links = nav_bar.find_elements_by_css_selector('a')
            self.assertEquals(nav_links[0].get_attribute("href"), "http://localhost/")
            self.assertEquals(nav_links[1].get_attribute("href"), "http://localhost/compiler")
            self.assertEquals(nav_links[2].get_attribute("href"), "http://localhost/DocPage")
            self.assertEquals(nav_links[3].get_attribute("href"), "")
            self.assertEquals(nav_links[4].get_attribute("href"), "http://localhost/Contact")
            self.assertEquals(nav_links[5].get_attribute("href"), "https://github.com/aleksander-mendoza/SolomonoffLib")
            self.assertEquals(nav_links[6].get_attribute("href"), "http://localhost/Download")

            driver.get("http://localhost/compiler")
            nav_bar: WebElement = WebDriverWait(driver, 3).until(
                EC.visibility_of_element_located((By.CSS_SELECTOR, 'nav.navbar')))
            nav_links = nav_bar.find_elements_by_css_selector('a')
            self.assertEquals(nav_links[0].get_attribute("href"), "http://localhost/")
            self.assertEquals(nav_links[1].get_attribute("href"), "http://localhost/compiler")
            self.assertEquals(nav_links[2].get_attribute("href"), "http://localhost/DocPage")
            self.assertEquals(nav_links[3].get_attribute("href"), "")
            self.assertEquals(nav_links[4].get_attribute("href"), "http://localhost/Contact")
            self.assertEquals(nav_links[5].get_attribute("href"), "https://github.com/aleksander-mendoza/SolomonoffLib")
            self.assertEquals(nav_links[6].get_attribute("href"), "http://localhost/Download")

            driver.get("http://localhost/DocPage")
            nav_bar: WebElement = WebDriverWait(driver, 3).until(
                EC.visibility_of_element_located((By.CSS_SELECTOR, 'nav.navbar')))
            nav_links = nav_bar.find_elements_by_css_selector('a')
            self.assertEquals(nav_links[0].get_attribute("href"), "http://localhost/")
            self.assertEquals(nav_links[1].get_attribute("href"), "http://localhost/compiler")
            self.assertEquals(nav_links[2].get_attribute("href"), "http://localhost/DocPage")
            self.assertEquals(nav_links[3].get_attribute("href"), "")
            self.assertEquals(nav_links[4].get_attribute("href"), "http://localhost/Contact")
            self.assertEquals(nav_links[5].get_attribute("href"), "https://github.com/aleksander-mendoza/SolomonoffLib")
            self.assertEquals(nav_links[6].get_attribute("href"), "http://localhost/Download")

            driver.get("http://localhost/Contact")
            nav_bar: WebElement = WebDriverWait(driver, 3).until(
                EC.visibility_of_element_located((By.CSS_SELECTOR, 'nav.navbar')))
            nav_links = nav_bar.find_elements_by_css_selector('a')
            self.assertEquals(nav_links[0].get_attribute("href"), "http://localhost/")
            self.assertEquals(nav_links[1].get_attribute("href"), "http://localhost/compiler")
            self.assertEquals(nav_links[2].get_attribute("href"), "http://localhost/DocPage")
            self.assertEquals(nav_links[3].get_attribute("href"), "")
            self.assertEquals(nav_links[4].get_attribute("href"), "http://localhost/Contact")
            self.assertEquals(nav_links[5].get_attribute("href"), "https://github.com/aleksander-mendoza/SolomonoffLib")
            self.assertEquals(nav_links[6].get_attribute("href"), "http://localhost/Download")

            driver.get("http://localhost/Download")
            nav_bar: WebElement = WebDriverWait(driver, 3).until(
                EC.visibility_of_element_located((By.CSS_SELECTOR, 'nav.navbar')))
            nav_links = nav_bar.find_elements_by_css_selector('a')
            self.assertEquals(nav_links[0].get_attribute("href"), "http://localhost/")
            self.assertEquals(nav_links[1].get_attribute("href"), "http://localhost/compiler")
            self.assertEquals(nav_links[2].get_attribute("href"), "http://localhost/DocPage")
            self.assertEquals(nav_links[3].get_attribute("href"), "")
            self.assertEquals(nav_links[4].get_attribute("href"), "http://localhost/Contact")
            self.assertEquals(nav_links[5].get_attribute("href"), "https://github.com/aleksander-mendoza/SolomonoffLib")
            self.assertEquals(nav_links[6].get_attribute("href"), "http://localhost/Download")
        except Exception as e:
            driver.save_screenshot('test_tips.png')
            raise e

    def _test_variable_buttons(self):
        driver = self.driver
        try:
            # open our web page
            driver.get("http://localhost/compiler")
            # Get website's title
            title = driver.title
            # test if the title is correct. It should contain "Solomonoff" string in the title
            self.assertIn("Solomonoff", driver.title)

            # at the beginning the tips open
            # but they usually show up after a tiny delay. Here we wait for
            # the tips to load and show up. In particular, we wait
            # until "Skip" button appears
            skip: WebElement = WebDriverWait(driver, 3).until(
                EC.visibility_of_element_located((By.CSS_SELECTOR, '.enjoyhint_skip_btn')))
            # We press the "Skip" button
            webdriver.ActionChains(driver).move_to_element(skip).click().perform()
            # The tips should disappear
            # Now we focus on the Ace editor. We query it with CSS selector
            editor = driver.find_element_by_css_selector('#editor > div.ace_scroller > div')

            # Now the request has been sent to server and it will take some time before its processed.
            # In the meantime, while we wait for the response from server, we focus on the REPL output console.
            # Now we query console output with CSS selector
            replOutput = driver.find_element_by_id('outputField')
            # We get the current REPL output. Most likely its just an empty string, unless there was a compilation error
            replOutputText1 = replOutput.get_attribute("value")

            # Now we type some code into the Ace editor
            webdriver.ActionChains(driver).move_to_element(editor).click().send_keys("x = 'tre':'00'").perform()

            # Now we focus on the "Compile" button. We query the button with CSS selector
            compile = driver.find_element_by_css_selector('#btnn')
            # We press "Compile" button
            compile.click()

            # We wait until the REPL output updates. The server needs to do some work and send it back.
            WebDriverWait(driver, 10).until(text_to_change((By.ID, "outputField"), replOutputText1))

            # Now we query the new text
            replOutputText2 = replOutput.get_attribute("value")
            # print(replOutputText2)
            self.assertEquals(replOutputText2.strip(), "> :load")

            # We get the REPL input field
            replInput = driver.find_element_by_id('inputField')

            webdriver.ActionChains(driver) \
                .move_to_element(replInput) \
                .click() \
                .send_keys(":eval x 'tre'") \
                .send_keys(Keys.RETURN).perform()

            WebDriverWait(driver, 10).until(text_to_change((By.ID, "outputField"), replOutputText2))

            replOutputText3 = replOutput.get_attribute("value")
            self.assertEquals(replOutputText3.strip(), "> :load\n> :eval x 'tre'\n'00'")

        except Exception as e:
            driver.save_screenshot('test_compilation.png')
            raise e

    def tearDown(self):
        self.driver.close()


if __name__ == "__main__":
    unittest.main()
