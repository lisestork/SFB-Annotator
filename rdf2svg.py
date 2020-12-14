#!/usr/bin/env python

import argparse as argp
import os

import matplotlib.pyplot as plt
import networkx as nx
import rdflib
from rdflib.namespace import (DC, DCTERMS, DOAP, FOAF, OWL, RDF, RDFS, SKOS,
                              VOID, XSD)

parser = argp.ArgumentParser(description='Plot RDF graph in SVG.')                                            
parser.add_argument('-i',
                    '--input',
                    help='Input in RDF/Turtle.',
                    type=str,
                    required=True)
parser.add_argument('-o',
                    '--output',
                    help='Output in SVG.',
                    type=str)
args = parser.parse_args()

ANNO = rdflib.Namespace("http://localhost:8080/rdf/nc/annotation/")
DWC = rdflib.Namespace("http://rs.tdwg.org/dwc/terms/")
NHC = rdflib.Namespace("http://makingsense.liacs.nl/rdf/nhc/")
DWCIRI =rdflib.Namespace("http://rs.tdwg.org/dwc/iri/")
OA =rdflib.Namespace("http://www.w3.org/ns/oa#")
DSW = rdflib.Namespace("http://purl.org/dsw/")
NC = rdflib.Namespace("http://makingsense.liacs.nl/rdf/nc/")
IMG = rdflib.Namespace("http://localhost:8080/semanticAnnotator/files/")
DCMITYPE = rdflib.Namespace("http://purl.org/dc/dcmitype/")

infile = args.input
basename = os.path.splitext(infile)[0]
outfile = args.output if args.output else basename + '.svg'
title = os.path.basename(infile)
print(outfile)
g = rdflib.Graph()
g.parse(infile, format='ttl')
g.bind("foaf", FOAF)
g.bind("rdf", RDF)
g.bind("dwc", DWC)
g.bind("dwciri", DWCIRI)
g.bind("dsw", DSW)
g.bind("nhc", NHC)
g.bind("dc", DC)
g.bind("oa", OA)
g.bind("dcterms", DCTERMS)
g.bind("nc", NC)
g.bind("img", IMG)
g.bind("dcmitype", DCMITYPE)
g.bind("anno", ANNO)

G = nx.Graph()
for (s, p, o) in g:
    G.add_node(s.n3(g.namespace_manager), group=s.n3(g.namespace_manager))
    G.add_node(o.n3(g.namespace_manager), group=o.n3(g.namespace_manager))
    G.add_edge(s.n3(g.namespace_manager), o.n3(g.namespace_manager), group=p.n3(g.namespace_manager))

pos = nx.drawing.layout.spring_layout(G, seed=1234)
plt.figure()
nx.draw_networkx_edge_labels(G, pos, edge_labels=nx.get_edge_attributes(G, 'group'), font_color='red', font_size=5)
nx.draw(G, with_labels=True, pos=pos, node_size=12, node_color='lightgreen', edge_color='gray', font_size=5)
plt.title(title)
plt.axis('off')
plt.tight_layout()
plt.savefig(outfile)
