from django.views.generic.list import ListView

from .models import Item

class ItemList(ListView):
    model = Item
