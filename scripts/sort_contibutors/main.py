import requests
import json

# It's just for debugging purpose
def print_list(user_list : list):
	for element in user_list:
		print(element['username'],element['ranking'])

# This function parses the contribution list, sorting
# the users per its ranks
def parse_contributor_list(user_list : list) -> list:
	ret = []
	top_contributors = []
	contributors = []
	
	for element in user_list:
		ranking = element['contributions']
		username = element['login']
		
		# Filter the github bots
		if '[bot]' not in username:
			if ranking >= 100:
					top_contributors.append({'username':username,'ranking':ranking})
			else:
				contributors.append({'username':username,'ranking':ranking})
			
	
	ret.append(top_contributors)
	ret.append(contributors)
	return ret

# Retrieves the list of contributors of a repository in JSON format 
def get_contibutor_list(project : str,user_token: str) -> list:
	headers = {'X-GitHub-Api-Version':'2022-11-28','Accept':'application/vnd.github+json','Authorization':'Bearer ' + user_token}
	r = requests.get('https://api.github.com/repos/OWASP/'+project+'/contributors',headers=headers)
	return r.json()
	
token = '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'
project = 'wrongsecrets'

contributors_list = parse_contributor_list(get_contibutor_list(project,token))
print_list(contributors_list[1])
