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

use_Firefox = True

class text_to_change(object):
    def __init__(self, locator, text):
        self.locator = locator
        self.text = text

    def __call__(self, driver):
        actual_text = EC._find_element(driver, self.locator).get_attribute("value")
        return actual_text != self.text

def assertEquals(var1, var2):
    if var1 == var2:
        return True
    else:
        return False


class PythonOrgSearch(unittest.TestCase):

    def setUp(self):
        if use_Firefox == True:
            self.driver = webdriver.Firefox()
        else:
            self.driver = webdriver.Chrome()


  

    def test1(self):
        # open our web page
        driver = self.driver
        driver.get("http://localhost/compiler")
        # Get website's title
        title = driver.title
        # test if the title is correct. It should contain "Solomonoff" string in the title
        assert "Solomonoff" in driver.title

        

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

     
        # Now we type some code into the Ace editor
        webdriver.ActionChains(driver).move_to_element(editor).click().send_keys("x = 'tre':'00'").perform()

        # Now we focus on the "Compile" button. We query the button with CSS selector
        compile = driver.find_element_by_css_selector('#btnn')
        # We press "Compile" button
        compile.click()

        # Now the request has been sent to server and it will take some time before its processed.
        # In the meantime, while we wait for the response from server, we focus on the REPL output console.
        # Now we query console output with CSS selector
        replOutput = driver.find_element_by_id('outputField')
        # We get the current REPL output. Most likely its just an empty string, unless there was a compilation error
        replOutputText1 = replOutput.get_attribute("value")

        # We get the REPL input field
        replInput = driver.find_element_by_id('inputField')
        # We type some code in REPL
        replInput.send_keys(':eval x \'tre\'')
        # We hit Return key
        replInput.send_keys(Keys.RETURN)

        # We wait until the REPL output updates. The server needs to do some work and send it back.
        WebDriverWait(driver, 10).until(text_to_change((By.ID, "outputField"), replOutputText1))
        # The text_to_change(replOutput, replOutputText1) is a function that tells selenium to wait until the text of
        # replOutput differs from its previous value replOutputText1

        # Now we query the new text
        replOutputText2 = replOutput.get_attribute("value")
        # print(replOutputText2) 
        assert replOutputText2.strip() == "> :eval x 'tre'\n'00'"

        def_func = driver.find_element_by_id('tips')
        def_func_value = def_func.get_attribute("value")
        #print(def_func_value)

        tips = driver.find_element_by_css_selector('#tips')
        tips_text = tips.text
        #print(tips_text)
        assert "x" in tips_text<!doctype html>
<html>
  <head>
    <title>This is the title of the webpage!</title>
  </head>
  <body>
    <p>This is an example paragraph. Anything in the <strong>body</strong> tag will appear on the page, just like this <strong>p</strong> tag and its contents.</p>
  </body>
</html>


        clear = driver.find_element_by_css_selector('body > main > div > div > div.first > span > button')
        clear.click()
        replOutput_test2 = driver.find_element_by_id('outputField')
        replOutputText1_test2 = replOutput.get_attribute("value")
      # print(replOutputText1_test2)
        assert replOutputText1_test2 == ""


        code_from_doc = driver.find_element_by_css_selector('#tutorial > pre:nth-child(18)')
        code_from_doc.click()

        tips_1 = driver.find_element_by_css_selector('#tips')
        tips_text_1 = tips_1.text
        if "x" in tips_text_1:
            replInput_1 = driver.find_element_by_id('inputField')
            replInput_1.send_keys(':unset x')
            replInput_1.send_keys(Keys.RETURN)

        compile_1 = driver.find_element_by_css_selector('#btnn')
        compile_1.click()
        replInput_2 = driver.find_element_by_id('inputField')
        replInput_2.send_keys(':eval x \'tre\'')
        replInput_2.send_keys(Keys.RETURN)
        replOutput_test_3 = driver.find_element_by_id('outputField')
        replOutputText1_test_3 = replOutput_test_3.get_attribute("value")
        #print(replOutputText1_test_3)
        assert "No match!" in replOutputText1_test_3

        URL = driver.current_url
        assertEquals(URL, "http://localhost/compiler" )

        Start_URL = driver.find_element_by_css_selector('body > nav > a')
        Start_URL_text = Start_URL.get_attribute('href')
        assertEquals(Start_URL, "http://localhost/")

        Compiler_URL = driver.find_element_by_css_selector('#navbarsExampleDefault > ul > li.nav-item.active > a')
        Compiler_URL_text = Compiler_URL.get_attribute('href')
        assertEquals(Compiler_URL, "http://localhost/compiler")

        Documentation_URL = driver.find_element_by_css_selector('#navbarsExampleDefault > ul > li:nth-child(2) > a')
        Documentation_URL_text = Documentation_URL.get_attribute('href')
        assertEquals(Documentation_URL, "http://localhost/DocPage")

        Contact_URL = driver.find_element_by_id('bigl1')
        Contact_URL_text = Contact_URL.get_attribute('href')
        assertEquals(Contact_URL, "http://localhost/Contact")

        GitHub_URL = driver.find_element_by_id('bigl2')
        GitHub_URL_text =  GitHub_URL.get_attribute('href')
        assertEquals( GitHub_URL, "https://github.com/aleksander-mendoza/SolomonoffLib")

        Download_URL = driver.find_element_by_id('bigl3')
        Download_URL_text =  Download_URL.get_attribute('href')
        assertEquals(Download_URL, "http://localhost/Download")










    # def run_firefox():
    #     # This method runs all tests in Firefox
    #     options = Options()
    #     options.headless = False

    #     driver = webdriver.Firefox(options=options)
    #     test1(driver)
    #     driver.close()


    # def run_chrome():
    #     # This method runs all tests in Chrome
    #     options = Options()
    #     options.headless = False

    #     driver = webdriver.Chrome(options=options)
    #     test1(driver)
    #     driver.close()


    # run_firefox()
    def tearDown(self):
            self.driver.close()

if __name__ == "__main__":
    unittest.main()
   