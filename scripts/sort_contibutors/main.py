import requests
import os
from dotenv import load_dotenv

# This function parses the contribution list, sorting
# the users per its ranks


def print_file(s: str, flag: bool) -> None:

    # True for MD , false for HTML file
    if flag:
        f = open('contributors_file.md', 'w')
        f.write(s)
        return
    f = open('contributors_file.html', 'w')
    f.write(s)


def print_md(user_list: dict, label="") -> str:

    string = '{}\n'.format(label)
    for value in user_list:
        string += '- [{} @{}](https://www.github.com/{})\n'.format(value['name'],
                                                                   value['username'], value['username'])
    return string + '\n'


def print_html(leaders: dict, top_contributors: dict, contributors: dict, testers: dict, special_thanks: dict) -> str:

    string = '<html><head></head><body>\n'

    string += '<h1>Leaders</h1>\n'
    string += '<ul>\n'
    for value in leaders:
        string += '<li><a href=\'https://www.github.com/{}\'>{} @{}</a></li>\n'.format(
            value['username'], value['name'], value['username'])
    string += '</ul>\n'

    string += '\n<h1>Top contributors</h1>\n'
    string += '<ul>\n'
    for value in top_contributors:
        string += '<li><a href=\'https://www.github.com/{}\'>{} @{}</a></li>\n'.format(
            value['username'], value['name'], value['username'])
    string += '</ul>\n'

    string += '\n<h1>Contributors</h1>\n'
    string += '<ul>\n'
    for value in contributors:
        string += '<li><a href=\'https://www.github.com/{}\'>{} @{}</a></li>\n'.format(
            value['username'], value['name'], value['username'])
    string += '</ul>\n'

    string += '<h1>Testers</h1>\n'
    string += '<ul>\n'
    for value in testers:
        string += '<li><a href=\'https://www.github.com/{}\'>{} @{}</a></li>'.format(
            value['username'], value['name'], value['username'])
    string += '</ul>\n\n'

    string += '<h1>Special thanks</h1>\n'
    string += '<ul>\n'
    for value in special_thanks:
        string += '<li><a href=\'https://www.github.com/{}\'>{} @{}</a></li>\n'.format(
            value['username'], value['name'], value['username'])
    string += '</ul>\n'

    string += '</body><html>\n'
    return string


def parse_contributor_list(user_list: list, user_token: str) -> list:
    contributors = []

    for element in user_list:
        ranking = element['contributions']
        username = element['login']
        name = get_fullname(username, user_token)
        if name == None:
            name = username

        leaders_and_multijuicer = ['DerGut', 'bkimminich', 'MichaelEischer', 'rseedorff', 'jonasbg', 'scornelissen85', 'zadjadr', 'stuebingerb', 'sydseter', 'troygerber', 'skandix', 'saymolet',
                                   'adrianeriksen', 'pseudobeard', 'coffemakingtoaster', 'wurstbrot', 'blucas-accela', 'fwijnholds', 'stefan-schaermeli', 'nickmalcolm', 'orangecola', 'commjoen', 'bendehaan']

        # Filter the github bots
        if '[bot]' not in username and username not in leaders_and_multijuicer:
            contributors.append(
                {'username': username, 'name': name, 'ranking': ranking})

    return contributors

# Retrieves the list of fullnames of contributors of a repository in JSON format


def get_fullname(username: str, user_token: str) -> str:
    headers = {'X-GitHub-Api-Version': '2022-11-28',
               'Accept': 'application/vnd.github+json',
               'Authorization': 'Bearer ' + user_token}
    r = requests.get('https://api.github.com/users/' +
                     username, headers=headers, timeout=20)
    if r.status_code == 401:
        print("Invalid token")
        os._exit(-1)
    return r.json()['name']

# Retrieves the list of contributors of a repository in JSON format


