# List of Endpoints

## Worker initialization

Used to set the endpoint that is used by the worker to send back the results.  
Endpoint: `/init`  
Parameters:

- `endpoint`: The endpoint the worker should send results to
- `port`: The port of the webserver the worker should send results to

Returns the following json equivalent to the `WorkerInformation` class:

```
{
    "name": "NAME",
    "model": "MODEL",
    "maxQueueSize": MAX_QUEUE_SIZE,
    "currentQueueSize": CURRENT_QUEUE_SIZE
}
```

Possible Status Codes:

- `400` Bad Request: If the endpoint or port parameter is not present
- `500` Internal Server Error: If there was an error initializing the client

## Worker information

Used to get information like name, model, current queue size and maximum queue size from the worker.  
Endpoint: `/info`  
Returns the following json equivalent to the `WorkerInformation` class:

```
{
    "name": "NAME",
    "model": "MODEL",
    "maxQueueSize": MAX_QUEUE_SIZE,
    "currentQueueSize": CURRENT_QUEUE_SIZE
}
```

## Adding IWorkerAudioRequests

Used to add a new IWorkerAudioRequest to the internal queue of the worker.  
Endpoint: `/request`
Possible Status Codes:

- `400` Bad Request: If the parsing of the `IWorkerAudioRequest` fails or if not all preprocesses are available
- `503` Service Unavailable: If the queue is full  
