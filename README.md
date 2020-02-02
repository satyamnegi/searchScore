## SELLICS SEARCH SCORE SOLUTION

### Project Requirements

* JDK 1.8.

* Spring-boot 2.2.4.RELEASE.

* Maven


Project is structured and grouped in a standard format.

#### How to run the application:

* Unzip the project or git clone [https://github.com/satyamnegi/searchScore.git](https://github.com/satyamnegi/searchScore.git).

* Open terminal.

* Go to the project directory:- cd score then do mvn clean install.

* Run the application:- cd target then do java -jar score-0.0.1-SNAPSHOT.jar.

* Hit the API:- [http://localhost:8080/estimate?keyword=iphone+charger](http://localhost:8080/estimate?keyword=iphone+charger)

* Cool, here's your response what you are looking for.

#### Assumptions Take:

* Firstly, i drilled down the search functionality of how Amazon's search works exactly and on what basis. This was important to derive my solution.

* Next, We have to strictly adhere to 10 seconds SLA. So, i thought to keep 9500ms for API calls to Amazon and 500ms for processing of service.

* Next, i thought how could i use response from API till the extent that i should get a closer parity with my response.

* Finally, i thought to drill down search by hitting API again & again till the stipulated time to get more closer analysis of data.


#### Algorithm:

* After lot of research and the amount of data which we are getting from Amazon API as response, i thought of going through the approach of splitting my input and searching them one by one in increasing order of split and yeah it worked as expected. So, let's take and example input to follow my approach.
* If input is iphone charger, then my obvious hits to the API were like:- [i, ip, iph, ipho, ..., iphone charger]. And to achieve this with required time i have used multi-threading logic.
* After getting all the required responses from the API, I have found the first matches of the input(ex-iphone charger) from total response of each call and based on that derived the estimated score.

#### Hint*

* When I actually done research in Amazon search bar, I have found that order has some sort of importance and thought of using the same to build my logic. For ex- if I type some keyword in search bar, I get the order based on the popularity of those items. And I have seen the important hint from input i.e. Amazon Sort/Return: Sort the Candidate-Set by search-volume and return the top 10 results. So, here's my approach.

#### Precision of outcome

* I definitely say that my outcome is not exact as Amazon's but yeah I am sure that it closely matches to correctness. I have used max from the response data by doing live calculations and based on my approach I feel this logic to give better outcomes.