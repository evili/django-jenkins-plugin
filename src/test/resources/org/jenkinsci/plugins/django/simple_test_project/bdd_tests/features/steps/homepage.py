from __future__ import print_function
from behave import *

@then(u'I will see an item list')
def impl(context):
    item_list = context.browser.find_elements_by_xpath('//ul/li')
    assert len(item_list)

