Gazetteer
=========
Project to development a Collaborative Gazetteer for Biodiversity data. This repository will be used to store .java files about Gazetteer that can be edited and updated for other persons.
This project has the following features:

1- Read all Biodiversity files that contains data about harvest and places. 

    1.1 Clear the inaccurate data, i.e, remove data that not has important information, e.g., county, place, latitude ...

    1.2 Count the number of inaccurate data and build maps to shows them

    1.3 Count the number of data that has information about place, latitude, longitude...

    1.4 Split the data count held in step 1.3 in after and before of GPS use by biologists.
    
2- Read source Data like Geonames, Wikimapia, DBpedia and use your data to improve the Biodiversity records used in step1

    2.1 Build a java client to connect with Wikimapia and download your data (It is need create a Wikimapia user)

    2.2 Download Geonames and DBpedia data in RDF

      2.2.1 Create a method for read and store this data

3- Cluster all Biodiversity data and resolve toponymy (using Geographical Information Retrieval tecniques)

    3.1 We are using Star algorithm and jaccard similarity coeficient to cluster this data

    3.2 Together with step 3.1 we are using a stop word list to remove words like (solo arenoso, terra firme...)
   
4- Improve the Biodiversity data! 
    
    4.1 In this step we are using the data downloaded in step 2 to improve Biodiversity data, find what data has more similarity between Biodiversity data and Geonames, Wikimapia, DBpedia data.
    
    4.2 Create a method to summarize the geographical coordinates, like Linus' Law (How much more places has the same record, more right it will be)
   
5- Count the number of data that were improved
    
    5.1 Count how many data that have has information about latitude, longitude were recovered.
    
    5.2 Count how many data that have has geographical information recovered and were much old, i.e., Before use of GPS by biologists
    
    5.3 Calculate the T student test using the results of step 5.1 and 5.2 to see how much  significant was our approach
   
6- Mapping the data to ontology
    
    6.1 Create subject, predicate and object for each biodiversity data.
    
    6.1 Mapping geographical coordinates to GeoSPARQL

7- Store the data in one Triple Store and verify the results
    
    7.1 Build a base line that contain information about which query represent a place (using 60 places)
    
    7.2 build the semantic queries
    
    7.3 Put all queries in the Triple Store and verify the precision, recall and F1 measure
   
8- Use the same places in step 7 and verify the occurence this data in DBpedia and Geonames
    
    8.1 Build a base line that contain information about which query represent a place in DBpedia and Geonames 
    
    8.2 Build the semantic queries for both repositories
    
    8.3 Put all queries in the Triple Store and verify the precision, recall and F1 measure
  
9- Development a Interface that enables Biologists insert data within Gazetteer
    
    9.1 Use the Google Maps API to show the map
    
    9.2 Create a view that enables Biologists, see and improve the missing data!
    
    9.3 Allow Biologists insert places using TAGS
    
      9.3.1 Cluster the data inserted and improve the Biodiversity data similar.
    
    9.4 Allow Biologists insert link from DBpedia source, when it exist.   
    
    9.5 Create a view that enables Biologists agree with geographical coordinates and Links about DBpedia inserted in the Gazetteer
  
10- Verify the number of place inserted by users and quality from Data
    
    10.1 Verify the average of users that agree with the geographical coordinates
    
    10.2 Verify the average of users that agree with the Linked Data in Gazetteer
    
    10.3 Repeat the step 5


Architecture from Project
