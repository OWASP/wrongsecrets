#!/usr/bin/python
# -*- coding: utf-8 -*-

import json

from github import Github

# authors.py contains a dict with a mapping:
# authors["b-mueller"] = "Bernhard Mueller"
from authors import *

g = Github()
repo = g.get_repo("OWASP/owasp-mstg")
stats = repo.get_stats_contributors()
