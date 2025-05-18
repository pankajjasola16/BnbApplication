package com.airbnb.repository;

import com.airbnb.entity.City;
import com.airbnb.entity.Country;
import com.airbnb.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

// URL --> http://localhost:8080/api/v1/property/propertyresult?city=Ooty
// URL --> http://localhost:8080/api/v1/property/propertyresult?city=India
// above both will work --> means one search box can perform multiple searches

public interface PropertyRepository extends JpaRepository<Property, Long> {

    // First way of Building the Query

    // below we will built a Search Query
//    @Query("select p from Property p JOIN City c on p.city=c.id where c.name =:cityName")
//    List<Property> searchProperty(
//            @Param("cityName") String cityName
//    );

    // c.name =cityName --> this name (cityName) should come from here --> @Param("cityName")
    // @Param does --> whatever value supply here -->  String cityName --> Param
    // will replace that value here (cityName) --> c.name =cityName

    //                Another way of Building the Query --> this is better

    // select p from Property p --> means we are joining property table with city table -->
    //  why to join --> because city name is present
    // in city table --> and city name is taken from city table and --> searches is happening
    // in --> Property table so --> How to write Query --> see below:

    // INNER JOIN p.city c --> means we are joining the table p(Property p) with p.city

    // why p.city --> because this is the reference variable given in the Property entity(city)
    // --> (private City city) --> means you are telling that --> this Property table now
    // joining with the city reference variable (city reference variable means City table
    // which I will call it as --> c --> p.city  c
    // Property --> entity class

    // where c.name=:cityName
    // if we wil not use --> INNER --> it will still work:
    // "select p from Property p INNER JOIN p.city c where c.name=:cityName"

//    @Query("select p from Property p INNER JOIN p.city c where c.name=:cityName")
    // NOW IF I WANT TO SEARCH FOR CITY AND COUNTRY BOTH above one is only for city

//@Query("SELECT p FROM Property p INNER JOIN p.city c ON c.name = :name INNER JOIN p.country co ON co.name = :name")
    // OR SEE below query both are same
@Query("select p from Property p JOIN p.city c JOIN p.country co where c.name=:name or co.name =:name")
    List<Property> searchProperty(
//            @Param("cityName") String cityName
            @Param("name") String name
    );

// below code for finding duplicate of property
    @Query("SELECT p FROM Property p WHERE p.name = :propertyName AND p.city.id = :cityId AND p.country.id = :countryId")
    Optional<Property> findByPropertyNameAndCityIdAndCountryId(
            @Param("propertyName") String propertyName,
            @Param("cityId") Long cityId,
            @Param("countryId") Long countryId);

    // @Param("propertyName") --> above this --> "propertyName" --> should match with --> WHERE p.name = :propertyName

// @Param("cityName") --> here we will change --> @Param("name") --> because either it will
    // search based on name(city) or name(country)

    // SO ABOVE WE ARE Doing INNER jOIN  --> IT WILL join both Table

    //      EXACT DETAIL OF ABOVE 1

//    Can I Tell there is a relation between the Property table and city table
//    So can I tell city table  can be written down inside property table  see Proprty.java
//    so city reference variable is a part of Property table
//    so what we are doing above  SELECT   p   from Property  INNER JOIN p.city
//    <space>  c  means directly INNERJOIN
//    means join of two tables are done  now no need to write  ON    query
//    where c.name = :cityName

    //      EXACT DETAIL OF ABOVE 2

    // when you join two tables --> there should be --> one primary and foreign key
    // between these tables --> which means there is an RDBMS Concept between this table
    // what is RDBMS --> YOU are breaking two tables -> joining the tables --> based on
    // Primary key and foreign key
    // so just by writing INNER JOIN p.city --> directly join happens and then call that
    // table c --> p.city  c --> so joining between --> Property p and c  and then put a
    // condition

}


                      //select p from Property p
                      //JOIN City c on p.city=c.id
                      //where c.name =:cityName
//
        //Details for One way

//select p from Property p  
//give me All the variables (p) from  Property  p
//JOIN City c on p.city=c.id  
//We are joining this Property table (Property p) with 
//city table and city table Is called as  c  on 
//p.city = c.id 
//here p.city is a Foreign key(see in property table(city_id))  and joining
//this to primary key  c.id
//where c.name =cityName  in city table variable name is  c So c.name
//In short  Select p from property table (Property p)  Join this property table with 
//City c  based on the common columns (p.city = c.id) between  them (foreign key and
//Primary key)  where you need to search based on the  city name (c.name)  given
//In your parent table 
//Ex:
//        if I will give here  (c.name = name) Ooty  it will now become Ooty (c.name = name) 
//then get the id number  and based on that id number (p.city=c.id)  we will give you
//only those property (select p from Property p)  that belongs to  Ooty

// general  query:

//SELECT columns
//FROM table1
//JOIN table2
//ON table1.common_column = table2.common_column
//WHERE conditions;



// detail of this is in --> Lecture 24 --> page 63 --> JAVA-Pankaj-Sir-Feb-2






