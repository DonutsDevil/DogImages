# DogImages Library
## About 
It fetch dog images from the server and returns those images as asked.
- Can fetch single image at a time
- Can fetch mutliple image at a time parrallely

## Dependency
```
implementation 'com.github.DonutsDevil:DogImages:1.0.2'
```

## How to use the library
It requires a one time initialization, On Application start Initialize the library using 
```
ImageInit.getInstance(dogImageCallback())

// Listen to the state whether dog images have been fetched or not
private fun dogImageCallback() = object : Callback() {
        override fun onCompletion(state: Utility.Companion.UI_STATES, bitmap: Bitmap?, reason: String) {
           
        }
    }
```
## Methods
``getNextImage()`` Fetches the next imgae from server if a image is already not avialable <br />
``getPreviousImage()`` Fetches previous image which was pulled from the server, Also gives UI_STATE as IS_FIRST_IMAGE if previous image is not aviable<br />
``getImages(number: Int)`` Fetches number of images from the server and keep it loaded<br />

> **_NOTE:_** A [sample application](https://github.com/DonutsDevil/DogImages/tree/master/app) is made which shows how to use the library.



## Package Structure
```
com.example.network           # Root Package
.
├── interfaces                # Interfaces required by the library
|                             
|
├── networkManager             
│   ├── HttpCall              # Making the raw HTTP call.
│   └── HttpCallHandler       # Handle calls made to HttpCall
|   └── NetworkManager        # Passes information what to fetch to HttpCallHandler
|
├── utility                   # Utility Classes
|   │── Executor              # LiveData clss which notifies on internet connection status
|   │── JsonParser            # Used by Room to convert data and store
|
|── ImageInit                 # Class which need one time initialzation to use the library

```

## Architecture
<img src="https://github.com/DonutsDevil/DogImages/blob/master/resources/architecture.drawio.png"/>

- Once NetworkManager gets what needs to fetch from ImageInit, It makes a call to HttpCallHandler

- HttpCallHandler task is to tell HttpCall to make the server call, But in addition to that if any failure occured to fetch image it will retry 2 times more
  - Case when retry calls are made:
    - Response Code != 200
    - Image not found when making a server call
    - IOException
  - By Default all HttpCalls are on background thread.

- HttpCall task is to make just raw http call and return the jsonResponse got from the server.
