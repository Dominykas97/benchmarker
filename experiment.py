from bs4 import BeautifulSoup
import json
import matplotlib.pyplot as plt
import requests

host = 'https://prometheus-myproject.192.168.42.234.nip.io' # TODO: Query oc for this
metrics = [('Throughput', 'flink_taskmanager_job_task_operator_componentThroughput'),
           ('Heap Usage', 'flink_taskmanager_Status_JVM_Memory_Heap_Used'),
           ('CPU Load', 'flink_taskmanager_Status_JVM_CPU_Load')]
time_interval = '1m' # >= the amount of time between 'oc create' and all the work terminating
login_info = {'username': 'developer', 'password': '?????'}
session = requests.Session()

# 1. Try to retrieve JSON data
# 2. If success, go to ...
# 3. If there is a button saying 'Sign in with an OpenShift account'
# 4. Press on it <form method="GET" action="/oauth/start">
# 4.5. <form action="/login" method="POST"> (with hidden values!)
# 5. Enter 'developer' into 'inputUsername'
# 6. Enter the password into 'inputPassword'
# 7. Submit
# 8. That takes us to the data (?)

# Temp code for logging things
import requests
import logging
import http.client as http_client
http_client.HTTPConnection.debuglevel = 1
logging.basicConfig()
logging.getLogger().setLevel(logging.DEBUG)
requests_log = logging.getLogger("requests.packages.urllib3")
requests_log.setLevel(logging.DEBUG)
requests_log.propagate = True

for metric_name, metric_id in metrics[:1]:
    path = '/api/v1/query?query=' + metric_id + '[' + time_interval + ']'
    first_page = session.get(host + path, verify=False).text
    login_page = session.get(host + '/oauth/start', params={'rd': path}, verify=False)
    print('*****Cookies:')
    print(session.cookies.get_dict())
    soup = BeautifulSoup(login_page.text, features='html5lib')
    hidden_values = soup.find_all('input', type='hidden')
    hidden_dict = dict((tag.get('name'), tag.get('value')) for tag in hidden_values)
    print('**********Referer: ' + login_page.url)
    cookie = session.cookies.get_dict()['csrf']
    origin = login_page.url[:login_page.url.find('login')-1]
    ua = 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36'
    sign_in_page = session.post(host + '/login', data={**login_info, **hidden_dict}, verify=False, cookies={'csrf': cookie},
                                headers={'Referer': login_page.url, 'Origin': origin, 'Authority': 'Bearer jtTqXIty8lsbSiQQEzCPPMlKl0jJFOzSbC2jXBt5LMc', 'User-Agent': ua, 'X-CSRF-Token': 'xxx'})
    print(sign_in_page.request.headers)
    print(sign_in_page.history)


"""
with open('sample.json') as sample_file:
    data = json.loads(sample_file.read())

for series in data['data']['result']:
    x = []
    y = []
    for timestamp, value in series['values']:
        x.append(timestamp)
        y.append(float(value))
    plt.plot(x, y)
plt.xlabel('time')
plt.show()
"""
