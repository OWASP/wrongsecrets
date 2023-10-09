import requests
import json
import os
from dotenv import load_dotenv

# It's just for debugging purpose


def print_list(user_list: dict, label="") -> None:

    print(label)
    for value in user_list:
        print(value['name'], " ", value['username'], ' ', value['ranking'])
    print("=============================================")

# This function parses the contribution list, sorting
# the users per its ranks


def parse_contributor_list(user_list: list, user_token: str) -> list:
    contributors = []

    for element in user_list:
        ranking = element['contributions']
        username = element['login']
        name = get_fullname(username, user_token)

        # Filter the github bots
        if '[bot]' not in username:
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
    print("[+] Merging the lists .. ")
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

    l = get_contibutors_list(token)
    special = l[0]
    normal = l[1]
    print_list(special, 'Special thanks')
    print_list(normal, 'Contributors')

else:
    print(
        'The variable USER_TOKEN  does not exists. You must setup the variable'
        'in the following way:')
    print('echo USER_TOKEN=github_pat_dfs1N_f>.env')
    print('note that the USER_TOKEN requires "repository access" only')
