#!/usr/bin/env python

import argparse as argp
import os

import matplotlib.pyplot as plt
import networkx as nx
import rdflib
from rdflib.namespace import (RDF, RDFS, FOAF, DCTERMS)

# CLI
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
infile = args.input
basename = os.path.splitext(infile)[0]
outfile = args.output if args.output else basename + '.svg'
title = os.path.basename(infile)
print(outfile)

# set namespace prefixes
ANNO = rdflib.Namespace("http://localhost:8080/rdf/nc/annotation/")
DCMITYPE = rdflib.Namespace("http://purl.org/dc/dcmitype/")
DSW = rdflib.Namespace("http://purl.org/dsw/")
DWC = rdflib.Namespace("http://rs.tdwg.org/dwc/terms/")
DWCIRI = rdflib.Namespace("http://rs.tdwg.org/dwc/iri/")
GN = rdflib.Namespace("http://sws.geonames.org/")
GBIF = rdflib.Namespace("http://www.gbif.org/species/")
IMG = rdflib.Namespace("http://localhost:8080/semanticAnnotator/files/")
OA = rdflib.Namespace("http://www.w3.org/ns/oa#")
OBO = rdflib.Namespace("http://purl.obolibrary.org/obo/")

# populate RDF graph
g = rdflib.Graph()
g.parse(infile, format='ttl')
g.bind("anno", ANNO)
g.bind("dcterms", DCTERMS)
g.bind("dcmitype", DCMITYPE)
g.bind("dsw", DSW), FOAF
g.bind("img", IMG)
g.bind("oa", OA)
g.bind("obo", OBO)
g.bind("rdf", RDF)
g.bind("rdfs", RDFS)

# plot the graph
G = nx.DiGraph()
for (s, p, o) in g:
    G.add_node(s.n3(g.namespace_manager), group=s.n3(g.namespace_manager))
    G.add_node(o.n3(g.namespace_manager), group=o.n3(g.namespace_manager))
    G.add_edge(s.n3(g.namespace_manager), o.n3(g.namespace_manager),
               group=p.n3(g.namespace_manager))
pos = nx.drawing.layout.spring_layout(G, iterations=50)
plt.figure()
nx.draw(G, pos, with_labels=True, node_shape='o', node_size=10,
        node_color='lightblue', edge_color="gray", width=0.2, font_size=4)
nx.draw_networkx_edge_labels(G, pos,
                             edge_labels=nx.get_edge_attributes(G, 'group'),
                             font_color='red', font_size=4)
plt.title(title)
plt.axis('off')
plt.savefig(outfile)
