# django-jenkins-plugin
![Build status](https://github.com/evili/django-jenkins-plugin/actions/workflows/maven.yml/badge.svg)

Django Jenkins Plugin tests [Django](https://www.djangoproject.com/)
projects with the help of the
[django-jenkins](https://pypi.python.org/pypi/django-jenkins/)
Python package. This plugin is able to run all the tests provided by
django-jenkins with a pristine copy of any Django project. There is no
need to include 'django\_jenkins' in INSTALLED\_APPS, no dependencies to
add into requirements file...

## Quick setup

To build your project with the help of this plugin

 * Create a new Freestyle project
 * Setup your SCM, polling, etc.
 * Add "Django Jenkins Builder" as a build step (maybe the *only* build
   step).
 * Optionally add some post-build steps (like "Publish JUnit test
 result report").

That's all.
