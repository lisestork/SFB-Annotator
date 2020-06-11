import sys
import json

from urllib.parse import urlparse, unquote

url=sys.argv[1]
o=urlparse(url)
s=unquote(o.query).replace("annotation=","")

print(json.dumps(json.loads(s), indent=2))
