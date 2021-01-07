from selenium import webdriver
from selenium.webdriver.common.keys import Keys
from selenium.webdriver.firefox.options import Options

count = 0

def check(value):
    global count

    if value == True:
        print("Step "+ str(count) + " pass")
    else:
        print("Step "+ str(count) + " not pass")
    count =count +1
        


options = Options()
options.headless = True
driver = webdriver.Firefox(options=options)

driver.get("http://localhost/compiler1")
title = driver.title
check("Solomonoff" in driver.title)

elem = driver.find_element_by_name("inp")
elem.clear()
elem.send_keys(":eval x \'hellllllo\'")
elem.send_keys(Keys.RETURN)
check("Transducer \'y\'" not in driver.page_source)

driver.close()

