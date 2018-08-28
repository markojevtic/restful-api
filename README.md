# Example of design restful api

## The case

An issuer can create a tender describing the work that needs to be
done. For which a bidder can hand in one or more offers. The issuer
can then accept out of the given offers only one that suits best. All the other offers are getting
rejected immediately. Once an offer got accepted, it cannot be rejected anymore.

#### Tender story
As an user of API, I want create tender for an Issuer to describe work that should be done. 
Also API user has possibility to query tenders by issuer id.

###### Acceptance criteria 

_A. An API user can create a new tender_
```
Given a new tender
    with issuer id 1
    and description 'Build the house'
When client POST the tender
Then the tender is stored in system
    And the result has status 204        
```

_B. An API user can query tender by issuer id_  
```
Given an existing tender
    with issuer id 1
    and description 'Build the house'
    and another existing tender 
    with issuer 2
    and description 'Repair the house'
When client query API by issuer id 1
Then result has status 200 
    and contains anly tenders with issuer id 1    '    
```

_C. An API user cannot create a tender that doesn't have issuer id_
```
Given a new tender
    with no issuer id
    and description 'Build the house'
When client POST the tender
Then result has status 400    
```

#### Offer story
As an API user, I should be able to create a offer for an existing tender. The user should be able to 
get all offers for a tender, get all offers for a bidder, and filter by both.

###### Acceptance criteria 

_A. an API user can create a new offer_
```
Given an existing tender
    and a new offer for the tender
    with bidder id 1
    and description 'Offer text'
When client POST the offer
Then the offer is stored in system
And then result has status 204        
```

_B. an API user cannot create a offer if there is no tender_
```
Given a new offer for the tender
    with bidder id 1
    and description 'Offer text'
When client POST the offer
Then result has status 400        
```

_C. an API user cannot create a new offer with no bidder id_
```
Given an existing tender
    and a new offer for the tender
    and description 'offer text'
When client POST the offer
Then result has status 400        
```

_D. an API user can query offer by bidder id_  
```
Given an existing tender
    with an offer by bidder 1
    and another offer by bidder 2
and another existing tender 
    with an offer by bidder 1    
When client query API by bidder id 1
Then result has status 200 
    and contains only offers with id 1
```

_E. an API user can query offer by tender id_  
```
Given an existing tender
    with an offer by bidder 1
    and another offer by bidder 2
and another existing tender 
    with an offer by bidder 1    
When client query API by second thender
Then result has status 200 
    and contains only offers for the second tender
```

_F. an API user can query offer by tender id_  
```
Given an existing tender
    with an offer by bidder 1
    and another offer by bidder 2
and another existing tender 
    with an offer by bidder 1    
When client query API by first thender
Then result has status 200 
    and contains only offers for the second tender
```

#### Accept a offer story
As an API user(Issuer), I should be able to accept an offer for my existing tender. Once when one offer is accepted
all other offer for my story should be declined.

###### Acceptance criteria 

_A. an API user(Issuer) can accept an offer_
```
Given an existing tender
    with an offer with id 1
    and another offer with id 2
When issuer accept offer with id 1
Then the offer has status accepted
    And all other have status declined
    And the result have status 200        
```

_B. an API user cannot accept a not new offer_
```
Given an existing tender
    with an accepted offer with id 1
    and an declined offer with id 2
When issuer accept offer with id 2
Then result has status 400        
```

#### Finalization story
Documentation improvement, tests, manual testing, etc.
     
## Technology stack overview

Application is based on spring boot framework, the main reasons why we use it:
- nowadays it's the most popular java framework, and it has more than  2 millions developers
- it encourages/leads developers to create an application with a clear architecture
- fast development and support for all mainstream technologies( dbs, queues, etc)

In order to speed up writing code and get rid of boiler plate code we use lombok, and because of that you will need lombok plugin
for your IDE.

For building application we use maven.

### Architectural overview
We tried to keep boundary/modules isolated as much as it possible, and every boundary has three layers: 
* resource: contains rest api definition.  
* service: contains services/logic definition
* repository: layer that cares about storing data

Code/package organisation is done by layers, it means that the base package is `com.github.markojevtic.restfulapi`, and subpackages
  * com.github.markojevtic.restfulapi.resource - resource layer classes ( controllers, converters, dtos )
  * com.github.markojevtic.restfulapi.service - service layer classes ( services )
  * com.github.markojevtic.restfulapi.repository - repository layer classes ( repositories and entities )

There was an option to define package by module/boundary and then by layer, but it approach there would only complicate navigation,
and did not bring any benefit. Package by module approach is more suitable when we have more modules and more complex module whit multiple services. 
 
#### Rest

To define RESTful API we use spring-hateos, it allows as to defined a navigable API. Documenting API is done with swagger.
In case of real-life RESTful api, every resource has to support all verbs(GET,POST,PUT,DELETE), but here we follow YAGNI method,
and implement only methods that was specified in requirements.

###### Exception handling
We use exception handler(@ControllerAdvaice) to provide better exception to http status mapping, 
and put to payload an error message with more useful information, therefor consumers of our API can better understand  what is wrong. 

###### Using DTO
We use DTO objects it gives us many benefits: API versioning, protecting some sensitive
data that we have in entities, etc. To do conversion between entities and DTOs we relay on spring conversion service, 
and our converters. Every converter do two things:
    * maps fields from entity to DTOs and vice versa . 
    * add relevant links to DTOs

#### Service
Service layer is implemented in "Spring way", every service has interface and implementation.

#### Repository level
DB is a detail, and it should not have impact on app architecture, and decision which database to use it should be postponed as far as it possible. Here I've decided
to do not burden myself with that detail. To get data from a database we use spring repository interfaces.

In case of the model, we follow KISS principle and we keep number of properties small as it is possible.

### Implementation overview

#### Tender API

In order to provide API to handle create and querying Tenders, we implemented TendersResource a rest controller,
and mapped it into "/tenders". To make it functional we've introduced TenderService, and TenderResource. To see 
API documentation please take a look [swagger docs](localhost:8080/swagger-ui.html) tender-resource section. 

#### Offer API
The offer API handles creating offer for an open tender, and querying offers by tender and bidder.We have implemented
that in the standard way, rest layer has been implemented in OfferResource, service is defined by interface OfferService 
and implemented in OfferServiceImpl, the repository is a spring JPA repository defined in OfferRepository. The offer
module has a dependency on module Tender. We need cause we create offer only if tender is biddable.

##### Accept an Offer API 
To provide API for accepting an offer by side of issuer, I exposed a sub-resource of offer "/offers/{offerId}/accepted" with 
verb POST. It is defined as a method in class OfferResource. The rest controller calls the service layer mehtod,
which load target offer, validate does offer have status NEW, and then accept them, decline all other offers 
for the tender, and then call TenderService in order to close this tender for new bidding.