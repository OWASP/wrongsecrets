# CTF Instructions

So you want to play a CTF with WrongSecrets? This is the place to read up all about it.
Our CTF setup makes use of the [Juice Shop CTF CLI extension](https://github.com/juice-shop/juice-shop-ctf), which you
can read all about at [here](https://pwning.owasp-juice.shop/part1/ctf.html).

The difference between Juiceshop and WrongSecrets, is that WrongSecrets is more of a secrets-hunter game. Thiss means
that your contestants will try to find the CTF key soon after a few challenges. That is why we should separate out the
actual container for which the CTF scores are generated, from the container where the challenges live in.

You can see this practice already here in our repository: Our standard [Dockerfile](/Dockerfile) does not contain any
CTF entries, our Heroku [Dockerfile.web](/Dockerfile.web) does contain them.
So make sure you host your actual scoring Dockerfile.web at a place where your contestants cannot enter the container (
image) in order to extract the CTF key.

## Setting up CTFs

There are 3 flavors of CTF to be setup: Docker/Heroku, K8S, Cloud based.

### Docker or Heroku CTF

When doing a Docker or Heroku based CTF, you can follow
the [instructions in the readme](https://github.com/commjoen/wrongsecrets#ctfd-support).
If you want to use your own CTF key, you can build a container with the following
arguments `CTF_ENABLED=true,HINTS_ENABLED=false,CTF_KEY=<YOURNEWKEYHERE>`. Just make sure you provide the same key
to `juice-shop-ctf` when you run it.

Want to make it a little more exciting? Override the Dockerfile with your preferred values, so that copying from online
hosted solutions no longer works!

### K8s based CTF

TODO as #https://github.com/commjoen/wrongsecrets/issues/372

### Cloud based CTF 

TODO as #https://github.com/commjoen/wrongsecrets/issues/372

