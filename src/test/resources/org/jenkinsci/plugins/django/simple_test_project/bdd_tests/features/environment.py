from behave import when
from django.core.management import execute_from_command_line
from selenium import webdriver

def before_all(context):
    context.browser = webdriver.Firefox()
    context.browser.implicitly_wait(1)
    context.server_url = 'http://localhost:8081'
    execute_from_command_line(['manage.py', 'loaddata',
                               'test_initial_data.json'])

def after_all(context):
    context.browser.quit()

@when(u'I visit the "{link}" page')
def get_url(context, link=''):
    url = u'/'.join([context.server_url, link.strip('/')])
    context.browser.get(url)

# def before_feature(context, feature):
#     pass

# def after_feature(context, feature):
#     pass
