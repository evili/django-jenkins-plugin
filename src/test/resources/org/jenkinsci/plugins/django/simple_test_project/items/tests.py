from django.core.urlresolvers import reverse
from django.db.utils import IntegrityError
from django.test import Client,TestCase

from .models import Item

_ITEM_1 = 'Item 1'
_ITEM_2 = 'Item 2'

class ItemTestCase(TestCase):

    def test_new_item(self):
        i1 = Item.objects.get_or_create(name=_ITEM_1)[0]
        i1.save()
        self.assertEquals(i1.name, _ITEM_1,
                          msg='First item should be named "'+_ITEM_1+'"')

    def test_item_name_uniqe(self):
        i1 = Item.objects.get_or_create(name=_ITEM_1)[0]
        i1.save()
        i2 = Item.objects.get_or_create(name=_ITEM_2)[0]
        i2.save()
        self.assertNotEqual(i1.name, i2.name,
                            msg='Disctint items should have '
                            'distinctnames: "%s","%s"' %
                            (i1.name, i2.name))

        i3 = Item(name=_ITEM_1)

        try:
            i3.save()
            self.fail(msg="Item name should't have been repeated.")
        except IntegrityError as e:
            pass


class ItemViewTestCase(TestCase):

    def setUp(self):
        self.client = Client()

    def test_index_view(self):
        response = self.client.get(reverse('items_index'))
        self.assertEquals(response.status_code, 200)

        
        
