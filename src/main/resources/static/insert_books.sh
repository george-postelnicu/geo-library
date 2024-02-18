 curl -v -d "@book1.json" -H "Content-Type: application/json" http://localhost:8080/api/books
 curl -v -d "@book2.json" -H "Content-Type: application/json" http://localhost:8080/api/books
 curl -v -d "@book3.json" -H "Content-Type: application/json" http://localhost:8080/api/books
 curl -v -d "@book4.json" -H "Content-Type: application/json" http://localhost:8080/api/books

 # TODO for loop in a file, and parse the output to see if the request is HTTP/1.1 201