def fetch_repository(project: str, user_token: str) -> list:
    headers = {'X-GitHub-Api-Version': '2022-11-28',
               'Accept': 'application/vnd.github+json',
               'Authorization': 'Bearer ' + user_token}
    r = requests.get('https://api.github.com/repos/OWASP/' +
                     project+'/contributors', headers=headers, timeout=20)
    if r.status_code == 401:
        print("Invalid token")
        os._exit(-1)
    return parse_contributor_list(r.json(), token)


def merge_users(l: list) -> list:
    username = dict()
    ranking = dict()

    for a in l:

        if a['username'] in ranking:
            ranking[a['username']] += a['ranking']
        else:
            ranking[a['username']] = a['ranking']
        username[a['username']] = {'username': a['username'],
                                   'name': a['name'], 'ranking': ranking[a['username']]}

    l = dict(sorted(username.items(),
             key=lambda x: x[1]['ranking'], reverse=True))

    special_contributors = []
    contributors = []

    for key, value in l.items():
        element = {'name': value['name'],
                   'username': key, 'ranking': value['ranking']}
        if element['ranking'] >= 100:
            special_contributors.append(element)
        else:
            contributors.append(element)
    return [special_contributors, contributors]


def get_contibutors_list(token: str) -> list:

    print("[+] Fetching the Wrong Secrets CTF party contributors list ... ")
    wrongsecrets_ctf_list = fetch_repository('wrongsecrets-ctf-party', token)
    print("[+] Fetching the Wrong Secrets Binaries contributors list ... ")
    wrongsecrets_binaries_list = fetch_repository(
        'wrongsecrets-binaries', token)
    print("[+] Fetching the Wrong Secrets contributors list ... ")
    wrongsecrets_list = fetch_repository('wrongsecrets', token)
    merged_list = wrongsecrets_binaries_list + \
        wrongsecrets_ctf_list + wrongsecrets_list
    print("[+] Sorting the list .. ")
    return merge_users(merged_list)


# =============================================================
# THE MAIN PROGRAM STARTS HERE
# =============================================================

# Loads the .env file
load_dotenv()
token = os.getenv('USER_TOKEN')

if token is not None:

    testers = [
        {'username': 'davevs', 'name': 'Dave van Stein'},
        {'username': 'drnow4u', 'name': 'Marcin Nowak'},
        {'username': 'mchangsp', 'name': 'Marc Chang Sing Pang'},
        {'username': 'djvinnie', 'name': 'Vineeth Jagadeesh'}
    ]
    leaders = [
        {'username': 'bendehaan', 'name': 'Ben de Haan'},
        {'username': 'commjoen', 'name': 'Jeroen Willemsen'}
    ]
    special_thanks = [
        {'username': 'madhuakula', 'name': 'Madhu Akula @madhuakula'},
        {'username': 'bkimminich', 'name': 'Björn Kimminich'},
        {'username': 'devsecops', 'name': 'Dan Gora'},
        {'username': 'saragluna', 'name': 'Xiaolu Dai'},
        {'username': 'jonathanGiles', 'name': 'Jonathan Giles'},
    ]

    l = get_contibutors_list(token)
    special_list = l[0]
    normal_list = l[1]
    l = merge_users(special_list + normal_list)

    print('[+] Print to HTML file')
    html_payload = print_html(leaders, l[0], l[1], testers, special_thanks)
    print_file(html_payload, False)

    print('[+] Print to MD file')
    md_payload = print_md(leaders, 'Leaders')
    md_payload += print_md(l[0], 'Top contributors')
    md_payload += print_md(l[1], 'Contributors')
    md_payload += print_md(testers, 'Testers')
    md_payload += print_md(special_thanks, 'Special thanks')
    print_file(md_payload, True)

    print('[+] Done')
else:
    print(
        'The variable USER_TOKEN  does not exists. You must setup the variable'
        'in the following way:')
    print('echo USER_TOKEN=github_pat_dfs1N_f>.env')
    print('note that the USER_TOKEN requires "repository access" only')
