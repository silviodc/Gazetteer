# SWI: A Semantic Web Interactive Gazetteer to support Linked Open Data

Project to develop a Collaborative Gazetteer for Biodiversity data. This repository will be used to store .java files to be used in the background of the mentioned Gazetteer.

## What it does:

1. Read Biodiversity files from large csv file formats. 
```
    1.1 Clear the inaccurate data, i.e, removal of data entries that do not have, county, place, latitude, longitude and name.
    1.2 Count the number of data regarding its specification. (county, place, latitude, longitude and name.)
```    
2. Read external data sources like Geonames, DBpedia and use it to improve the Biodiversity records
```
    2.1 We are using downloaded Geonames files** and requesting data from DBpedia using SPARQL
    2.2 These informations are aggregated in our biodiversity files.
```
3. Cluster all Biodiversity data 
```
    3.1 By default we are using jaccard similarity string.  The other methods still lacking comparison 
    3.1 We are using Kmeans, and single link algorithm to cluster this data
    3.2 To do so, we are applying this workflow:
    for all Biodiversity entries:
	    1) build small groups of data by:
	   		1.1) Associating the rdf labels in our ontology to biodiversity data.
	   		1.2) Remove stopwords
	   		1.3) Verify if the municipality mentioned is the same of IBGE or wasn't informed
	   	2) Cluster the data
 ```
4. Improve the Biodiversity data! 
```
    4.1 We developed a method to summarize the geographical coordinates, like Linus' Law (How much more places has the same record, more right it will be)
```
5. Count the number of data that were improved
```
    5.1 Time series counting regarding how many data have coordinate before and after SWI Gazettteer .
```
6. Mapping the data to ontology
 ```
  6.1 Create subject, predicate and object for each biodiversity data.
  6.2 Mapping geographical coordinates to GeoSPARQL using WTKLiteral
```
## Paper:
```
@article{CARDOSO2016389,
title = "SWI: A Semantic Web Interactive Gazetteer to support Linked Open Data",
journal = "Future Generation Computer Systems",
volume = "54",
number = "Supplement C",
pages = "389 - 398",
year = "2016",
issn = "0167-739X",
doi = "https://doi.org/10.1016/j.future.2015.05.006",
url = "http://www.sciencedirect.com/science/article/pii/S0167739X15001818",
author = "Silvio D. Cardoso and Flor K. Amanqui and Kleberson J.A. Serique and JosÃ© L.C. dos Santos and Dilvan A. Moreira",
keywords = "Semantic Web, Volunteered geographic information, Gazetteer"
}
```

## Dependencies

Case you have to include new dependencies, please use the pom.xml and refer to the MAVEN_README.md file in [/files] to do it smoothly ;)

## Example

The files in the package br.usp.icmc.gazetteer.Test are used as main example of the project.

## Using

Before running the SWI Gazetteer, you have to configure the Archives_location.xml file in [files/configFiles/] with the necessary information:
 
```
<repository>  Indicates a new data repository
<name> speciesLink </ name>  repository name
<filepath>  CSV that contains information about localities
<ColumDate>  Date Information
<ColumPlace> Name Place
<ColumCounty> County, municipality
<ColumLati> >>> Latitude (WGS84 eg -2.9421)
<ColumLong> >>> Longitude (WGS84 eg -2.9421)
<ColumPoly> >>> Polygon that contains informations about (e.g. Amazonas State)
```    
For more details please, open an issue!
