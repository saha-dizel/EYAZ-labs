webSearch is lab 1. It works with Elasticsearch (ver. 7.4.2 was used, but 7.5, which is latest version as of today should totally work as well).

Dependencies: Gradle is working on all dependencies in code. You will also need Elasticsearch server to run the program. Can obtain for free on the official website. Port is 9200, which is default.

The program creates a new index page EVERY TIME it starts. So in order for it to work properly, you need to delete previously created index manually. You can use curl -X DELETE "localhost:9200/page?pretty" to do the job (assuming you are running server instance locally, of course, otherwise specify needed address), or you can modify the code to check the existence of an index every time.
