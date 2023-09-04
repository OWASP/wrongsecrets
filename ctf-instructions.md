# CTF Instructions

So you want to play a CTF with WrongSecrets? This is the place to read up all about it.
Our CTF setup makes use of the [Juice Shop CTF CLI extension](https://github.com/juice-shop/juice-shop-ctf), which you can read all about at [here](https://pwning.owasp-juice.shop/companion-guide/snapshot/part1/ctf.html).

The difference between Juiceshop and WrongSecrets, is that WrongSecrets is more of a secrets-hunter game.
This means that your contestants will try to find the CTF key soon after a few challenges.
That is why we should separate out the actual container for which the CTF scores are generated, from the container where the challenges live in. We call this the 3-domain setup where you now have 3 environments:

- the play-environment: here players can just play with WrongSecrets: this can be something you host online, or just a Docker container they start up locally.
- the CTF-scoring-environment: this is the intermediary domain where people exchange answers found in the 'play-environment' for actual flags for the CTF-platform.
- your CTF-platform: this can be a platform like CTFD or FBCTF.

You can see this practice already here in our repository: Our standard [Dockerfile](/Dockerfile) does not contain any CTF entries, our Heroku [Dockerfile.web](/Dockerfile.web) does contain them.
So make sure you host your actual scoring Dockerfile.web at a place where your contestants cannot enter the container (image) in order to extract the CTF key.

## Want to get rid of the additional domain?

Want to make sure you don't need to bug your users to copy paste values twice to get points? Here we describe the "2-domain setup". With the 2-domain setup you need to do a manual crafted approach instead of the HMAC based approach for platforms like CTFD. That way, you do not need the 'CTF-scoring-environment' to exchange answers for flags, for this you:
- Follow the steps described at [instructions in the readme](https://github.com/OWASP/wrongsecrets#ctfd-support).
- Then unzip the created zip file and update all the flags in flags.jsson with the actual values of the answers for your CTF.
- Zip the json files again.
- Upload your own crafted zipfile with the actual answers, instead of HMACs to CTFD.

Now users can directly use your Wrongsecrets setup together with the CTF-platform to play challenges without having to copy answers and flags twice!

Note: make sure that you do set `CTF_SERVER_ADDRESS` to point to the address where you are running your CTF-platform (E.g. CTFD/Facebook CTF) and that you set `challenge_acht_ctf_to_provide_to_host_value` and `challenge_thirty_ctf_to_provide_to_host_value` to the flag you store in your CTF-platform.

## Setting up CTFs

There are 3 flavors of CTF to be setup: Docker/Heroku, K8S, Cloud based.

### Docker or Heroku CTF

When doing a Docker or Heroku based CTF, you can follow the [instructions in the readme](https://github.com/OWASP/wrongsecrets#ctfd-support).
If you want to use your own CTF key, you can build a container with the following arguments `CTF_ENABLED=true,HINTS_ENABLED=false,CTF_KEY=<YOURNEWKEYHERE>`. Just make sure you provide the same key
to `juice-shop-ctf` when you run it.
Host the Docker container somewhere, where your users can not access the container variables directly, so they cannot extract the CTF key that easily.
Want to make it a little more exciting? Create your own custom Docker image for both the 'play-environment' and the 'CTF-scoring-environment', where you override certain values (e.g. the ARG, the docker ENV, etc.) with your preferred values, so that copying from any existing online solution no longer works!
There are a few env-vars that you need to pay attention to when setting this up:
- `CTF_SERVER_ADDRESS` in the 'play-environment' to be set to the URL of the 'CTF-scoring-environment' (e.g. your instance of wrongsecrets-ctf.herokuapp.com), and in the 2-domain approach that would be your CTF-platform. Note that in the domain where your users exchange answers for flags for your CTF-platform, you can set it to the URL where your CTF-platform lives.
- `challenge_acht_ctf_to_provide_to_host_value` and `challenge_thirty_ctf_to_provide_to_host_value` need to be set to a sufficiently long value at the 'play-environment' where your players interact with WrongSecrets to hack around. The value of this entry is returned to the players when they have found the randomly generated value in the logs. If you have the 2-domain approach: make sure that this value is actually the flag-entry for challenge 8 in your CTF-platform, if you have the normal setup, make sure that your 'CTF-scoring-environment' where people provide answers in exchange for flags has the same value stored under `challenge_acht_ctf_host_value`.
- `challenge_acht_ctf_host_value` needs to be set in your 'ctf scoring environment' where players exchange answers for CTF flags to the same value as `challenge_acht_ctf_to_provide_to_host_value` in the environment players play around. Note that this value is not required in a 2-domain approach.

### K8s based CTF

If you are interested in setting up a Kubernetes based CTF, you might want to look at [WrongSecrets CTF party](https://github.com/OWASP/wrongsecrets-ctf-party) instead. Still want to take a different approach than using that? Please read the rest of the paragraph.

When you want to enable the Kubernetes challenges in your CTF-environment, make sure your 'play-environment' is actually running in a Kubernetes environment where the K8ss Configmap, K8s secret, and optionally the Vault setup, are configured correctly. See [our k8s folder](/k8s/) as an example, or have a look at our [Okteto](/okteto/) setup for just having the K8s & Configmap challenges supported.
When you take the 2-domain approach, make sure that the decoded K8S Secret entry and the Configmap value are stored correctly in the CTF-platform. If you take the standard HMAC approach instead, make sure that your CTF-scoring-environment has the following environment variables set:

- `SPECIAL_K8S_SECRET` which should be set to the value stored in your K8S Configmap
- `SPECIAL_SPECIAL_K8S_SECRET` which should be set to the value of your K8S Secret.
- `vaultPassword`  (optionally when having vault setup for your players) which should be set to the value stored inside Vault for challenge 7.

### Cloud based CTF

If you are interested in setting up a Cloud-based CTF in AWS, you might want to look at [WrongSecrets CTF party](https://github.com/OWASP/wrongsecrets-ctf-party) instead. Still want to take a different approach than using that? Please read the rest of the paragraph.

When you take the 2-domain approach, make sure that the decoded K8S Secret entry and the Configmap value are stored correctly in the CTF-platform, next: make sure that the values used for Challenge 9,10 & 11 are stored there correctly as well.

Note: if you want to support challenge 11 at your CTF: make sure players don't share the same cloud-account together, or make sure that the privilege escalation path can only be done to the given account described in the challenge code and not to a role/user with more administrative access, as this would allow your players to wreak havoc to your CTF setup. We rather recommend disabling challenge 11 in your CTF setups.

If you take the 3 domain setup, make sure the following values are configured in your CTF-scoring-environment:

- `default_aws_value_challenge_9` set to the value of the secret generated for challenge 9. Don't be fooled by the name, as this will work for AWS/GCP/Azure.
- `default_aws_value_challenge_10` set to the value of the secret generated for challenge 10. Don't be fooled by the name, as this will work for AWS/GCP/Azure.
- `default_aws_value_challenge_11` (Optionally, when you have separated cloud accounts or took care of permissiosn boundaries) set to the value of the secret generated for challenge 11. Don't be fooled by the name, as this will work for AWS/GCP/Azure.
