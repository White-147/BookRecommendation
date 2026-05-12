import math
import threading
import time
import pymysql
from selenium import webdriver
from selenium.webdriver.chrome.service import Service
from selenium.webdriver.common.by import By


class SQL:
    def __init__(self):
        self.db = pymysql.connect(host='192.168.10.12',
                                  user='root',
                                  password='root',
                                  database='library')
        self.cursor = self.db.cursor()

    def get(self):
        sql = "SELECT M_TITLE FROM book where img is null;"
        self.cursor.execute(sql)

        results = self.cursor.fetchall()
        self.cursor.close()
        self.db.close()
        return results

    def save(self, img, bookName):
        try:
            sql = "update book set img  = %s where M_TITLE = %s" % ("'" + img + "'", "'" + bookName + "'")
            print(sql)
            self.cursor.execute(sql)

            self.db.commit()
            self.cursor.close()
            self.db.close()
        except Exception as e:
            pass


def get_src(result_list):
    for result in result_list:

        chrome_options = webdriver.ChromeOptions()
        # 使用无界面浏览器模式
        chrome_options.add_argument('--headless')
        # 打开窗口
        chrome_options.add_argument('window-size=1920x1080')
        chrome_options.add_argument('--start-maximized')
        # 不加载图片
        prefs = {"profile.managed_default_content_settings.images": 2}
        chrome_options.add_experimental_option("prefs", prefs)
        wd = webdriver.Chrome(options=chrome_options, service=Service(r'./tool/chromedriver.exe'))

        wd.implicitly_wait(30)
        wd.get("https://www.dangdang.com/")
        wd.find_element(By.ID, 'key_S').send_keys(result[0])

        wd.implicitly_wait(10)
        search_element = wd.find_element(By.ID, "form_search_new")
        time.sleep(2)
        search_element.find_element(By.CLASS_NAME, "button").click()
        try:
            src = wd.find_element(By.NAME, "itemlist-picture").find_element(By.XPATH, "./img").get_attribute("src")
            SQL().save(src, result[0])
        except Exception as e:
            SQL().save("not found", result[0])
            continue
        wd.close()
        wd.quit()


class MyThreading(threading.Thread):
    def __init__(self, func, args=()):
        threading.Thread.__init__(self)
        self.func = func
        self.args = args

    def run(self):
        self.result = self.func(*self.args)

    def get_result(self):
        try:
            return self.result
        except Exception as err:
            return err


def main():
    results = SQL().get()
    result_list = []
    split = 200
    page = math.ceil(len(results) / split)
    for k in range(0, page):
        result = results[k * split:split + k * split]
        result_list.append(result)
    thread_num = 10
    for i in range(0, len(result_list), thread_num):
        thread_list = []
        for index in range(0, thread_num):
            if i + index < page:
                thread_temp = MyThreading(func=get_src, args=([result_list[i + index]]))
                thread_list.append(thread_temp)

        for thread in thread_list:
            thread.start()

        for thread in thread_list:
            thread.join()


if __name__ == '__main__':
    main()
