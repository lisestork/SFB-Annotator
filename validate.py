#!/usr/bin/env python

import sys
import json

from tripoli import IIIFValidator


json_file = sys.argv[1]

with open(json_file) as fh:
    data = json.load(fh)
    iv = IIIFValidator(fail_fast=False, verbose=True)
    iv.validate(data)

