import requests
import json
import os
from dotenv import load_dotenv

# It's just for debugging purpose
def print_list(user_list : list, label : str):
	print(label)
	for element in user_list:
		print(element['username'],element['ranking'])
	print("=============================================")

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
	return parse_contributor_list(r.json())


# =============================================================
# THE MAIN PROGRAM STARTS HERE
# =============================================================

# Loads the .env file
load_dotenv()
token = os.getenv('USER_TOKEN')

if token != None:
	
	# Prints the Wrong Secrets contributors list
	wrongsecrets_list = get_contibutor_list('wrongsecrets',token)
	print_list(wrongsecrets_list[0],"Wrong Secrets top contributors:")
	print_list(wrongsecrets_list[1],"\nContributors:")
	
	# Prints the Wrong Secrets Binaries contributors list
	wrongsecrets_binaries_list = get_contibutor_list('wrongsecrets-binaries',token)
	print_list(wrongsecrets_binaries_list[0],"Wrong Secrets Binaries Top contributors:")
	print_list(wrongsecrets_binaries_list[1],"\nContributors:")
	
	# Prints the Wrong Secrets CTF contributors list
	wrongsecrets_ctf_list = get_contibutor_list('wrongsecrets-ctf-party',token)
	print_list(wrongsecrets_ctf_list[0],"Wrong Secrets CTF Top contributors:")
	print_list(wrongsecrets_ctf_list[1],"\nContributors:")

else:
	print("The variable USER_TOKEN  does not exists. You must setup the variable in the following way:")
	print("echo USER_TOKEN=github_pat_dfs13342gsfgsL4S4Q0CLbcVrIdsfz0gdN_f > .env")


