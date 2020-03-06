from django.db import models


class Item(models.Model):

    name = models.CharField(max_length=63, unique=True)

    def __string__(self):
        return self.name

    def __unicode__(self):
        return self.__string__()

    class Meta:
        ordering = ['name']
