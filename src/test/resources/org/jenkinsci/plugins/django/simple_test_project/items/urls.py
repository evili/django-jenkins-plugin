
from django.conf.urls import patterns, url
from .views import ItemList

urlpatterns = patterns('',
                       url(r'^$', ItemList.as_view(),
                           name = 'items_index'),   
)
