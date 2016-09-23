# ImageThief

A small Android application that satisfies the following minimum requirements:
- has an activity with the text input that allows the user to enter a URL of an image on the internet
- application should download the image, Android service should be used for the downloading
- don't use any 3rd party image loading libraries like Picasso
- once downloaded, store the image on device and display it on screen rotated 180 degrees
- image rotation should be performed on the actual image data


Note
- I used HttpUrlConnection for http request just because it was more simple for this sample app. But I prefer uring third-party applications such as OkkHttp(and Retrofit) or Volley in real application.
- Observable Pattern whitch I use in this sample application is primitive, just because making application more simple. But I prefere Agera in real applications.
- I used SharedPreference for saving data just because making application more simple. we should use db in real app.
- Testing is really basic because I didn't have enough time for that.

 
TODO
- Use render -script for rotating image
- resolving dick-cache bug
- more test case
