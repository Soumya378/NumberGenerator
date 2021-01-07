# NumberGenerator

The port and the values for the min time and max time range are defined in the application.properties

For each of the HTTP request the uuid and results are stored in a hashmap.

The URLS  are as below :-
POST- /api/generate   ----> http://localhost:2021/api/generate

POST - /api/bulkGenerate  ---> http://localhost:2021/api/bulkGenerate

GET - /api/tasks/{UUID of the task}/status    ---> http://localhost:2021/api/tasks/{UUID}/status

GET - /api/tasks/{Task UUID of the task}?action=get_numlist  ---> http://localhost:2021/api/tasks/{UUID}?action=get_numlist


FOr the BUlk Generate to optimize to make it wor parallely --- (In Progress)
1. We can use CompletableFuture to get all the tasks done in parallel.
2. Executor's framework can be used to run all the future tasks - Fork Join pool
