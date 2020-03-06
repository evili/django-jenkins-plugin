from django_test_deploy.settings import *

INSTALLED_APPS += ('bdd_tests',)
TEST_RUNNER = 'django_behave.runner.DjangoBehaveTestSuiteRunner'
#'django_behave.runner.DjangoBehaveTestSuiteRunner'
#'django_behave.runner.DjangoBehaveOnlyTestSuiteRunner'
