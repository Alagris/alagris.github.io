from time import sleep
from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.remote.webelement import WebElement
from selenium.webdriver.firefox.options import Options
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.webdriver.common.by import By
from selenium.common.exceptions import TimeoutException, WebDriverException


class text_to_change(object):
    def __init__(self, locator, text):
        self.locator = locator
        self.text = text

    def __call__(self, driver):
        actual_text = EC._find_element(driver, self.locator).get_attribute("value")
        return actual_text != self.text


def test1(driver):
    # open our web page
    driver.get("http://localhost/compiler1")
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



def run_firefox():
    # This method runs all tests in Firefox
    options = Options()
    options.headless = False

    driver = webdriver.Firefox(options=options)
    test1(driver)
    driver.close()


def run_chrome():
    # This method runs all tests in Chrome
    options = Options()
    options.headless = False

    driver = webdriver.Chrome(options=options)
    test1(driver)
    driver.close()


run_firefox()
